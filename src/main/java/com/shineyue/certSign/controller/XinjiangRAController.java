package com.shineyue.certSign.controller;

import com.shineyue.certSign.model.dto.RaUserDTO;
import com.shineyue.certSign.model.bo.RaUserBO;
import com.shineyue.certSign.model.dto.RaUserVOConvertDTO;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.vo.RaUserVO;
import com.shineyue.certSign.service.impl.XinjiangRAServiceImpl;
import com.shineyue.certSign.utils.ParamConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PackageName: com.shineyue.certSign.controller
 * @Description: TODO 证书接口
 * @author: 罗绂威
 * @date: wrote on 2019/8/28
 */
@RestController
public class XinjiangRAController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    XinjiangRAServiceImpl xinjiangRAService;

    /**
     * @Author luofuwei
     * @Description //TODO 发证
     * @Date 2019/9/8
     * @Param [raUserVO]
     * @return com.shineyue.certSign.model.DataResult
     **/
    @PostMapping(value = "CA/ra/dealCertSign.serivce")
    public DataResult dealCertSignRegister(@RequestBody RaUserVO raUserVO){

        DataResult dataResult = new DataResult();

        try{

            String req = raUserVO.getReq();
            logger.info("req:{}",req);
            if (null == req ||"".equals(req)) {
                dataResult.setError("req不能为空");
                return dataResult;
            }
            // 参数转换
            dataResult = ParamConvertUtils.CaseUnderlineConvert(raUserVO);
            if (100001 != dataResult.getStatus()) {
                return dataResult;
            }
            /** 获取转换后的DTO对象 */
            RaUserDTO raUserDTO = (RaUserDTO) dataResult.getResults();
            RaUserVOConvertDTO raUserVOConvert = new RaUserVOConvertDTO();
            RaUserBO raUserBO = (RaUserBO) raUserVOConvert.convert(raUserVO);
            raUserBO.setRaUserDTO(raUserDTO);

            dataResult = xinjiangRAService.dealCertSign(raUserBO);

        }catch(Exception e){

            dataResult.setStatus(200001);
            dataResult.setMsg("对象转换错误");
            dataResult.setError(e.getMessage());
        }

        return dataResult;
    }


    public DataResult installCertTag(){
        return null;
    }
}
