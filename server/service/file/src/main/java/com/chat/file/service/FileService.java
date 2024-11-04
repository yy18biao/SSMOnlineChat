package com.chat.file.service;

import com.chat.core.enums.ResCode;
import com.chat.oss.domain.OSSResp;
import com.chat.oss.service.OSSService;
import com.chat.security.exception.ServiceException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Resource
    private OSSService ossService;


    public OSSResp uploadFile(MultipartFile file) {
        try {
            return ossService.uploadFile(file);
        }catch (Exception e) {
            throw  new ServiceException(ResCode.FAILED_FILE_UPLOAD);
        }
    }
}
