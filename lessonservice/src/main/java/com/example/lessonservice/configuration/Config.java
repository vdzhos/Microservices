package com.example.lessonservice.configuration;

import com.example.lessonservice.utils.Utils;
import com.example.lessonservice.utils.UtilsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Utils getUtils(){
        return new UtilsImpl();
    }



}
