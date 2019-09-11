package com.shineyue.certSign.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/11
 */
public class ClassPathResourceReadeUtils {
    /**
     * path:文件路径
     * @since JDK 1.8
     */
    private final String path;

    /**
     * content:文件内容
     * @since JDK 1.6
     */
    private String content;

    public ClassPathResourceReadeUtils(String path) {
        this.path = path;
    }

    public String getContent() {
        if (content == null) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                System.out.println("路径测试:"+resource.getPath());
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                content = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
            } catch ( IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return content;
    }
}
