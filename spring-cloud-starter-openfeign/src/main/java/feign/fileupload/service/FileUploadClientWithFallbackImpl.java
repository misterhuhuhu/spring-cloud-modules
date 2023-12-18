package feign.fileupload.service;


import feign.exception.BadRequestException;
import feign.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadClientWithFallbackImpl implements FileUploadClientWithFallBack {

    @Override
    public String fileUpload(MultipartFile file) {
        try {
            throw new NotFoundException("hi, something wrong");
        } catch (Exception ex) {
            
            if (ex instanceof NotFoundException) {
                return "Not Found!!!";
            }
            return "Exception!!!";
        }
    }

}
