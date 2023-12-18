package feign.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public OkHttpClient client() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.build();
    }

 
}
