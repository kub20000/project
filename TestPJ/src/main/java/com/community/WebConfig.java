package com.community;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String EXTERNAL_UPLOAD_PATH = "file:///C:/kkh/webDev/workspace/TestPJ/build/resources/main/static/uploads";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(EXTERNAL_UPLOAD_PATH);
    }
}