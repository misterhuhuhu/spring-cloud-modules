为演示路由需要启动两个服务[secondservice](customfilters%2Fsrc%2Fmain%2Fjava%2Fcustomfilters%2Fsecondservice)和[service](customfilters%2Fsrc%2Fmain%2Fjava%2Fcustomfilters%2Fservice)
<br>需要配置文件

- [secondservice-application.properties](..%2F..%2F..%2Fgithub%20project%2Ftutorials%2Fspring-cloud-modules%2Fspring-cloud-gateway%2Fsrc%2Fmain%2Fresources%2Fsecondservice-application.properties)
- [service-application.properties](..%2F..%2F..%2Fgithub%20project%2Ftutorials%2Fspring-cloud-modules%2Fspring-cloud-gateway%2Fsrc%2Fmain%2Fresources%2Fservice-application.properties)
- [application.yml](customfilters%2Fsrc%2Fmain%2Fresources%2Fapplication.yml) 打开日志

``` 
 o.s.c.g.handler.FilteringWebHandler      : Sorted gatewayFilterFactories: [[GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RemoveCachedBodyFilter@4dfe8b37}, order = -2147483648], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter@45117dd}, order = -2147482648], [GatewayFilterAdapter{delegate=customfilters.gatewayapp.filters.global.FirstPreLastPostGlobalFilter@715a70e9}, order = -1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyWriteResponseFilter@42507640}, order = -1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardPathFilter@4c302f38}, order = 0], [[RewritePath /service(?<segment>/?.*) = '${segment}'], order = 1], [customfilters.gatewayapp.filters.factories.LoggingGatewayFilterFactory$$Lambda$889/0x0000027aa553e600@50fc3fba, order = 1], [customfilters.gatewayapp.filters.factories.ModifyResponseGatewayFilterFactory$$Lambda$891/0x0000027aa553ea80@40c36a06, order = 3], [customfilters.gatewayapp.filters.factories.ModifyRequestGatewayFilterFactory$$Lambda$892/0x0000027aa553ece0@16c7d802, order = 4], [customfilters.gatewayapp.filters.factories.ChainRequestGatewayFilterFactory$$Lambda$893/0x0000027aa553ef40@56f50457, order = 5], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter@11dcd42c}, order = 10000], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.config.GatewayNoLoadBalancerClientAutoConfiguration$NoLoadBalancerClientFilter@2a47597}, order = 10150], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.WebsocketRoutingFilter@75aea2ba}, order = 2147483646], GatewayFilterAdapter{delegate=customfilters.gatewayapp.filters.global.LoggingGlobalPreFilter@3bc69ce9}, GatewayFilterAdapter{delegate=customfilters.gatewayapp.filters.global.LoggingGlobalFiltersConfigurations$$Lambda$676/0x0000027aa545dd10@abad89c}, [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyRoutingFilter@602f8f94}, order = 2147483647], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardRoutingFilter@d13baac}, order = 2147483647]]
```
## 全局 filter
### 全局[前置]filter
[LoggingGlobalPreFilter.java](customfilters%2Fsrc%2Fmain%2Fjava%2Fcustomfilters%2Fgatewayapp%2Ffilters%2Fglobal%2FLoggingGlobalPreFilter.java)
``` 
@Component
public class LoggingGlobalPreFilter implements GlobalFilter {

    final Logger logger = LoggerFactory.getLogger(LoggingGlobalPreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Global Pre Filter executed");
        return chain.filter(exchange);
    }
}
```

