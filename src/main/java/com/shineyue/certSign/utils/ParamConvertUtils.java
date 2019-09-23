package com.shineyue.certSign.utils;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.RaUserDTO;
import com.shineyue.certSign.model.dto.SignContractDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.shineyue.certSign.service.impl.XinjiangCAServiceImpl.PERSONSIGN;
import static com.shineyue.certSign.service.impl.XinjiangCAServiceImpl.PERSONSIGNCURRENT;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/8
 */
public class ParamConvertUtils<T> {

    private static final Logger logger = LoggerFactory.getLogger(ParamConvertUtils.class);

    public static <T> DataResult CaseUnderlineConvert(T t){

        DataResult dataResult = new DataResult();

        // 用于传输到第三方系统的数据对象
        RaUserDTO raUserDTO = new RaUserDTO();

        try {

            // 获得RaUserVO的所有声明的属性名和属性值，进行参数处理
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields){
                field.setAccessible(true);
                if ("ignoreUserIdNo".equals(field.getName())) {
                    raUserDTO.addValue("ignore_USER_IDNO",field.get(t));
                } else {
                    String getFieldName = field.getName();
                    char[] chars = getFieldName.toCharArray();
                    // 记录大写位置
                    int[] upperCaseIndex = new int[10];
                    int j = 0;
                    /** 查找大写字符位置 */
                    for (int i=0; i<chars.length; i++) {
                        // 如果是大写字母
                        if (Character.isUpperCase(chars[i])) {
                            upperCaseIndex[j] = i;
                            j++;
                        }
                    }
                    StringBuffer fieldName = new StringBuffer();
                    fieldName.append(getFieldName.toUpperCase());
                    // 在记录大写位置处插入'_'字符
                    for (int i=0;i<upperCaseIndex.length;i++){
                        if (upperCaseIndex[i] != 0){
                            if (i<=0){
                                fieldName.insert(upperCaseIndex[i],'_');
                            }else {
                                fieldName.insert(upperCaseIndex[i]+i,'_');
                            }
                        }
                    }

                    raUserDTO.addValue(fieldName.toString(),field.get(t));
                }
            }

            dataResult.setStatus(100001);
            dataResult.setResults(raUserDTO);

            return dataResult;

        } catch ( IllegalAccessException e ) {

            dataResult.setStatus(200004);
            dataResult.setMsg("参数大小写与下划线转换失败！");
            dataResult.setError(e.getMessage());

            return dataResult;
        }
    }


    public static DataResult dealPersonSign1(SignContractDTO signContractDTO){
        DataResult dataResult = new DataResult();
        List<SignContractDTO> list = new ArrayList<SignContractDTO>();
        try {
            // 处理个人签章多人签署
            String[] personPageNumsGroup = signContractDTO.getPersonPageNums().split("#");
            String[] namesGroup = signContractDTO.getPersonName().split("#");
            String[] phonesGroup = signContractDTO.getPersonPhone().split("#");
            String[] idCardsGroup = signContractDTO.getPersonIdCard().split("#");
            String[] personPicPointsGroup = signContractDTO.getPersonPicPoints().split("#");

            for (int i = 0; i < personPageNumsGroup.length; i++) {
                String[] names = namesGroup[i].split("\\|");
                String[] phones = phonesGroup[i].split("\\|");
                String[] idCards = idCardsGroup[i].split("\\|");
                String[] personPicPoints = personPicPointsGroup[i].split("\\|");
                // 存储多个信息、位置
                for (int j = 0; j < personPicPoints.length; j++) {
                    SignContractDTO signContractDTO1 = new SignContractDTO();
                    signContractDTO1.setPersonPageNums(personPageNumsGroup[i]);
                    BeanUtils.copyProperties(signContractDTO, signContractDTO1, "personName", "personPhone", "personIdCard", "personPicPoints","personPageNums");
                    signContractDTO1.setPersonPicPoints(personPicPoints[j]);
                    if (j >= names.length) {
                        signContractDTO1.setPersonName("");
                    } else {
                        signContractDTO1.setPersonName(names[j]);
                    }
                    if (j >= phones.length) {
                        signContractDTO1.setPersonPhone("");
                    } else {
                        signContractDTO1.setPersonPhone(phones[j]);
                    }
                    if (j >= idCards.length) {
                        signContractDTO1.setPersonIdCard("");
                    } else {
                        signContractDTO1.setPersonIdCard(idCards[j]);
                    }
                    list.add(signContractDTO1);
                }
            }
            PERSONSIGN.put(signContractDTO.getWqhth(), list);
            PERSONSIGNCURRENT.put(signContractDTO.getWqhth(), 0);
        } catch ( Exception e ) {
            dataResult.setStatus(400001);
            dataResult.setMsg("事件处理个人签署出错");
            dataResult.setError(e.getMessage());
        }

        return dataResult;
    }
    public static DataResult dealPersonSign(SignContractDTO signContractDTO){
        DataResult dataResult = new DataResult();
        List<SignContractDTO> list = new ArrayList<SignContractDTO>();
        try {
            // 处理个人签章多人签署
            String[] personPageNumsGroup = signContractDTO.getPersonPageNums().split("#");
            String[] namesGroup = signContractDTO.getPersonName().split("#");
            String[] phonesGroup = signContractDTO.getPersonPhone().split("#");
            String[] idCardsGroup = signContractDTO.getPersonIdCard().split("#");
            String[] personPicPointsGroup = signContractDTO.getPersonPicPoints().split("#");

            int personCount = personPicPointsGroup.length;

            if (personCount == 2) {
                if (idCardsGroup[0].equals(idCardsGroup[1])) {
                    String[] names = namesGroup[0].split("\\|");
                    String[] phones = phonesGroup[0].split("\\|");
                    String[] idCards = idCardsGroup[0].split("\\|");
                    String personPageNums = signContractDTO.getPersonPageNums();

                    for (int i = 0; i<idCards.length;i++) {
                        String[] personPicPoint1 = personPicPointsGroup[0].split("\\|");
                        String[] personPicPoint2 = personPicPointsGroup[1].split("\\|");
                        String personPicPoints = personPicPoint1[i] + "#" + personPicPoint2[i] ;
                        SignContractDTO signContractDTO1 = new SignContractDTO();
                        BeanUtils.copyProperties(signContractDTO, signContractDTO1, "personName", "personPhone", "personIdCard", "personPicPoints","personPageNums");
                        if (i >= names.length) {
                            signContractDTO1.setPersonName("");
                        } else {
                            signContractDTO1.setPersonName(names[i]);
                        }
                        if (i >= phones.length) {
                            signContractDTO1.setPersonPhone("");
                        } else {
                            signContractDTO1.setPersonPhone(phones[i]);
                        }
                        if (i >= idCards.length) {
                            signContractDTO1.setPersonIdCard("");
                        } else {
                            signContractDTO1.setPersonIdCard(idCards[i]);
                        }
                        signContractDTO1.setPersonPageNums(personPageNums);
                        signContractDTO1.setPersonPicPoints(personPicPoints);
                        list.add(signContractDTO1);
                    }
                    PERSONSIGN.put(signContractDTO.getWqhth(), list);
                    PERSONSIGNCURRENT.put(signContractDTO.getWqhth(), 0);
                }
            }else {
            
                for (int i = 0; i < personPageNumsGroup.length; i++) {
                    String[] names = namesGroup[i].split("\\|");
                    String[] phones = phonesGroup[i].split("\\|");
                    String[] idCards = idCardsGroup[i].split("\\|");
                    String[] personPicPoints = personPicPointsGroup[i].split("\\|");
                    // 存储多个信息、位置
                    for (int j = 0; j < personPicPoints.length; j++) {
                        SignContractDTO signContractDTO1 = new SignContractDTO();
                        signContractDTO1.setPersonPageNums(personPageNumsGroup[i]);
                        BeanUtils.copyProperties(signContractDTO, signContractDTO1, "personName", "personPhone", "personIdCard", "personPicPoints","personPageNums");
                        signContractDTO1.setPersonPicPoints(personPicPoints[j]);
                        if (j >= names.length) {
                            signContractDTO1.setPersonName("");
                        } else {
                            signContractDTO1.setPersonName(names[j]);
                        }
                        if (j >= phones.length) {
                            signContractDTO1.setPersonPhone("");
                        } else {
                            signContractDTO1.setPersonPhone(phones[j]);
                        }
                        if (j >= idCards.length) {
                            signContractDTO1.setPersonIdCard("");
                        } else {
                            signContractDTO1.setPersonIdCard(idCards[j]);
                        }
                        list.add(signContractDTO1);
                    }
                }
                PERSONSIGN.put(signContractDTO.getWqhth(), list);
                PERSONSIGNCURRENT.put(signContractDTO.getWqhth(), 0);
            }
        } catch ( Exception e ) {
            dataResult.setStatus(400001);
            dataResult.setMsg("事件处理个人签署出错");
            dataResult.setError(e.getMessage());
        }

        return dataResult;
    }
}
