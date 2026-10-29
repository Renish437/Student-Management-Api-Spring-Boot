package com.renish.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {


  public void addCorsMappings(CorsRegistry registry){
      registry.addMapping("/**")
              .allowedOrigins("http://localhost:5173")
              .allowedHeaders("*")
              .allowedMethods("POST","GET","PUT","PATCH","OPTIONS","DELETE")
              .allowCredentials(true);
  }

}
