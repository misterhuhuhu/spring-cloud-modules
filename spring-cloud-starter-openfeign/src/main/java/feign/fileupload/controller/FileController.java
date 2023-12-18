package feign.fileupload.controller;

import feign.fileupload.service.UploadService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    
    @Resource
    private UploadService service;
    
    @PostMapping(value = "/upload")
    public String handleFileUpload(@RequestPart(value = "file") MultipartFile file) {
        return service.uploadFileWithFallbackFactory(file);
    }
    
    @PostMapping(value = "/upload-mannual-client")
    public boolean handleFileUploadWithManualClient(
            @RequestPart(value = "file") MultipartFile file) {
        return service.uploadFileWithManualClient(file);
    }
    
    @PostMapping(value = "/upload-error")
    public String handleFileUploadError(@RequestPart(value = "file") MultipartFile file) {
        return service.uploadFileWithFallbackFactory(file);
    }
    
}