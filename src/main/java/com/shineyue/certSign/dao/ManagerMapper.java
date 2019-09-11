package com.shineyue.certSign.dao;

import com.shineyue.certSign.model.po.ManagerPO;
import com.shineyue.certSign.model.vo.InManagerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/6
 */
@Mapper
public interface ManagerMapper {

    @Select("select id,username,password from sys_manager where username=#{username} and password=#{password}")
    ManagerPO queryManagerByUsernameAndPassword(InManagerVO inManagerVO);


}
