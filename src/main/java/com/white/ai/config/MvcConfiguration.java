package com.white.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    /**
     * 配置跨域资源共享（CORS）映射
     * 允许所有来源、所有 HTTP 方法、所有请求头访问本应用的任意端点
     * 
     * @param registry CorsRegistry 对象，用于注册跨域映射配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 为所有路径添加跨域映射配置
        registry.addMapping("/**")
                // 允许所有来源访问
                .allowedOrigins("*")
                // 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
                .allowedMethods("*")
                // 允许所有请求头
                .allowedHeaders("*");

    }
}
