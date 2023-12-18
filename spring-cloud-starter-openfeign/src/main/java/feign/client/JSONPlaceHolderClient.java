package feign.client;


import feign.config.ClientConfiguration;
import feign.hystrix.JSONPlaceHolderFallback;
import feign.model.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "jplaceholder",
        url = "${external.api.url}",
        configuration = ClientConfiguration.class,
        fallback = JSONPlaceHolderFallback.class)
public interface JSONPlaceHolderClient {
    
    @GetMapping(value = "/posts")
    List<Post> getPosts();
    
    @GetMapping(value = "/posts/{postId}", produces = "application/json")
    Post getPostById(@PathVariable("postId") Long postId);
    
}
