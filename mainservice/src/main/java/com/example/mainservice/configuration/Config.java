package com.example.mainservice.configuration;

import com.example.mainservice.utils.Utils;
import com.example.mainservice.utils.UtilsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Utils getUtils(){
        return new UtilsImpl();
    }



}
