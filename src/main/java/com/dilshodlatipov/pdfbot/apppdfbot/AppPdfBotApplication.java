package com.dilshodlatipov.pdfbot.apppdfbot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableFeignClients
@RequiredArgsConstructor
public class AppPdfBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppPdfBotApplication.class, args);
    }
}
