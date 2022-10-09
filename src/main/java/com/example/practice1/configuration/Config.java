package com.example.practice1.configuration;

import com.example.practice1.utils.Utils;
import com.example.practice1.utils.UtilsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Utils getUtils(){
        return new UtilsImpl();
    }



}
