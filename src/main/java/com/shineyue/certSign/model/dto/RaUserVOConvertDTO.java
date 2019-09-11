package com.shineyue.certSign.model.dto;

import com.shineyue.certSign.model.bo.RaUserBO;
import org.springframework.beans.BeanUtils;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/8/30
 */
public class RaUserVOConvertDTO implements ConvertDTO {

    @Override
    public Object convert(Object o) {
        RaUserBO raUserBO = new RaUserBO();
        BeanUtils.copyProperties(o,raUserBO);
        return raUserBO;
    }
}
