package com.shineyue.certSign.dao;

import com.shineyue.certSign.model.po.CertMsgPO;
import com.shineyue.certSign.model.po.CertRegisterPO;
import com.shineyue.certSign.model.po.CertUserPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/5
 */
@Mapper
public interface XinjiangRAMapper {

    @Insert("INSERT INTO cert_msg(user_number,content) VALUES (#{userNumber},#{content})")
    int insertCertMsg(CertMsgPO certMsgPO);

    @Insert("insert into cert_register(user_number,rid) VALUES(#{userNumber},#{rid})")
    int insertUserIdToCertRegister(CertRegisterPO certRegisterPO);

    @Insert("INSERT INTO cert_user (\n" +
            "user_name,\n" +
            "user_idno,\n" +
            "cert_cn,\n" +
            "user_org,\n" +
            "user_state,\n" +
            "user_country,\n" +
            "user_email,\n" +
            "user_phone,\n" +
            "user_saname,\n" +
            "user_domain,\n" +
            "user_crey,\n" +
            "user_cert_type,\n" +
            "req\n" +
            ") VALUES (\n" +
            "#{userName},#{userIdno},#{userCn},#{userOrg},#{userState},#{userCountry},\n" +
            "#{userEmail},#{userPhone},#{userSaname},#{userDomain},#{userCrey},#{userCertType},#{req}\n" +
            "\n" +
            ")")
    int insertCertUserPO(CertUserPO certUserPO);

    /** 是否在系统录入用户基本信息 */
    @Select("select \n" +
            "id,user_number as userNumber,user_name as userName,user_idno as userIdno,cert_cn as certCn,user_org as userOrg,user_state as userState,user_country as userCountry,user_email as userEmail,user_phone as userPhone,user_saname as userSaname,user_domain as userDomain,user_crey as userCrey,user_cert_type as userCertType,req FROM cert_user where user_name=#{userName} and user_phone=#{userPhone};")
    CertUserPO queryCertUser(CertUserPO certUserPO);

    /** 是否注册 */
    @Select("select id,user_number as userNumber,rid from cert_register a WHERE user_number as userNumber=#{userNumber}")
    CertRegisterPO isRegister(int userNumber);

    /** 是否发证 */
    @Select("select a.id,a.user_number as userNumber,a.content,a.is_install as isInstallfrom cert_msg a where a.user_number as userNumber=#{userNumber}")
    CertMsgPO isSign(int userNumber);


}
