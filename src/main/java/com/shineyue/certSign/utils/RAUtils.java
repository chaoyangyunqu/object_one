package com.shineyue.certSign.utils;

import com.koal.ra.rpc.client.RaApiClient;
import com.koal.ra.rpc.client.RaApiException;
import com.koal.ra.rpc.client.constant.FsmId;
import com.koal.ra.rpc.client.constant.ModuleId;
import com.koal.ra.rpc.client.constant.ReviewType;
import com.koal.ra.rpc.client.resp.IssueResp;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.bo.RaUserBO;
import com.shineyue.certSign.model.dto.RaUserDTO;
import com.shineyue.certSign.model.po.CertMsgPO;
import koal.ra.caclient.LraType;
import koal.ra.caclient.ReqType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @Description: TODO 业务层工具代码
 * @author: luofuwei
 * @date: wrote on 2019/9/8
 */
@Slf4j
public class RAUtils {

    private final Logger logger = LoggerFactory.getLogger(RAUtils.class);

    /** 进行RA注册 */
    public DataResult getUserId(RaUserBO raUserBO){

        DataResult dataResult = new DataResult();
        int userId = 0;

        try {
            RaApiClient client = null;
            RaApiClient client2 = null;
            String dealUserCertType = null;

//            String kraPath = System.getProperty("user.dir") + "/src/main/resources/kra.properties";
//            String kra2Path = System.getProperty("user.dir") + "/src/main/resources/kra2.properties";
            String kraPath = System.getProperty("user.dir") + "/config/kra.properties";
            String kra2Path = System.getProperty("user.dir") + "/config/kra2.properties";
            logger.info("kra路径:{}",kraPath);
            logger.info("kra2路径:{}",kra2Path);

            int userCertTypeCode = raUserBO.getUserCertTypeCode();
            String userCertType = raUserBO.getUserCertType();

            /**
             * 1: 个人证书
             * 2: 企业证书
             * */
            if (1 == userCertTypeCode) {
                client = new RaApiClient(kraPath, ModuleId.user, FsmId.user);
                client2 = new RaApiClient(kra2Path, ModuleId.user, FsmId.user);
                // 初始化
                client.init();
                client2.init();
                dealUserCertType = userCertType;
            }else if (2 == userCertTypeCode) {
                client = new RaApiClient(kraPath, ModuleId.ent, FsmId.ent);
                client2 = new RaApiClient(kra2Path, ModuleId.ent, FsmId.ent);
                // 初始化
                client.init();
                client2.init();
                // CertType.ENT_DUAL_CERT_RSA.getCertTypeId();
                dealUserCertType = userCertType;
            }else {
                dataResult.setStatus(100002);
                dataResult.setError("证书尚未支持");
                return dataResult;
            }

            log.info("用户类型:{}",userCertTypeCode);
            log.info("证书类型类型:{}",dealUserCertType);
            // RA注册申请
            userId = (int) client.certApply(dealUserCertType, raUserBO.getRaUserDTO());
            if (userId <=0 ) {
                dataResult.setStatus(200001);
                dataResult.setError("RA远程服务器异常!");
                return dataResult;
            }

            logger.info("userId:{}",userId);
            dataResult.setStatus(100001);
            dataResult.setResults(userId);

            return dataResult;

        } catch ( RaApiException  e) {
            dataResult.setStatus(200004);
            dataResult.setMsg("Ra注申请失败");
            dataResult.setError(e.getMessage());
            return dataResult;
        }
    }

    public DataResult reviewSign (int userId, RaUserBO raUserBO) {

        DataResult dataResult = new DataResult();

        try {

            RaApiClient client = null;
            RaApiClient client2 = null;

//            String kraPath = System.getProperty("user.dir") + "/src/main/resources/kra.properties";
//            String kra2Path = System.getProperty("user.dir") + "/src/main/resources/kra2.properties";
            String kraPath = System.getProperty("user.dir") + "/config/kra.properties";
            String kra2Path = System.getProperty("user.dir") + "/config/kra2.properties";
            logger.info("kra路径:{}",kraPath);
            logger.info("kra2路径:{}",kra2Path);
            int userCertTypeCode = raUserBO.getUserCertTypeCode();

            /**
             * 1: 个人证书
             * 2: 企业证书
             * */
            if (1 == userCertTypeCode) {
                client = new RaApiClient(kraPath, ModuleId.user, FsmId.user);
                client2 = new RaApiClient(kra2Path, ModuleId.user, FsmId.user);
                // 初始化
                client.init();
                client2.init();
            }else if (2 == userCertTypeCode) {
                client = new RaApiClient(kraPath, ModuleId.ent, FsmId.ent);
                client2 = new RaApiClient(kra2Path, ModuleId.ent, FsmId.ent);
                // 初始化
                client.init();
                client2.init();
            }else {
                dataResult.setStatus(100002);
                dataResult.setError("证书尚未支持");
                return dataResult;
            }

            String req = raUserBO.getReq();
            logger.info("zsreq:{}",req);
            RaUserDTO userReviewDTO = new RaUserDTO(userId);
            // 审核发证
            boolean b = client2.review(userReviewDTO, ReviewType.ACCEPT);
            if (b) {
                IssueResp issueResp = client.sign(userReviewDTO, ReqType.CMP,
                        req, LraType.CMP);

                if (null == issueResp.getLraInfo() || "".equals(issueResp.getLraInfo())) {
                    dataResult.setStatus(200001);
                    dataResult.setMsg("证书服务方接口有误,未获取到证书!");
                    return dataResult;
                }

                // 暂存证书信息
                CertMsgPO certMsgPO = new CertMsgPO();
                certMsgPO.setContent(issueResp.getLraInfo());
                dataResult.setStatus(100001);
                dataResult.setSuccess(true);
                dataResult.setMsg("已获取到证书!");
                dataResult.setResults(certMsgPO);
                return dataResult;
            }

            dataResult.setStatus(200002);
            dataResult.setMsg("证书审核不通过!");

            return dataResult;

        } catch ( RaApiException  e ) {
            dataResult.setStatus(200004);
            dataResult.setMsg("Ra注申请失败");
            dataResult.setError(e.getMessage());
            return dataResult;
        }
    }
}
