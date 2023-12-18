package feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class CustomerRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("user", "ajeje");
        requestTemplate.header("password", "brazof");
        requestTemplate.header("Accept", "application/json");
    }
}
