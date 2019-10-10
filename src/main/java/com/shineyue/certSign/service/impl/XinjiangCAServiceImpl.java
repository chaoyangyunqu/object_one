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
import com.shineyue.certSign.utils.ParamConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PackageName: com.shineyue.certSign.service
 * @Description: TODO 签章处理
 * @author: 罗绂威
 * @date: wrote on 2019/8/28
 */
@Slf4j
@Service
public class XinjiangCAServiceImpl{

    @Resource
    ExecutorServiceImpl executorService;

    // 存放每一份合同对应多个买受人签章信息
    public static final Map<String, List<SignContractDTO>> PERSONSIGN = new ConcurrentHashMap<String,List<SignContractDTO>>();

    // 统计盖章次数
    public static final Map<String, Integer> PERSONSIGNCURRENT = new ConcurrentHashMap<String, Integer>();

    /** token */
    public static String token = "9e477b99-f958-4688-2fc6-ccf670861265";

    /** 电子章注册务地址 */
    @Value("${xinjiangca.auth}")
    public String authBaseUri;

    /** 企业公章服务地址 */
    @Value("${xinjiangca.ent}")
    public String entBaseUri;

    /** 企业骑缝章服务地址 */
    @Value("${xinjiangca.entMirle}")
    public String entMirleBaseUri;

    /** 个人签署服务地址 */
    @Value("${xinjiangca.person}")
    public String person;

    /** 个人签署回调地址 */
    @Value("${xinjiangca.signCallbackURL}")
    public String signCallbackURL;

    /** 新疆房产网签合同推送接口 */
    @Value("${xinjiangfc.fcSignCallbackURL}")
    private String fcSignCallbackURL;


    public <T> T dealSignCallbackPicOfPDF(SignCallbackDTO signCallbackDTO) {
        DataResult dataResult = new DataResult();
        log.info("个人签署推送房产网签开始。。。");
        try{
            dataResult.setStatus(100000);
            dataResult.setMsg("请求成功!");
            String dataJson = JSON.toJSONString(signCallbackDTO);
            log.info("房屋交易网签推送地址：{}",fcSignCallbackURL);
            HttpService.doPost(fcSignCallbackURL,dataJson);
        } catch (Exception e) {
            dataResult.setStatus(200001);
            dataResult.setMsg("个人签署推送房产网签失败");
            dataResult.setError(e.getMessage());
        }
        return (T) dataResult;
    }

