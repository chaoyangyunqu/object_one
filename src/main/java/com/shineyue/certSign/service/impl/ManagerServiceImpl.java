package com.shineyue.certSign.service.impl;

import com.shineyue.certSign.dao.ManagerMapper;
import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.po.ManagerPO;
import com.shineyue.certSign.model.vo.InManagerVO;
import com.shineyue.certSign.model.vo.OutManagerVO;
import com.shineyue.certSign.service.IManagerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/6
 */
@Service
public class ManagerServiceImpl  implements IManagerService {

    @Resource
    ManagerMapper managerMapper;

    @Override
    public DataResult managerQuery(InManagerVO inManagerVO) {
        DataResult dataResult = new DataResult();
        try{
            ManagerPO managerPO = managerMapper.queryManagerByUsernameAndPassword(inManagerVO);
            OutManagerVO outManagerVO = new OutManagerVO();
            // PO 对象转 VO对象
            BeanUtils.copyProperties(managerPO,outManagerVO);
            dataResult.setSuccess(true);
            dataResult.setMsg("查询成功");
            dataResult.setResults(outManagerVO);
        } catch (Exception e) {
            dataResult.setMsg("查询失败！");
            dataResult.setError("业务层异常："+e.getMessage());
        }
        return dataResult;
    }
}
