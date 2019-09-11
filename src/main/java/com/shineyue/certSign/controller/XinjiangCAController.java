package com.shineyue.certSign.controller;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.SignContractDTO;
import com.shineyue.certSign.model.vo.PicBindCertCNVO;
import com.shineyue.certSign.service.impl.XinjiangCAServiceImpl;
import com.shineyue.certSign.utils.IpAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @PackageName: com.shineyue.certSign.controller
 * @Description: TODO 签章接口
 * @author: 罗绂威
 * @date: wrote on 2019/8/28
 */
@RestController
public class XinjiangCAController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    XinjiangCAServiceImpl xinjiangCAService;

    @PostMapping(value = "CA/ca/getPicOfPDF.serivce")
    public DataResult test(@RequestBody SignContractDTO signContractDTO, HttpServletRequest request){

        DataResult dataResult = new DataResult();

        logger.info("收到来自[{}]的请求", IpAddressUtil.getIpAddress(request));
        try {
            String serial = "" ;
            serial = signContractDTO.getSerial();
            logger.info("证书序列号:{}",serial);
            if ("".equals(serial)) {
                dataResult.setMsg("未检查到证书序列号");
                dataResult.setError("请求参数错误");
                return  dataResult;
            }
            dataResult = xinjiangCAService.getPicOfPDF(signContractDTO);

        } catch ( Exception e ) {
            dataResult.setError("请求参数错误");
        }
        return dataResult;
    }

    @PostMapping(value = "CA/ca/picOfPDFTest.serivce")
    public DataResult getPicOfPDF(@RequestBody MultipartFile file , SignContractDTO signContractDTO ) {

        DataResult dataResult = new DataResult();
        try {
            if (file.isEmpty()) {
                dataResult.setError("文件为空，请重新选择文件！");
                return dataResult;
            }
            logger.info(file.getContentType());
            // image/png
            if (!"application/pdf".equals(file.getContentType())) {
                dataResult.setError("文件类型不支持，请上传PDF文件！");
                return dataResult;
            }
            logger.info("文件验证通过");
            String serial = "" ;
            serial = signContractDTO.getSerial();
            logger.info("证书序列号:{}",serial);
            if ("".equals(serial)) {
                dataResult.setMsg("未检查到证书序列号");
                dataResult.setError("请求参数错误");
                return  dataResult;
            }
            dataResult = xinjiangCAService.getPicOfPDFTest(file,signContractDTO);


        } catch ( Exception e ) {
            dataResult.setMsg("控制层捕获异常");
            dataResult.setError(e.getMessage());
        }
        return dataResult;

    }

    @PostMapping(value = "CA/ca/getCertSN")
    public DataResult getCertSN(@RequestBody MultipartFile file , PicBindCertCNVO picBindCertCNVO) {
        DataResult dataResult = new DataResult();

        try{
            if (file.isEmpty()) {
                dataResult.setMsg("文件为空，请重新选择文件！");
                return dataResult;
            }
            logger.info(file.getContentType());

            if (!"image/png".equals(file.getContentType())) {
                dataResult.setMsg("文件类型不支持，请上传png类型图片！");
                return dataResult;
            }
            String subject = picBindCertCNVO.getSubject();
            if (null == subject || "".equals(subject)) {
                dataResult.setMsg("未获取到subject参数！");
                return dataResult;
            }
            String serial = picBindCertCNVO.getSerial();
            if (null == serial || "".equals(serial)) {
                dataResult.setMsg("未获取到serial参数！");
                return dataResult;
            }
            logger.info("参数验证通过");
            dataResult = xinjiangCAService.getCertSN(file,picBindCertCNVO);
        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("请求参数有误!");
            dataResult.setError("错误信息:"+e.getMessage());
        }

        return dataResult;
    }

}