    public <T> T getPicOfPDF(SignContractDTO signContractDTO) {
        DataResult dataResult = new DataResult();
        log.info("签章业务开始,请等待...");
        try {
            String base64String = signContractDTO.getInputPDF();
            // 证书序列号16进制转换十进制
            String certSNDec = new BigInteger(signContractDTO.getSerial(), 16).toString(10);
            // 法人证书序列号16进制转换十进制
            String corCertSNDec = "";
            // 盖章信息
            String dataJsonStr = "";
            // 企业法人盖章信息
            String corPrama = "";
            // 骑缝章标志
            boolean isMirle = false;
            // 法人章标志
            boolean isCor = false;
            // 用户类型
            int type = 0;
            type = signContractDTO.getType();
            log.info("userType:{}",type);
            if (2 == type) {
                // pdf信息
                String jsonStrPDF = "{'inputPDF':'" + base64String.trim();
                // 企业页码
                String pageNums = signContractDTO.getPageNums();
                // 章大小
                String picSizes = "";
                // 企业盖章位置
                String picPoints = signContractDTO.getPicPoints();
                // 企业参数信息
                String pramaJsonStr = "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";
                dataJsonStr = jsonStrPDF + pramaJsonStr ;
                log.info("企业盖章参数信息:{}",pramaJsonStr);
                log.info("企业请求URL:{}",entBaseUri);
                isMirle = true;
                if (!"".equals(signContractDTO.getCorSerial())) {
                    corCertSNDec = new BigInteger(signContractDTO.getCorSerial(), 16).toString(10);
                    // 法人页码
                    String corPageNums = signContractDTO.getCorPageNums();
                    // 法人盖章位置
                    String corPicPoints = signContractDTO.getCorPicPoints();
                    // 法人签章参数信息
                    corPrama = "','CertSN':'"+corCertSNDec+"','PageNums':'" + corPageNums + "','picSizes':'"+picSizes+"','picPoints':'"+corPicPoints+"','token':'"+token+"'}";
                    log.info("法人盖章参数信息:{}",corPrama);
                    log.info("法人请求URL:{}",entBaseUri);
                    isCor = true;
                }
            } else if (3 == type) {
                // 页码
                String pageNums = signContractDTO.getPageNums();
                // 企业章大小
                String picSizes = "";
                // 盖章位置
                String picPoints = signContractDTO.getPicPoints();
                // pdf信息
                String jsonStrPDF = "{'inputPDF':'" + base64String.trim();
                // 参数信息
                String pramaJsonStr = "','CertSN':'"+certSNDec+"','PageNums':'" + pageNums + "','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"','token':'"+token+"'}";
                dataJsonStr = jsonStrPDF + pramaJsonStr ;
                log.info("机构盖章参数信息:{}",pramaJsonStr);
                log.info("机构URL:{}",entBaseUri);
            } else {
                dataResult.setMsg("其他机构签章功能暂未开通");
                return (T) dataResult;
            }

            // 文件存放路径
            String basePath = System.getProperty("user.dir") + "/signPDF";
            // 网签合同号
            String wqhth = signContractDTO.getWqhth();
            // 设置文件名
            String fileName = "/商品房网签合同(企业已签章)" + wqhth + ".pdf";
            String filePath = basePath + fileName;
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                // 不存在则新建文件夹
                dest.getParentFile().mkdirs();
            }

            log.info("签章第一阶段开始");
            dataResult = HttpConnetUtils.httpConnet(signContractDTO,entBaseUri,dataJsonStr);

            if (100001 == dataResult.getStatus()) {
                SignContractDTO entSCDTO = (SignContractDTO) dataResult.getResults();
                log.info("企业/机构第一阶段签章完成");
                if (isMirle) {

                    // 骑缝章  默认中间位置
                    String dataJsonMirleStr = "{'inputPDF':'" + entSCDTO.getInputPDF();
                    String mirlsPrama = "','CertSN':'"+certSNDec+"','picSize':'','picPos':'','token':'"+token+"'}";
                    String mirlsDataJsonStr = dataJsonMirleStr + mirlsPrama;
                    if (null==entSCDTO.getInputPDF()||"".equals(entSCDTO.getInputPDF())) {
                        log.info("第一阶段返回没有pdf");
                    }else {
                        log.info("第一阶段返回有pdf");
                    }
                    log.info("骑缝章参数:{}",mirlsPrama);
                    log.info("开始进行骑缝章...");
                    DataResult mirleDataResult = HttpConnetUtils.httpConnet(entSCDTO,entMirleBaseUri,mirlsDataJsonStr);
                    log.info("企业骑缝章结束完成");
                    SignContractDTO mirleSCDTO = (SignContractDTO) mirleDataResult.getResults();

                    if (isCor) {
                        log.info("企业法人：{}",mirleSCDTO.getCorSubject());
                        log.info("企业法人key序列号：{}",mirleSCDTO.getCorSerial());
                        // 企业法人章签章信息
                        String corDataJsonStr = "{'inputPDF':'" + mirleSCDTO.getInputPDF() + corPrama;
                        DataResult corDataResult = HttpConnetUtils.httpConnet(mirleSCDTO,entBaseUri,corDataJsonStr);
                        log.info("企业法人章签章已完成");

                        SignContractDTO corSCDTO = (SignContractDTO) corDataResult.getResults();
                        ConvertUtil.base64StringToFile(corSCDTO.getInputPDF(),filePath);
                        log.info("当前已文件存入本地");

                        ParamConvertUtils.dealPersonSign(corSCDTO);
                        String wqhth1 = corSCDTO.getWqhth();
                        int current = PERSONSIGNCURRENT.get(wqhth1) + 1 ;
                        log.info("current:{}",current);
                        log.info("SumCurrent:{}",PERSONSIGN.get(wqhth1).size());
                        if (current <= PERSONSIGN.get(wqhth1).size()) {
                            PERSONSIGNCURRENT.put(wqhth1,current);
                        }else {
                            log.info("默认发送时,下标越界");
                        }

                        SignContractDTO sp = PERSONSIGN.get(wqhth1).get(0);
                        executorService.dealPersonSignExecutor(sp,person,signCallbackURL);
                        corDataResult.setMsg("企业公章、法人签章已完成，请进行个人签署");
                        return (T) corDataResult;
                    }

                    ParamConvertUtils.dealPersonSign(mirleSCDTO);
                    String wqhth1 = mirleSCDTO.getWqhth();
                    int current = PERSONSIGNCURRENT.get(wqhth1) + 1 ;
                    log.info("current:{}",current);
                    log.info("SumCurrent:{}",PERSONSIGN.get(wqhth1).size());
                    if (current <= PERSONSIGN.get(wqhth1).size()) {
                        PERSONSIGNCURRENT.put(wqhth1,current);
                    }else {
                        log.info("默认发送时,下标越界");
                    }
                    SignContractDTO sp = PERSONSIGN.get(wqhth1).get(0);
                    executorService.dealPersonSignExecutor(sp,person,signCallbackURL);
                    mirleDataResult.setMsg("企业公章已完成，请进行个人签署");
//                    ConvertUtil.base64StringToFile(mirleSCDTO.getInputPDF(),filePath);
                    return (T) mirleDataResult;
                }
//                ConvertUtil.base64StringToFile(entSCDTO.getInputPDF(),filePath);
            }
            return (T) dataResult;
        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("业务层电子签章处理失败!");
            dataResult.setError("错误信息:"+e.getMessage());
            return (T) dataResult;
        }
    }

    public <T> T dealPersonSign(SignContractDTO signContractDTO) {
        DataResult dataResult = new DataResult();
        log.info("个人签署开始操作,请等待...");
        try {

            String certSNHex = signContractDTO.getSerial();
            log.info("证书序列号:{}",certSNHex);
            // 16进制转换十进制
            String certSNDec = new BigInteger(certSNHex, 16).toString(10);
            String wqhth = signContractDTO.getWqhth();
            log.info("网签合同号:{}",wqhth);
            String pageNums = signContractDTO.getPersonPageNums();
            log.info("页码:{}",pageNums);
            // 个人章章大小
            String picSizes = "(60,60)";
            // pdf信息
            String tempPDF = signContractDTO.getInputPDF();
            log.info("".equals(tempPDF) ? "个人签署的pdf文件为空":"个人签署的pdf文件不为空");
            String pDFJsonStr = "{'inputPDF':'" + tempPDF;
            // 盖章位置
            String picPoints = signContractDTO.getPersonPicPoints();
            // 参数信息
            String pramaJsonStr = "','wqhth':'"+wqhth+"','UserInfo':{'name':'"+signContractDTO.getPersonName()+"','phone':'"+signContractDTO.getPersonPhone()+"','idCard':'"+signContractDTO.getPersonIdCard()+"'},"
                    + "'SignInfo':{'PageNums':'"+pageNums+"','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"'},'token':'"+token+"','EntInfo':{'entCode':'"+certSNDec+"','entName':'"
                    + signContractDTO.getSubject()+"'},'callbackUrl':'"+signCallbackURL+"'}";

            // 盖章信息
            String dataJsonStr = pDFJsonStr + pramaJsonStr ;
            log.info("个人签署参数信息:{}",pramaJsonStr);
            log.info("个人签署请求URL:{}",person);
            log.info("个人签署回调URL:{}",signCallbackURL);

            dataResult = HttpConnetUtils.httpConnet(signContractDTO,person,dataJsonStr);
            SignContractDTO rollBackscDTO = (SignContractDTO) dataResult.getResults();
            String wqhth1 = rollBackscDTO.getWqhth();
            if (100001 != dataResult.getStatus()) {
                return (T) dataResult;
            }
            log.info("个人签章处理结束");
            int current = PERSONSIGNCURRENT.get(wqhth1) + 1 ;
            log.info("current:{}",current);
            log.info("SumCurrent:{}",PERSONSIGN.get(wqhth1).size());
            if (current <= PERSONSIGN.get(wqhth1).size()) {
                PERSONSIGNCURRENT.put(wqhth1,current);
            }else {
                log.info("默认发送时,下标越界");
            }
            signContractDTO.setInputPDF(tempPDF);
            dataResult.setResults(signContractDTO);
            dataResult.setMsg("个人签署推送成功");
            log.info("个人签署推送成功!");
            return (T) dataResult;
        } catch ( Exception e ){
            dataResult.setMsg("个人签署推送失败");
            dataResult.setError("业务层错误信息："+e.getMessage());
            return (T) dataResult;
        }
    }

    public DataResult dealCallbackPersonSign (SignCallbackDTO signCallbackDTO) {
        DataResult dataResult = new DataResult();
        SignContractDTO signContractDTO;
        String wqhth = "";
        String inputPDF = "";
        String isSign = signCallbackDTO.getIsSign();
        String cancel  = "-2";
        String refuse  = "0";
        try{
            wqhth = signCallbackDTO.getWqhth();
            log.info("回调合同号:{}",wqhth);
            inputPDF = signCallbackDTO.getInputPDF();
            if (PERSONSIGN.containsKey(wqhth)) {
                if (PERSONSIGNCURRENT.containsKey(wqhth)) {
                    int current = PERSONSIGNCURRENT.get(wqhth) ;
                    log.info("回调curret:{}",current);
                    log.info("回调SumCurret:{}",PERSONSIGN.get(wqhth).size());
                    if (current < PERSONSIGN.get(wqhth).size()) {
                        signContractDTO = PERSONSIGN.get(wqhth).get(current);
                        if (!"".equals(inputPDF)) {
                            signContractDTO.setInputPDF(inputPDF);
                        }
                        // 合同撤销/拒绝签署
                        if (isSign.equals(cancel) || isSign.equals(refuse)) {
                            PERSONSIGN.remove(wqhth);
                            PERSONSIGNCURRENT.remove(wqhth);
                            return dealSignCallbackPicOfPDF(signCallbackDTO);
                        }
                        dataResult = dealPersonSign(signContractDTO);
                        if (100001 != dataResult.getStatus()) {
                            PERSONSIGN.remove(wqhth);
                            PERSONSIGNCURRENT.remove(wqhth);
                            log.info("回调个人签署异常:{}",dataResult.getError());
                        }
                        PERSONSIGNCURRENT.put(wqhth,current + 1);
                    }else {
                        PERSONSIGN.remove(wqhth);
                        PERSONSIGNCURRENT.remove(wqhth);
                        dataResult = dealSignCallbackPicOfPDF(signCallbackDTO);
                    }
                }
            }
        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("事件处理个人签署出错");
            dataResult.setError(e.getMessage());
        }

        return dataResult;
    }

    // 获取certCN  并与企业唯一号绑定
