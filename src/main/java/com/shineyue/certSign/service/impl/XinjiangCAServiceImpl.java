package com.shineyue.certSign.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.SignCallbackDTO;
import com.shineyue.certSign.model.dto.SignContractDTO;
import com.shineyue.certSign.model.vo.PicBindCertCNVO;
import com.shineyue.certSign.utils.ConvertUtil;
import com.shineyue.certSign.utils.HttpConnetUtils;
import com.shineyue.certSign.utils.HttpService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @PackageName: com.shineyue.certSign.service
 * @Description: TODO
 * @author: 罗绂威
 * @date: wrote on 2019/8/28
 */
@Service
public class XinjiangCAServiceImpl{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** token */
    private String token = "9e477b99-f958-4688-2fc6-ccf670861265";

    /** 电子章注册务地址 */
    @Value("${xinjiangca.auth}")
    private String authBaseUri;

    /** 企业公章服务地址 */
    @Value("${xinjiangca.ent}")
    private String entBaseUri;

    /** 企业骑缝章服务地址 */
    @Value("${xinjiangca.entMirle}")
    private String entMirleBaseUri;

    /** 个人签署服务地址 */
    @Value("${xinjiangca.person}")
    private String person;

    /** 个人签署回调地址 */
    @Value("${xinjiangca.signCallbackURL}")
    private String signCallbackURL;

    /** 新疆房产网签合同推送接口 */
    @Value("${xinjiangfc.fcSignCallbackURL}")
    private String fcSignCallbackURL;

    public <T> T dealSignCallbackPicOfPDF(SignCallbackDTO signCallbackDTO) {
        DataResult dataResult = new DataResult();
        logger.info("个人签署推送房产网签开始。。。");
        try{
            dataResult.setStatus(100000);
            dataResult.setMsg("请求成功!");
            String dataJson = JSON.toJSONString(signCallbackDTO);
            String returnStr = HttpService.doPost(fcSignCallbackURL,dataJson);
        } catch (Exception e) {
            dataResult.setStatus(200001);
            dataResult.setMsg("个人签署推送房产网签失败");
            dataResult.setError(e.getMessage());
        }
        return (T) dataResult;
    }

    public <T> T getPicOfPDF(SignContractDTO signContractDTO) {
        DataResult dataResult = new DataResult();
        logger.info("开始操作,请等待...");
        try {
        // 证书序列号
        String certSNHex = signContractDTO.getSerial();
        // 16进制转换十进制
        String certSNDec = new BigInteger(certSNHex, 16).toString(10);
        // 网签合同号
        String wqhth = "";
        // 用户类型
        int type = 0;
        // pdf文件
        String base64String = signContractDTO.getInputPDF();
        // 盖章信息
        String dataJsonStr = "";
        // 骑缝章标志
        boolean isMirle = false;
        // 骑缝章  默认中间位置
        String dataJsonMirleStr = "";
        // PDF文件信息
        // 服务地址
        String baseURI = "";
        type = signContractDTO.getType();
        logger.info("userType:{}",type);
        if (2 == type) {
            // 页码
            String pageNums = signContractDTO.getPageNums();
            // 企业章大小
            String picSizes = "(100,100)";
            // 盖章位置
            String picPoints = signContractDTO.getPicPoints();
            // 盖章信息
            dataJsonStr = "{'inputPDF':'"
                    + base64String.trim()
                    + "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";
            baseURI = entBaseUri;
            isMirle = true;
            logger.info("盖章信息:{}",dataJsonStr);
            logger.info("请求URL:{}",baseURI);

        } else if (3 == type) {
            // 页码
            String pageNums = signContractDTO.getPageNums();
            // 企业章大小
            String picSizes = "(100,100)";
            // 盖章位置
            String picPoints = signContractDTO.getPicPoints();
            // 盖章
            dataJsonStr = "{'inputPDF':'"
                    + base64String.trim()
                    + "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";

            baseURI = entBaseUri;
            logger.info("盖章信息:{}",dataJsonStr);
            logger.info("请求URL:{}",baseURI);
        } else {
            dataResult.setMsg("其他机构签章功能暂未开通");
            return (T) dataResult;
        }

        // 文件存放路径
        String basePath = System.getProperty("user.dir") + "/signPDF";
        wqhth = signContractDTO.getWqhth();
        // 设置文件名
        String fileName = "/商品房网签合同(企业已签章)" + wqhth + ".pdf";
        String filePath = basePath + fileName;
        File dest = new File(filePath);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            // 不存在则新建文件夹
            dest.getParentFile().mkdirs();
        }
        dataResult = HttpConnetUtils.httpConnet(signContractDTO,baseURI,dataJsonStr);
        // 存入本地
        if (100001 == dataResult.getStatus()) {
            SignContractDTO signContractDTO1 = (SignContractDTO) dataResult.getResults();
            if (isMirle) {
                // 骑缝章  默认中间位置
                dataJsonMirleStr = "{'inputPDF':'"
                        + signContractDTO1.getInputPDF()
                        + "','CertSN':'"+certSNDec+"','picSize':'100','picPos':'','token':'"+token+"'}";
                DataResult dataResult1 = HttpConnetUtils.httpConnet(signContractDTO1,entMirleBaseUri,dataJsonMirleStr);
                SignContractDTO signContractDTO2 = (SignContractDTO) dataResult.getResults();
                ConvertUtil.base64StringToFile(signContractDTO2.getInputPDF(),filePath);
                // 个人签署
                logger.info("企业骑缝章已完成");
                dataResult1 = dealPersonSign(signContractDTO2);
                return (T) dataResult1;
            }
            ConvertUtil.base64StringToFile(signContractDTO1.getInputPDF(),filePath);
        }
        return (T) dataResult;
        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("业务层电子签章处理失败!");
            dataResult.setError("错误信息:"+e.getMessage());
            return (T) dataResult;
        }
    }

    /**
     * @Author luofuwei
     * @Description //TODO 电子签章
     * @Date 2019/9/4
     * @Param [file] 网签合同PDF文件
     * @return T
     **/
