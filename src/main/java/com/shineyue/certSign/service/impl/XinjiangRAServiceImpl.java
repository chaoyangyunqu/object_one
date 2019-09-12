package com.shineyue.certSign.service.impl;

import com.shineyue.certSign.dao.XinjiangRAMapper;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.bo.RaUserBO;
import com.shineyue.certSign.model.po.CertMsgPO;
import com.shineyue.certSign.model.po.CertUserPO;
import com.shineyue.certSign.model.vo.RaUserVO;
import com.shineyue.certSign.service.IXinjiangRAService;
import com.shineyue.certSign.utils.RAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @PackageName: com.shineyue.certSign.service
 * @Description: TODO 证书相关操作
 * @author: 罗绂威
 * @date: wrote on 2019/8/28
 */
@Service
public class XinjiangRAServiceImpl implements IXinjiangRAService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /** 企业基本信息服务地址 */
    @Value("xinjiangfcUrl.http")
    private String xjfcQyjbxx;

    @Resource
    XinjiangRAMapper xinjiangRAMapper;

    /**
     * @Author luofuwei
     * @Description //TODO 注册并发证
     * @Date 2019/9/9
     * @Param [raUserVO]
     * @return T
     **/
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public <T> T dealCertSign(RaUserBO raUserBO) {

        DataResult dataResult = new DataResult();

        try{

            // 获取RA远程服务器返回注册的userId
            RAUtils RAUtils = new RAUtils();
            dataResult = RAUtils.getUserId(raUserBO);
            // 注册失败
            if (100001 != dataResult.getStatus()) {
                return (T) dataResult;
            }
            // 注册成功
            int userId = (int) dataResult.getResults();
            // 审核发证
            dataResult = RAUtils.reviewSign(userId,raUserBO);
            // 发证失败
            if (100001 != dataResult.getStatus()) {
                return (T) dataResult;
            }
            // 发证成功
            return (T) dataResult;

        } catch (Exception e) {
            dataResult.setStatus(400001);
            dataResult.setMsg("发证业务处理失败！");
            dataResult.setError("异常信息："+e.getMessage());
            return (T) dataResult;
        }
    }

    /**
     * @param raUserVO
     * @return T
     * @Author luofuwei
     * @Description //TODO 插入证书用户信息
     * @Date 2019/9/9
     * @Param [certUserPO]
     */
    @Override
    public <T> T insertCertUser(RaUserVO raUserVO) {
        DataResult dataResult = new DataResult();
        try {

            CertUserPO certUserPO = new CertUserPO();
            // VO 转 PO 对象
            BeanUtils.copyProperties(raUserVO,certUserPO);
            // 录入RA用户信息
            int row = xinjiangRAMapper.insertCertUserPO(certUserPO);
            logger.info("插入信息：[{}]条",row);
            if (row <= 0) {
                dataResult.setStatus(500001);
                dataResult.setError("业务系统录入RA用户信息失败!");
                return (T) dataResult;
            }

            dataResult.setStatus(100001);
            dataResult.setMsg("业务系统录入RA用户信息成功!");

        } catch ( Exception e ) {
            dataResult.setStatus(400001);
            dataResult.setMsg("服务器程序出现异常!");
            dataResult.setError("异常原因:"+e.getMessage());
        }

        return (T) dataResult;
    }

    /**
     * @param certMsgPO
     * @return T
     * @Author luofuwei
     * @Description //TODO  插入证书信息
     * @Date 2019/9/9
     * @Param [certMsgPO]
     */
    @Override
    public <T> T insertCertMsg(CertMsgPO certMsgPO) {
        DataResult dataResult = new DataResult();
        try {

            // 录入证书信息
            int row = xinjiangRAMapper.insertCertMsg(certMsgPO);
            if (row <= 0) {
                dataResult.setStatus(500001);
                dataResult.setError("业务系统录入证书信息失败!");
                return (T) dataResult;
            }
            dataResult.setStatus(100001);
            dataResult.setMsg("业务系统录入证书信息成功!");

        } catch ( Exception e ) {
            dataResult.setStatus(400001);
            dataResult.setMsg("服务器程序出现异常!");
            dataResult.setError("异常原因:"+e.getMessage());
        }
        return (T) dataResult;
    }

    /**
     * @param raUserVO
     * @return T
     * @Author luofuwei
     * @Description //TODO 查询证书用户的信息
     * @Date 2019/9/9
     * @Param [raUserVO]
     */
    @Override
    public <T> T queryCertUser(RaUserVO raUserVO) {
        DataResult dataResult = new DataResult();
        try {

            CertUserPO certUserPO = new CertUserPO();
            // VO 转 PO 对象
            BeanUtils.copyProperties(raUserVO,certUserPO);
            // 查询录入信息
            CertUserPO returnCertUserPO = xinjiangRAMapper.queryCertUser(certUserPO);
            if (StringUtils.isEmpty(returnCertUserPO)) {
                dataResult.setSuccess(true);
                dataResult.setStatus(500001);
                dataResult.setMsg("未查询到证书用户信息!");
                dataResult.setError("查到一个空对象啦");

                return (T) dataResult;
            }
            dataResult.setSuccess(true);
            dataResult.setStatus(100001);
            dataResult.setMsg("查询成功");
            dataResult.setResults(returnCertUserPO);

        } catch ( Exception e ) {
            dataResult.setSuccess(false);
            dataResult.setStatus(400001);
            dataResult.setMsg("服务器程序出现异常!");
            dataResult.setError("异常原因:"+e.getMessage());
        }

        return (T) dataResult;
    }
}
