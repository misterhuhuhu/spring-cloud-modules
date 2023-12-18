## 简单使用feign

1. 定义Feign  client
```
public interface BookClient {
    @RequestLine("GET /{isbn}")
    BookResource findByIsbn(@Param("isbn") String isbn);

    @RequestLine("GET")
    List<BookResource> findAll();

    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    void create(Book book);
}
```
2. 使用feign 包装 client

``` 
BookClient bookClient = Feign.builder()
  .client(new OkHttpClient())
  .encoder(new GsonEncoder())
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger(BookClient.class))
  .logLevel(Logger.Level.FULL)
  .target(BookClient.class, "http://localhost:8081/api/books");
```


或者引入
```
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-ribbon</artifactId>
    <version>${feign.version}</version>
</dependency>
```
使用`RibbonClient.create()` 实现 负载均衡或服务发现
``` 
BookClient bookClient = Feign.builder()
  .client(RibbonClient.create())
  .target(BookClient.class, "http://localhost:8081/api/books");
```

## 重试器

1. 简单的 Retryer 实现
    ``` 
    public class NaiveRetryer implements feign.Retryer {
        @Override
        public void continueOrPropagate(RetryableException e) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    
        @Override
        public Retryer clone() {
            return new NaiveRetryer();
        }
    }
    ```
2. 将实现添加到客户端构建器
    ``` 
    public static <T> T createClient(Class<T> type, String uri) {
        return Feign.builder()
          // ...
          .retryer(new NaiveRetryer())    
          // ...
    }
    ```
3. 默认实现
    ``` 
    public static <T> T createClient(Class<T> type, String uri) {
        return Feign.builder()
    // ...
          .retryer(new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5))    
    // ...
    } 
    ```
4. 不重试
   如果我们不希望 Feign 重试任何调用，我们可以向客户端构建器提供Retryer.NEVER_RETRY实现
##  使用 Feign 设置请求标头
1. 静态Headers
    ``` 
    @Headers("Accept-Language: en-US")
    public interface BookClient {
        
        @RequestLine("GET /{isbn}")
        BookResource findByIsbn(@Param("isbn") String isbn);
    
        @RequestLine("POST")
        @Headers("Content-Type: application/json")
        void create(Book book);
    }
    ```
2. 动态Headers
    ``` 
    @Headers("x-requester-id: {requester}")
    public interface BookClient {
       
        @RequestLine("GET /{isbn}")
        BookResource findByIsbn(@Param("requester") String requester, @Param("isbn") String isbn);
    } 
    ```
3. HeaderMaps 注解
    ``` 
    @RequestLine("POST")
    void create(@HeaderMap Map<String, Object> headers, Book book);
    ```
    ```
    Map<String,Object> headerMap = new HashMap<>();
    headerMap.put("metadata-key1", "metadata-value1");
    headerMap.put("metadata-key2", "metadata-value2");
    bookClient.create(headerMap, book);
    ```
4. RequestInterceptor
```
public class AuthRequestInterceptor implements RequestInterceptor {
    private AuthorisationService authTokenService;
   
    public AuthRequestInterceptor(AuthorisationService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorisation", authTokenService.getAuthToken());
    }
}

Feign.builder()
  .requestInterceptor(new AuthInterceptor(new ApiAuthorisationService()))
  .encoder(new GsonEncoder())
  .decoder(new GsonDecoder())
  .logger(new Slf4jLogger(type))
  .logLevel(Logger.Level.HEADERS)
  .target(BookClient.class, "http://localhost:8081/api/books");


```
## Netflix Feign 和 OpenFeign
``` 
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```
基本相同 
openfeign 支持 Micrometer, Dropwizard Metrics, Apache HTTP Client 5, Google HTTP client

