package com.shineyue.certSign.service.impl;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.SignContractDTO;
import com.shineyue.certSign.utils.HttpConnetUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

import static com.shineyue.certSign.service.impl.XinjiangCAServiceImpl.PERSONSIGN;
import static com.shineyue.certSign.service.impl.XinjiangCAServiceImpl.PERSONSIGNCURRENT;

/**
 * @Description: TODO 异步实现类
 * @author: luofuwei
 * @date: wrote on 2019/9/21
 */
@Slf4j
@Component
public class ExecutorServiceImpl {
    /** token */
    public static String token = "9e477b99-f958-4688-2fc6-ccf670861265";

    @Async("asyncServiceExecutor")
    public void dealPersonSignExecutor(SignContractDTO signContractDTO,String person,String signCallbackURL){
        log.info("个人签署开始操作,请等待...");
        try{
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
            // pdf信息
            String tempPDF = signContractDTO.getInputPDF();
            String PDFJsonStr = "{'inputPDF':'" + tempPDF;
            // 盖章位置
            String picPoints = signContractDTO.getPersonPicPoints();
            // 参数信息
            String pramaJsonStr = "','wqhth':'"+wqhth+"','UserInfo':{'name':'"+signContractDTO.getPersonName()+"','phone':'"+signContractDTO.getPersonPhone()+"','idCard':'"+signContractDTO.getPersonIdCard()+"'},"
                    + "'SignInfo':{'PageNums':'"+pageNums+"','picSizes':'"+picSizes+"','picPoints':'"+picPoints+"'},'token':'"+token+"','EntInfo':{'entCode':'"+certSNDec+"','entName':'"
                    + signContractDTO.getSubject()+"'},'callbackUrl':'"+signCallbackURL+"'}";

            // 盖章信息
            String dataJsonStr = PDFJsonStr + pramaJsonStr ;
            if ("".equals(tempPDF)) {
                log.info("不存在个人盖章PDF文件");
            }else {
                log.info("存在个人盖章PDF文件");
            }
            log.info("个人签署参数信息:{}",pramaJsonStr);
            log.info("个人签署请求URL:{}",person);
            log.info("个人签署回调URL:{}",signCallbackURL);

            DataResult dataResult = HttpConnetUtils.httpConnet(signContractDTO,person,dataJsonStr);
            if (!dataResult.isSuccess()) {
                if (PERSONSIGN.containsKey(wqhth)) {
                    PERSONSIGN.remove(wqhth);
                }
                if (PERSONSIGNCURRENT.containsKey(wqhth)) {
                    PERSONSIGNCURRENT.remove(wqhth);
                }
            }
            SignContractDTO rollBackscDTO = (SignContractDTO) dataResult.getResults();
            log.info("个人签章处理结束");
            signContractDTO.setInputPDF(tempPDF);
            dataResult.setResults(signContractDTO);
            dataResult.setMsg("个人签署推送成功");
            log.info("个人签署推送成功!");
        } catch (Exception e) {
            log.info("个人签署失败");
            e.printStackTrace();
        }
    }
}
