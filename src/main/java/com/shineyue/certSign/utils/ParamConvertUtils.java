package com.shineyue.certSign.utils;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.dto.RaUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

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
}
