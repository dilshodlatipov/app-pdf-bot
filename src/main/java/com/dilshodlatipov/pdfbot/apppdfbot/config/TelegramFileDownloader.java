package com.dilshodlatipov.pdfbot.apppdfbot.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "telegramFileDownloader", url = "${telegram.api-url}")
public interface TelegramFileDownloader {
    @GetMapping("/file/bot${telegram.bot-token}/{filePath}")
    byte[] downloadFile(@PathVariable("filePath") String filePath);
}
