package feign.config;


import feign.Response;
import feign.codec.ErrorDecoder;
import feign.exception.BadRequestException;
import feign.exception.NotFoundException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return switch (response.status()) {
            case 400 -> new BadRequestException();
            case 404 -> new NotFoundException();
            default -> new Exception("Generic error");
        };
    }
}
