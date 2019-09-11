package com.shineyue.certSign.model.bo;

import com.shineyue.certSign.model.BasicVOAndBO;
import com.shineyue.certSign.model.dto.RaUserDTO;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/8/30
 */
public class RaUserBO extends BasicVOAndBO {

    RaUserDTO raUserDTO;

    public RaUserDTO getRaUserDTO() {
        return raUserDTO;
    }

    public void setRaUserDTO(RaUserDTO raUserDTO) {
        this.raUserDTO = raUserDTO;
    }

}