//    @Override
    public <T> T getPicOfPDFTest(MultipartFile file,SignContractDTO signContractDTO) {
        DataResult dataResult = new DataResult();
        logger.info("开始操作,请等待...");
        try {

            File f = ConvertUtil.multipartFileToFile(file);
            // PDF文件转码
            String base64String= ConvertUtil.getPDFBinary(f);
            // 删除遗留文件
            File del = new File(f.toURI());
            del.delete();
//            -----分割线-------
            // 证书序列号
            String certSNHex = signContractDTO.getSerial();
            // 16进制转换十进制
            String certSNDec = new BigInteger(certSNHex, 16).toString(10);
            // 网签合同号
            String wqhth = "";
            // 用户类型
            int type = 0;
            // 盖章信息
            String dataJsonStr = "";
            // 骑缝章标志
            boolean isMirle = false;
            // 骑缝章  默认中间位置
            String dataJsonMirleStr = "";
            // PDF文件信息
            // 服务地址
            String baseURI = "";
            type = signContractDTO.getType();
            logger.info("userType:{}",type);
            if (2 == type) {
                // 页码
                String pageNums = signContractDTO.getPageNums();
                // 企业章大小
                String picSizes = "(100,100)";
                // 盖章位置
                String picPoints = signContractDTO.getPicPoints();
                // 盖章信息
                dataJsonStr = "{'inputPDF':'"
                        + base64String.trim()
                        + "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";
                baseURI = entBaseUri;
                isMirle = true;
                logger.info("盖章信息:{}",dataJsonStr);
                logger.info("请求URL:{}",baseURI);

            } else if (3 == type) {
                // 页码
                String pageNums = signContractDTO.getPageNums();
                // 企业章大小
                String picSizes = "(100,100)";
                // 盖章位置
                String picPoints = signContractDTO.getPicPoints();
                // 盖章
                dataJsonStr = "{'inputPDF':'"
                        + base64String.trim()
                        + "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";

                baseURI = entBaseUri;
                logger.info("盖章信息:{}",dataJsonStr);
                logger.info("请求URL:{}",baseURI);
            } else {
                dataResult.setMsg("其他机构签章功能暂未开通");
                return (T) dataResult;
            }

            // 文件存放路径
            String basePath = System.getProperty("user.dir") + "/signPDF";
            wqhth = signContractDTO.getWqhth();
            // 设置文件名
            String fileName = "/商品房网签合同(企业已签章)" + wqhth + ".pdf";
            String filePath = basePath + fileName;
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                // 不存在则新建文件夹
                dest.getParentFile().mkdirs();
            }
            dataResult = HttpConnetUtils.httpConnet(signContractDTO,baseURI,dataJsonStr);
            // 存入本地
            if (100001 == dataResult.getStatus()) {
                SignContractDTO signContractDTO1 = (SignContractDTO) dataResult.getResults();
                if (isMirle) {
                    // 骑缝章  默认中间位置
                    dataJsonMirleStr = "{'inputPDF':'"
                            + signContractDTO1.getInputPDF()
                            + "','CertSN':'"+certSNDec+"','picSize':'100','picPos':'','token':'"+token+"'}";
                    DataResult dataResult1 = HttpConnetUtils.httpConnet(signContractDTO1,entMirleBaseUri,dataJsonMirleStr);
                    SignContractDTO signContractDTO2 = (SignContractDTO) dataResult.getResults();
                    ConvertUtil.base64StringToFile(signContractDTO2.getInputPDF(),filePath);
                    // 个人签署
                    logger.info("企业骑缝章已完成");
                    dataResult1 = dealPersonSign(signContractDTO2);
                    return (T) dataResult1;
                }
                ConvertUtil.base64StringToFile(signContractDTO1.getInputPDF(),filePath);
            }
            return (T) dataResult;
        } catch ( Exception e ){
            dataResult.setMsg("电子签章失败");
            dataResult.setError("业务层错误信息："+e.getMessage());
            return (T) dataResult;
        }
    }

    public <T> T dealPersonSign(SignContractDTO signContractDTO) {
        DataResult dataResult = new DataResult();
        logger.info("个人签署开始操作,请等待...");
        try {

            // 证书序列号
            String certSNHex = signContractDTO.getSerial();
            // 16进制转换十进制
            String certSNDec = new BigInteger(certSNHex, 16).toString(10);
            // 网签合同号
            String wqhth = signContractDTO.getWqhth();
            // 页码
            String pageNums = signContractDTO.getPersonPageNums();
            // 企业章大小
            String picSizes = "(60,60)";
            // 盖章位置
            String picPoints = signContractDTO.getPersonPicPoints();
            // 盖章信息
            String dataJsonStr = "";
            // pdf信息
            String PDFJsonStr = "{'inputPDF':'" + signContractDTO.getInputPDF();
            // 参数信息
            String pramaJsonStr = "','wqhth':'"+wqhth+"','UserInfo':{'name':'"+signContractDTO.getPersonName()+"','phone':'"+signContractDTO.getPersonPhone()+"','idCard':'"+signContractDTO.getPersonIdCard()+"'},"
                    + "'SignInfo':{'PageNums':'"+pageNums+"','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"'},'token':'"+token+"','EntInfo':{'entCode':'"+certSNDec+"','entName':'"
                    + signContractDTO.getSubject()+"'},'callbackUrl':'"+signCallbackURL+"'}";
            dataJsonStr = PDFJsonStr + pramaJsonStr ;
            logger.info("TestinputPDF:{}",PDFJsonStr);
            logger.info("个人盖章信息:{}",pramaJsonStr);
            logger.info("个人请求URL:{}",person);

            dataResult = HttpConnetUtils.httpConnet(signContractDTO,person,dataJsonStr);
            if (100001 != dataResult.getStatus()) {
                return (T) dataResult;
            }
            dataResult.setMsg("个人签署推送成功");
            dataResult.setResults(signContractDTO);
            logger.info("个人签署推送成功!");
            return (T) dataResult;
        } catch ( Exception e ){
            dataResult.setMsg("个人签署推送失败");
            dataResult.setError("业务层错误信息："+e.getMessage());
            return (T) dataResult;
        }
    }

    // 获取certCN  并与企业唯一号绑定
