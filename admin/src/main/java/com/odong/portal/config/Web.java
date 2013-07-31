package com.odong.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午2:26
 */
@EnableWebMvc
@Configuration("config.web")
public class Web extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/main");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/jquery/**").addResourceLocations("/WEB-INF/jquery/");
        registry.addResourceHandler("/bootstrap/**").addResourceLocations("/WEB-INF/bs-v3.0.0-rc1/");
        registry.addResourceHandler("/ueditor/**").addResourceLocations("/WEB-INF/ueditor1_2_6_1-utf8/");
        registry.addResourceHandler("/highcharts/**").addResourceLocations("/WEB-INF/Highcharts-3.0.1/");
        registry.addResourceHandler("/style/**").addResourceLocations("/WEB-INF/style/");
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");


    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
