package com.shineyue.certSign.service;

import com.shineyue.certSign.model.bo.RaUserBO;
import com.shineyue.certSign.model.po.CertMsgPO;
import com.shineyue.certSign.model.vo.RaUserVO;

public interface IXinjiangRAService {

    <T> T dealCertSign(RaUserBO raUserBO);

    /**
     * @Author luofuwei
     * @Description //TODO 查询证书用户的信息
     * @Date 2019/9/9
     * @Param [certUserPO]
     * @return T
     **/
    <T> T queryCertUser(RaUserVO raUserVO) ;

    /**
     * @Author luofuwei
     * @Description //TODO 插入证书用户的信息
     * @Date 2019/9/9
     * @Param [raUserVO]
     * @return T
     **/
    <T> T insertCertUser(RaUserVO raUserVO);

    /**
     * @Author luofuwei
     * @Description //TODO  插入证书信息
     * @Date 2019/9/9
     * @Param [certMsgPO]
     * @return T
     **/
    <T> T insertCertMsg(CertMsgPO certMsgPO);

}
