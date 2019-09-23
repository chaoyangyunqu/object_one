package com.shineyue.certSign.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: TODO 网签合同数据传输对象
 * @author: luofuwei
 * @date: wrote on 2019/9/7
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignContractDTO {
    /** 网签合同号 */
    @NotBlank(message = "网签合同号不能为空!")
    private String wqhth;

    /** PDF文件 */
    private String inputPDF;

    /** key的序列号 */
    @NotBlank(message = "证书序列号不能为空")
    private String serial;

    /** 对象名称 */
    @NotBlank(message = "对象名称不能为空")
    private String subject;

    /** 用户类型(1.个人/2.企业/3.房管局) */
    private int type;

    /** 页码,可多个 */
    @NotBlank(message = "页码不能为空")
    private String pageNums;

    /** 签章位置 */
    @NotBlank(message = "企业/机构坐标位置不能为空")
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