### 全局[后置]filter
[LoggingGlobalFiltersConfigurations.java](customfilters%2Fsrc%2Fmain%2Fjava%2Fcustomfilters%2Fgatewayapp%2Ffilters%2Fglobal%2FLoggingGlobalFiltersConfigurations.java)
``` 
@Configuration
public class LoggingGlobalFiltersConfigurations {

    final Logger logger = LoggerFactory.getLogger(LoggingGlobalFiltersConfigurations.class);

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                logger.info("Global Post Filter executed");
            }));
    }
}
```
### 前置后置组合
[FirstPreLastPostGlobalFilter.java](customfilters%2Fsrc%2Fmain%2Fjava%2Fcustomfilters%2Fgatewayapp%2Ffilters%2Fglobal%2FFirstPreLastPostGlobalFilter.java)
``` 
@Component
public class FirstPreLastPostGlobalFilter implements GlobalFilter, Ordered {

    final Logger logger = LoggerFactory.getLogger(FirstPreLastPostGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("First Pre Global Filter");
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                logger.info("Last Post Global Filter");
            }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
```
## GatewayFilters
### 使用GatewayFilterFactory

基本结构

[LoggingGatewayFilterFactory](customfilters/src/main/java/customfilters/gatewayapp/filters/factories/LoggingGatewayFilterFactory.java)
```
@Component
public class LoggingGatewayFilterFactory extends 
  AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    final Logger logger =
      LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // ...
    }

    public static class Config {
        // ...
    }
}
```
在配置中定义三个基本字段
```
public static class Config {
    private String baseMessage;
    private boolean preLogger;
    private boolean postLogger;

    // contructors, getters and setters...
}

```
这些字段是：

- 将包含在日志条目中的自定义消息
- 指示filter是否应在转发请求之前记录的标志
- 一个标志，指示filter在收到来自代理服务的响应后是否应记录

在配置文件中，传入属性
> spring.cloud.gateway.routes[0].filters[1]=Logging=My Custom Message, true, true

这样的话,必须重写 shortcutFieldOrder 方法，以指示快捷方式属性将使用的顺序和参数数量：
```
@Override
public List<String> shortcutFieldOrder() {
    return Arrays.asList("baseMessage",
      "preLogger",
      "postLogger");
}
```
或者
```
# Or, as an alternative:
#spring.cloud.gateway.routes[0].filters[1].name=Logging
#spring.cloud.gateway.routes[0].filters[1].args[baseMessage]=My Custom Message
#spring.cloud.gateway.routes[0].filters[1].args[preLogger]=true
#spring.cloud.gateway.routes[0].filters[1].args[postLogger]=true
```

或者
```
...
filters:
- RewritePath=/service(?<segment>/?.*), $\{segment}
- name: Logging
  args:
    baseMessage: My Custom Message
    preLogger: true
    postLogger: true
```


排序 filter
```
@Override
public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter((exchange, chain) -> {
        // ...
    }, 1);
}
```

编程方式使用
```
@Bean
public RouteLocator routes(RouteLocatorBuilder builder, LoggingGatewayFilterFactory loggingFactory) {
    return builder.routes().route("service_route_java_config", r -> r.path("/service/**")
                                                                            .filters(f -> f.rewritePath("/service(?<segment>/?.*)", "$\\{segment}")
                                                                                                  .filter(loggingFactory.apply(new Config("My Custom Message", true, true))))
                                                                            .uri("http://localhost:8081")).build();
}
```

## 高级场景


### 检查和修改请求

假设的场景:我们的服务过去常常根据区域设置查询参数提供其内容。然后，我们将 API 更改为使用 Accept-Language headers，但某些客户端仍在使用query param

按照以下逻辑配置网关进行规范化：
-如果我们收到 Accept-Language headers，我们希望保留它
-否则，请使用 locale 查询参数值
-如果也不存在，请使用默认区域设置
-最后，我们要删除 locale 查询参数

