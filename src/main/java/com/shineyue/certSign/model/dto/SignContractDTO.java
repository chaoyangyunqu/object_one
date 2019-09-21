package com.shineyue.certSign.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * @Description: TODO 网签合同数据传输对象
 * @author: luofuwei
 * @date: wrote on 2019/9/7
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignContractDTO {
    /** 网签合同号 */
    private String wqhth;

    /** PDF文件 */
    private String inputPDF;

    /** key的序列号 */
    private String serial;

    /** 对象名称 */
    private String subject;

    /** 用户类型(1.个人/2.企业/3.房管局) */
    private int type;

    /** 页码,可多个 */
    private String pageNums;

    /** 签章位置 */
    private String picPoints;

    /** 个人名称 */
    private String personName;

    /** 个人电话 */
    private String personPhone;

    /** 个人身份证 */
    private String personIdCard;

    /** 个人签署页码 */
    private String personPageNums;

    /** 个人签署坐标位置 */
    private String personPicPoints;

    /** 法人key的序列号 */
    private String corSerial;

    /** 法人对象名称 */
    private String corSubject;

    /** 法人签章页码 */
    private String corPageNums;

    /** 法人签署坐标位置 */
    private String corPicPoints;

}
