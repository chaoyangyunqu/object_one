package com.shineyue.certSign.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.SignContractDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/6
 */
@Slf4j
public class HttpConnetUtils {
    public static DataResult httpConnet(SignContractDTO signContractDTO,String url, String dataJsonStr){
        DataResult dataResult = new DataResult();
        HttpURLConnection httpConnection = null;
        String msg = "";
        log.info("{}远程连接中，请稍后...",url);
        try {
            URL targetUrl = new URL(url);

            httpConnection = (HttpURLConnection) targetUrl
                    .openConnection();
            log.info("{}远程已连接，开始进行操作...",url);
            // 超时设置
            httpConnection.setConnectTimeout(60000);
            httpConnection.setReadTimeout(60000);
            // 允许写操作
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type",
                    "application/json");
            OutputStreamWriter paramout = new OutputStreamWriter(
                    httpConnection.getOutputStream(), "UTF-8");
            paramout.write(dataJsonStr);
            paramout.flush();
            paramout.close();
            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpConnection.getResponseCode());
            }

            String resStr = "";
            msg = signContractDTO.getSubject();
            resStr = IOUtils.toString(httpConnection.getInputStream(), StandardCharsets.UTF_8);
            log.info("{}远程结束中，开始进行返回数据处理...",url);
            String resStrData = ConvertUtil.decodeStr(resStr.trim().replace(' ','+'));
            JSONObject resJson = JSON.parseObject(resStrData);
            if (resJson.getString("SUCCESS").equals("FALSE")) {
                String failedReson = resJson.getString("REASON");
                log.info("{}电子签章操作失败，原因如下:",msg);
                log.info(failedReson);
                dataResult.setStatus(200003);
                dataResult.setMsg("CA远程签章失败");
                dataResult.setError("失败原因:该证书序列号"+failedReson);
            }

            if (resJson.getString("SUCCESS").equals("TRUE")) {
                log.info("{}电子签章成功",msg);
                String res = resJson.getString("BASEDATA");
                signContractDTO.setInputPDF(res);
                dataResult.setStatus(100001);
                dataResult.setMsg("请求成功");
                dataResult.setSuccess(true);
                dataResult.setResults(signContractDTO);
            }
            httpConnection.disconnect();
            log.info("已完毕连接");
            return dataResult;
        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("请求失败");
            dataResult.setError("失败信息:"+e.getMessage());
            return dataResult;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

}
