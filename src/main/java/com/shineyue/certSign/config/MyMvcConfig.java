package com.shineyue.certSign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PackageName: com.shenyue.certSign.config
 * @Description:
 * @author: 罗绂威
 * @date: 2019/6/24
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/").setViewName("reportPDF");
        registry.addViewController("/up-download").setViewName("up-download");
        registry.addViewController("/reportPDF").setViewName("reportPDF");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/csa").setViewName("csa");
    }


}
