> 使用 https://jsonplaceholder.typicode.com/ 测试

1. feign client
   > [JSONPlaceHolderClient.java](src%2Fmain%2Fjava%2Ffeign%2Fclient%2FJSONPlaceHolderClient.java)
2. 自定义 Bean 配置
   > [ClientConfiguration.java](src%2Fmain%2Fjava%2Ffeign%2Fconfig%2FClientConfiguration.java)
3. 实现 RequestInterceptor
   > [ClientConfiguration.java](src%2Fmain%2Fjava%2Ffeign%2Fconfig%2FClientConfiguration.java)
4. 配置文件 RequestInterceptor
   > spring.cloud.openfeign.client.config.default.requestInterceptors=feign.config.CustomerRequestInterceptor
5. 配置ErrorDecoder 进行全局异常处理
   > [CustomErrorDecoder.java](src%2Fmain%2Fjava%2Ffeign%2Fconfig%2FCustomErrorDecoder.java)
   ``` 
   @Bean
   public ErrorDecoder errorDecoder() {
     return new CustomErrorDecoder();
   }
   ```
6. Sentinel提换  hystrix 断路器  
   1. 实现 fallback
       ``` 
       @FeignClient(value = "jplaceholder",
               url = "${external.api.url}",
               configuration = ClientConfiguration.class,
               fallback = JSONPlaceHolderFallback.class)
       public interface JSONPlaceHolderClient
       ```
   2. 引入sentinel
      ``` 
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>      
      ```
   3. 开启
      > spring.cloud.openfeign.circuitbreaker.enabled=true
## 通过 Feign 客户端上传文件 
1. pom
   ``` 
   <dependency>
       <groupId>io.github.openfeign.form</groupId>
       <artifactId>feign-form-spring</artifactId>
   </dependency>
   ```
2. 配置
    ``` 
    public class FeignSupportConfig {
        @Bean
        public Encoder multipartFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(() -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
        }
    
        @Bean
        public ErrorDecoder errorDecoder() {
            return new RetreiveMessageErrorDecoder();
        }
    }
    ```
3. 对指定client使用
   ``` 
   @FeignClient(name = "file", url = "http://localhost:8081", configuration = FeignSupportConfig.class)
   public interface UploadClient {
       @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       String fileUpload(@RequestPart(value = "file") MultipartFile file);
   }
   ```
或者通过 Feign.builder 定制
1. feign client
```
public interface UploadResource {
    @RequestLine("POST /upload-file")
    @Headers("Content-Type: multipart/form-data")
    Response uploadFile(@Param("file") MultipartFile file);
}
```
2. 手动上传
``` 
    public boolean uploadFileWithManualClient(MultipartFile file) {
        UploadResource fileUploadResource = Feign.builder().encoder(new SpringFormEncoder())
                .target(UploadResource.class, HTTP_FILE_UPLOAD_URL);
        Response response = fileUploadResource.uploadFile(file);
        return response.status() == 200;
    }
```
## feign 获取原始异常
1.  指定ErrorDecoder
   [RetreiveMessageErrorDecoder.java](src%2Fmain%2Fjava%2Ffeign%2Ffileupload%2Fconfig%2FRetreiveMessageErrorDecoder.java)
## patch请求报错
1. pom
   ``` 
   <dependency>
       <groupId>io.github.openfeign</groupId>
       <artifactId>feign-okhttp</artifactId>
   </dependency>
   ```
2. 配置文件
   > spring.cloud.openfeign.okhttp.enabled=true
   > 
## spring open feign
1. FeignClientsConfiguration
      
   上面的类包含以下 bean
    - Decoder – ResponseEntityDecoder，它包装了 SpringDecoder，用于解码 Response
    - Encoder – SpringEncoder 用于对 RequestBody 进行编码。
    - Logger – Slf4jLogger 是 Feign 使用的默认记录器。
    - Contract – SpringMvcContract，提供注解处理
    - Feign-Builder – HystrixFeign.Builder 用于构造组件。
    - Client – LoadBalancerFeignClient 或默认 Feign 客户端