//package com.shineyue.certSign;
//
//import com.shineyue.certSign.dao.ManagerMapper;
//import com.shineyue.certSign.dao.XinjiangRAMapper;
//import com.shineyue.certSign.model.po.CertRegisterPO;
//import com.shineyue.certSign.service.impl.XinjiangRAServiceImpl;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class StartApplicationTests {
//
//    private final Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Resource
//    XinjiangRAServiceImpl xinjiangRAService;
//
//    @Resource
//    ManagerMapper managerMapper;
//
//    @Resource
//    XinjiangRAMapper mapper;
//
//    @Test
//    public void contextLoads() {
//        CertRegisterPO certRegisterPO = new CertRegisterPO();
//        certRegisterPO.setRid(1123);
//        // 将userId存入数据库
//        int row1 = mapper.insertUserIdToCertRegister(certRegisterPO);
//        logger.info(":{}",row1);
//
//    }
//
//}
