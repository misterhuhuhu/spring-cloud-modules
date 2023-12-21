## 路由处理 [CustomPredicatesConfig.java](..%2F..%2F..%2Fgithub%20project%2Ftutorials%2Fspring-cloud-modules%2Fspring-cloud-bootstrap%2Fgateway-2%2Fsrc%2Fmain%2Fjava%2Fcom%2Fbaeldung%2Fspringcloudgateway%2Fcustompredicates%2Fconfig%2FCustomPredicatesConfig.java)

### 使用 Fluent API 定义路由
```
@Bean
public RouteLocator routes(RouteLocatorBuilder builder, GoldenCustomerRoutePredicateFactory gf ) {
    return builder.routes()
      .route("dsl_golden_route", r -> 
        r.predicate(gf.apply(new Config(true, "customerId")))
         .and()
         .path("/dsl_api/**")
         .filters(f -> f.stripPrefix(1))
         .uri("https://httpbin.org")
      )
      .route("dsl_common_route", r -> 
         r.predicate(gf.apply(new Config(false, "customerId")))
          .and()
          .path("/dsl_api/**")
          .filters(f -> f.stripPrefix(1))
          .uri("https://httpbin.org")
          )             
      .build();
}
```
- Route  ― 网关的主要API。它由给定的标识 （ID）、目标 （URI） 以及一组谓词和筛选器定义。
- Predicate — Java 8 Predicate — 用于使用标头、方法或参数匹配 HTTP 请求
- Filter — 一个标准的 Spring WebFilter

## 动态路由 [application-customroutes.yml](..%2F..%2F..%2Fgithub%20project%2Ftutorials%2Fspring-cloud-modules%2Fspring-cloud-bootstrap%2Fgateway-2%2Fsrc%2Fmain%2Fresources%2Fapplication-customroutes.yml)

### 在 YAML 中定义路由
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: golden_route
        uri: https://httpbin.org
        predicates:
        - Path=/api/**
        - GoldenCustomer=true
        filters:
        - StripPrefix=1
        - AddRequestHeader=GoldenCustomer,true
      - id: common_route
        uri: https://httpbin.org
        predicates:
        - Path=/api/**
        - name: GoldenCustomer
          args:
            golden: false
            customerIdCookie: customerId
        filters:
        - StripPrefix=1
        - AddRequestHeader=GoldenCustomer,false
```
## [Routing Factories](https://www.baeldung.com/spring-cloud-gateway-routing-predicate-factories)
Spring Cloud Gateway 使用 Spring WebFlux HandlerMapping 基础结构匹配路由
它还包括许多内置的路由Predicate Factories。所有这些谓词都与 HTTP 请求的不同属性匹配。多个路由Predicate Factories可以通过逻辑“and”进行组合
## [WebFilter Factories](https://www.baeldung.com/spring-cloud-gateway-webfilter-factories)

## LoadBalancerClient Filter
如果 URL 具有 lb 方案（例如 lb://baeldung-service），它将使用 Spring Cloud LoadBalancerClient 将名称（即 baeldung-service）解析为实际的主机和端口

## Actuator API监控

- pom
``` 
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-gateway</artifactId>
            <version>2.0.0.RC2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
- application.yml
```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: baeldung_route
        uri: http://baeldung.com
        predicates:
        - Path=/baeldung/
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
- 检查
  访问 url “http://localhost/actuator/gateway/routes/baeldung_route” 

## 自定义 Routing Predicate Factories

[predicate](org.springframework.cloud.gateway.handler.predicate)
``` 
public class HeaderRoutePredicateFactory extends 
  AbstractRoutePredicateFactory<HeaderRoutePredicateFactory.Config> {

    // ... setup code omitted
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                // ... predicate logic omitted
            }
        };
    }

    @Validated
    public static class Config {
        public Config(boolean isGolden, String customerIdCookie ) {
          // ... constructor details omitted
        }
        // ...getters/setters omitted
    }
}
```
可以观察到几个关键点：
- 它扩展了 AbstractRoutePredicateFactory<T>，后者又实现了网关使用的 RoutePredicateFactory 接口
- apply 方法返回实际 Predicate 的实例 – 在本例中为 GatewayPredicate
- 谓词定义了一个内部 Config 类，该类用于存储测试逻辑使用的静态配置参数

如果我们看一下其他可用的 PredicateFactory，我们会发现基本模式基本相同：
- 定义一个 Config 类来保存配置参数
- 扩展 AbstractRoutePredicateFactory，使用配置类作为其模板参数
- 重写 apply 方法，返回实现所需测试逻辑的谓词


假设以下场景：对于给定的 API，调用我们必须在两个可能的后端之间进行选择。“黄金”客户是我们最有价值的客户，他们应该被路由到一个强大的服务器，可以访问更多的内存、更多的CPU和更快的磁盘。非黄金客户使用功能较弱的服务器，这会导致响应时间变慢。

若要确定请求是否来自黄金客户，我们需要调用一个服务，该服务采用与请求关联的 customerId 并返回其状态。至于 customerId，在我们的简单场景中，我们假设它在 cookie 中可用。

有了所有这些信息，我们现在可以编写自定义谓词了。我们将保留现有的命名约定，并将类命名为 GoldenCustomerRoutePredicateFactory：

