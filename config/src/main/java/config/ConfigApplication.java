package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigApplication {
    
    @Value("${spring.cloud.config.server.git.uri}")
    private String uri;
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ConfigApplication.class, args);
        ConfigApplication bean = run.getBean(ConfigApplication.class);
        System.out.println(bean.getUri());
        System.out.println(bean.getUri());
        System.out.println(bean.getUri());
        System.out.println(bean.getUri());
    }
    
    public String getUri() {
        return uri;
    }
    
    public ConfigApplication setUri(String uri) {
        this.uri = uri;
        return this;
    }
}
