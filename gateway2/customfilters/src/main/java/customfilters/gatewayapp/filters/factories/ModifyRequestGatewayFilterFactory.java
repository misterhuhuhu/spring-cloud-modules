package customfilters.gatewayapp.filters.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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
