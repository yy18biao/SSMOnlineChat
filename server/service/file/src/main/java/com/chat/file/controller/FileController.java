package com.chat.file.controller;

import com.chat.file.service.FileService;
import com.chat.oss.domain.OSSResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;

    @PostMapping("/uploadFile")
    public OSSResp uploadFile(MultipartFile file) {
        return fileService.uploadFile(file);
    }
}