//    @Override
    public DataResult getCertSN(MultipartFile file , PicBindCertCNVO picBindCertCNVO){
        logger.info("**************************调用ReEnterpriseWebService接口开始*******************");
        DataResult dataResult = new DataResult();
        try {
            String userName = picBindCertCNVO.getSubject();
            // 企业公章数据
            String entImgData = ConvertUtil.GetImageBaes64Str(file);
            String entSealType = "png";
            // 该值通过硬件设备的签名证书中获取 40328751378DE9B463BFE72B
            String certSNHex = picBindCertCNVO.getSerial();
            // CertSN 企业唯一SN值 需要将获取的16进制转换成10进制
            String certSNDec = new BigInteger(certSNHex, 16).toString(10);
            String str = "{'CertSN':'" + certSNDec
                    + "','EntUserName':'"+userName+"','EntSealData':'" + entImgData
                    + "','EntSealType':'" + entSealType + "','token':'"+token+"'}";

            logger.info("authBaseUri:{}",authBaseUri);
            logger.info("token:{}",token);
            logger.info("certCN信息:{}",certSNDec);
            URL targetUrl = new URL(authBaseUri);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl
                    .openConnection();
            httpConnection.setConnectTimeout(30000);
            httpConnection.setReadTimeout(30000);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type",
                    "application/json");
            OutputStreamWriter paramout = new OutputStreamWriter(
                    httpConnection.getOutputStream(), "UTF-8");
            paramout.write(str);
            paramout.flush();
            paramout.close();
            if (httpConnection.getResponseCode() != 200) {
                dataResult.setStatus(200);
                dataResult.setSuccess(false);
                dataResult.setMsg("网络连接失败");
                return dataResult;
            }

            String resStr = "";
            resStr = IOUtils.toString(httpConnection.getInputStream(),
                    StandardCharsets.UTF_8);
            // logger.info(resStr);
            String resJson = ConvertUtil.decodeStr(resStr.trim().replace(' ',
                    '+'));
            logger.info(resJson);
            JSONObject resJsonObj = JSON.parseObject(resJson);
            if (resJsonObj.getString("SUCCESS").equals("FALSE")) {
                String FailedReson = resJsonObj.getString("REASON");
                logger.info("企业注册失败，原因如下:");
                logger.info(FailedReson);
                dataResult.setSuccess(false);
                dataResult.setMsg(FailedReson);
                return dataResult;
            }

            if (resJsonObj.getString("SUCCESS").equals("TRUE")) {
                logger.info("企业注册成功");
                logger.info("信息如下：{}",resJsonObj);
                String res = resJsonObj.getString("BASEDATA"); // 返回唯一编码值
                // 用于其他接口参数  certSN
                logger.info("企业唯一号:" + res);

            }
            httpConnection.disconnect();
            logger.info("**************************调用ReEnterpriseWebService接口结束*******************");
            dataResult.setSuccess(true);
            dataResult.setMsg("注册成功");
            return dataResult;
        } catch ( Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("电子章注册失败！");
            dataResult.setError("业务层错误："+e.getMessage());
            return dataResult;
        }
    }
}
