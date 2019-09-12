package com.shineyue.certSign.demo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.shineyue.certSign.utils.ConvertUtil;
import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SealPDFPushDataWebService {

    public static final String BASE_URI = "https://xjcasite:1449/services/PDFSign/PushPDFData/";

//	 public static final String BASE_URI =
//	 "http://192.168.117.19:8088/SealPic_WebService/services/PDFSign/PushPDFData/";

    public static void main(String[] args) {

        System.out
                .println("**************************PushPDFData的开始操作*******************");
        System.out.println(new Date());
        String base64String = ConvertUtil.getPDFBinary(new File(
                "D:\\Work\\Projects\\电子签名\\合同\\阿勒泰商品房预售合同2019年8月16日数据.pdf"));
        // UserInfo 签署人信息
        // SignInfo 签署信息
        // EntInfo 企业信息
        // token  token值
        // callbackUrl 回调地址
        String dataJsonStr = "{'inputPDF':'"
                + base64String.trim()
                + "','UserInfo':{'name':'张三','phone':'15276533512','idCard':'650100199001010123'},'SignInfo':{'PageNums':'1#2','picSizes':'(100,100)','picPoints':'(320,470)|(321,280)#(322,100)'},'token':'9e477b99-f958-4688-2fc6-ccf670861265','EntInfo':{'entCode':'12877592704008267040728012710','entName':'阿勒泰华丽房地产开发有限公司01440150'},'callbackUrl':'ad'}";
        try {
            URL targetUrl = new URL(BASE_URI);

            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl
                    .openConnection();
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

            resStr = IOUtils.toString(httpConnection.getInputStream(),
                    StandardCharsets.UTF_8);
            String resStrData = ConvertUtil.decodeStr(resStr.trim().replace(
                    ' ', '+'));
            System.out.println(resStr);
            JSONObject resJson = JSON.parseObject(resStrData);
            if (resJson.getString("SUCCESS").equals("FALSE")) {
                String FailedReson = resJson.getString("REASON");
                System.out.println(FailedReson);
            }

            if (resJson.getString("SUCCESS").equals("TRUE")) {
                System.out.println("数据推送成功");
            }
            httpConnection.disconnect();
            System.out.println(new Date());
            System.out
                    .println("**************************PushPDFData的结束操作*******************");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