需要继承`AbstractGatewayFilterFactory`
```
@Component
public class ModifyRequestGatewayFilterFactory extends AbstractGatewayFilterFactory<ModifyRequestGatewayFilterFactory.Config> {
    
    final Logger logger = LoggerFactory.getLogger(ModifyRequestGatewayFilterFactory.class);
    
    public ModifyRequestGatewayFilterFactory() {
        
        super(Config.class);
        logger.info("Loaded ModifyRequestGatewayFilterFactory");
    }
    
    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("defaultLocale");
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getRequest().getQueryParams().forEach((k, v) -> logger.info("key : {}, value : {} ", k, v));//打印QueryParams
            
            if (exchange.getRequest().getHeaders().getAcceptLanguage().isEmpty()) {//如果存在 Accept-Language
                
                String queryParamLocale = exchange.getRequest().getQueryParams().getFirst("locale");//获取 QueryParams
                
                Locale requestLocale = Optional.ofNullable(queryParamLocale).map(k -> Locale.forLanguageTag(k))//将 locale QueryParams转为 Locale 类型
                                               .orElse(config.getDefaultLocale());
                
                exchange.getRequest().mutate()//使请求可修改
                        .headers(h -> h.setAcceptLanguageAsLocales(Collections.singletonList(requestLocale)));
            }
            
            String allOutgoingRequestLanguages = exchange.getRequest().getHeaders().getAcceptLanguage().stream().map(Locale.LanguageRange::getRange).collect(Collectors.joining(","));
            
            logger.info("original uri  : {}", exchange.getRequest().getURI());
            logger.info("Modify request output - Request contains Accept-Language header: {}", allOutgoingRequestLanguages);
            
            ServerWebExchange modifiedExchange = exchange.mutate()
                                                         //request(originalRequest -> originalRequest) 获取url
                                                         .request(originalRequest -> {
                                                             originalRequest.uri(UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).replaceQueryParams(new LinkedMultiValueMap<String, String>()).build().toUri());
                                                             
                                                         }) //删除url参数
                                                         .build();
            
            logger.info("Removed all query params: {}", modifiedExchange.getRequest().getURI());
            
            return chain.filter(modifiedExchange);
        };
    }
    
    public static class Config {
        private Locale defaultLocale;
        
        public Config() {
        }
        
        public Locale getDefaultLocale() {
            return defaultLocale;
        }
        
        public void setDefaultLocale(String defaultLocale) {
            this.defaultLocale = Locale.forLanguageTag(defaultLocale);
        }
        
        ;
    }
}
```

### 修改响应

同[检查和修改请求](#检查和修改请求)

[ModifyResponseGatewayFilterFactory](customfilters/src/main/java/customfilters/gatewayapp/filters/factories/ModifyResponseGatewayFilterFactory.java)
```
(exchange, chain) -> {
    return chain.filter(exchange)
      .then(Mono.fromRunnable(() -> {
          ServerHttpResponse response = exchange.getResponse();

          Optional.ofNullable(exchange.getRequest()
            .getQueryParams()
            .getFirst("locale"))
            .ifPresent(qp -> {
                String responseContentLanguage = response.getHeaders()
                  .getContentLanguage()
                  .getLanguage();

                response.getHeaders()
                  .add("Bael-Custom-Language-Header", responseContentLanguage);
                });
        }));
}
```

### 将请求链接到其他服务
[ChainRequestGatewayFilterFactory](customfilters/src/main/java/customfilters/gatewayapp/filters/factories/ChainRequestGatewayFilterFactory.java)


- [Spring Cloud Gateway WebFilter Factories](https://www.baeldung.com/spring-cloud-gateway-webfilter-factories)
- [Writing Custom Spring Cloud Gateway Filters](https://www.baeldung.com/spring-cloud-custom-gateway-filters)


## 自带的FilterFactory

在org.springframework.cloud.gateway.filter.factory

### 请求头
- AddRequestHeader 添加 Header
- MapRequestHeader 映射 Header
- SetRequestHeader 设置或替换 Header
- RemoveRequestHeader 删除 Header


### 响应头
- RewriteResponseHeader 重写  Header
- DedupeResponseHeader 重复数据删除 Header
- RewriteLocationResponseHeader 删除 修改Location  Header

### url path

- SetPath
- RewritePath
- PrefixPath
- StripPrefix

### http status
- RedirectTo
- SetStatus

## 