//    @Override
    public DataResult getCertSN(MultipartFile file , PicBindCertCNVO picBindCertCNVO){
        log.info("**************************调用ReEnterpriseWebService接口开始*******************");
        DataResult dataResult = new DataResult();
        try {
            String userName = picBindCertCNVO.getSubject();
            // 企业公章数据
            String entImgData = ConvertUtil.GetImageBaes64Str(file);
            // 图片类型
            String entSealType = "png";
            // 该值通过硬件设备的签名证书中获取 40328751378DE9B463BFE72B
            String certSNHex = picBindCertCNVO.getSerial();
            // CertSN 企业唯一SN值 需要将获取的16进制转换成10进制
            String certSNDec = new BigInteger(certSNHex, 16).toString(10);
            // 企业联系人手机号
            String entPhone = picBindCertCNVO.getEntPhone();

            String str = "{'CertSN':'" + certSNDec
                    + "','EntUserName':'"+userName+"','EntPhone':'"+entPhone+"','EntSealData':'" + entImgData
                    + "','EntSealType':'" + entSealType + "','token':'"+token+"'}";

            String strShow = "{'CertSN':'" + certSNDec
                    + "','EntUserName':'"+userName+"','EntPhone':'"+entPhone
                    + "','EntSealType':'" + entSealType + "','token':'"+token+"'}";

            log.info("企业注册电子url:{}",authBaseUri);
            log.info("注册信息:{}",strShow);
            log.info("注册中，请稍后。。。");
            URL targetUrl = new URL(authBaseUri);
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl
                    .openConnection();
            httpConnection.setConnectTimeout(60000);
            httpConnection.setReadTimeout(60000);
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
            // log.info(resStr);
            String resJson = ConvertUtil.decodeStr(resStr.trim().replace(' ',
                    '+'));
            log.info(resJson);
            JSONObject resJsonObj = JSON.parseObject(resJson);
            if (resJsonObj.getString("SUCCESS").equals("FALSE")) {
                String FailedReson = resJsonObj.getString("REASON");
                log.info("企业注册失败，原因如下:");
                log.info(FailedReson);
                dataResult.setSuccess(false);
                dataResult.setMsg(FailedReson);
                return dataResult;
            }

            if (resJsonObj.getString("SUCCESS").equals("TRUE")) {
                log.info("企业注册成功");
            }
            httpConnection.disconnect();
            log.info("**************************调用ReEnterpriseWebService接口结束*******************");
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
