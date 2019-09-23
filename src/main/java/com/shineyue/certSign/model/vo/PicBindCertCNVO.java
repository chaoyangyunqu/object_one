package com.shineyue.certSign.model.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: TODO 电子章绑定
 * @author: luofuwei
 * @date: wrote on 2019/9/9
 */
@Data
public class PicBindCertCNVO {

    /** 对象名称 */
    private String subject;

    /** 序列号/唯一号 */
    private String serial;

    /** 企业联系人手机号 */
    private String entPhone;

}
