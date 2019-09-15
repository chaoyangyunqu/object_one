package com.shineyue.certSign.controller;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.SignCallbackDTO;
import com.shineyue.certSign.utils.ConvertUtil;
import com.shineyue.certSign.utils.IpAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/15
 */
@RestController
public class TestController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "CA/ca/dealSignCallbackPicOfPDFTest.serivce",method = RequestMethod.POST)
    public DataResult dealSignCallbackPicOfPDF(@RequestBody SignCallbackDTO signCallbackDTO, HttpServletRequest request){

        DataResult dataResult = new DataResult();

        logger.info("收到来自[{}]的请求", IpAddressUtil.getIpAddress(request));
        try {
            logger.info("证书回调信息:{}",signCallbackDTO.toString());
            // 文件存放路径
            String basePath = System.getProperty("user.dir") + "/signPDF";
            String wqhth = signCallbackDTO.getWqhth();
            // 设置文件名
            String fileName = "/商品房网签合同(个人签署)" + wqhth + ".pdf";
            String filePath = basePath + fileName;
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                // 不存在则新建文件夹
                dest.getParentFile().mkdirs();
            }
            ConvertUtil.base64StringToFile(signCallbackDTO.getInputPDF(),filePath);
        } catch ( Exception e ) {
            dataResult.setStatus(200001);
            dataResult.setMsg("请求失败!");
            dataResult.setError(e.getMessage());
        }
        return dataResult;
    }

}
