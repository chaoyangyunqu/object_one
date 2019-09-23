package com.shineyue.certSign.controller;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.vo.InManagerVO;
import com.shineyue.certSign.service.impl.ManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: TODO 管理员接口
 * @author: luofuwei
 * @date: wrote on 2019/9/6
 */
@RestController
public class ManagerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    ManagerServiceImpl managerService;

    /**
     * @Author luofuwei
     * @Description //TODO 管理员登陆
     * @Date 2019/9/6
     * @Param [managerPO]
     * @return com.shineyue.certSign.model.DataResult
     **/
    @PostMapping(value = "Manager/queryManager.service")
    public DataResult queryManager(@RequestBody InManagerVO inManagerVO){
        logger.info("登陆请求",inManagerVO);
        return managerService.managerQuery(inManagerVO);
    }
}
