package feign.hystrix;


import feign.client.JSONPlaceHolderClient;
import feign.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JSONPlaceHolderFallback implements JSONPlaceHolderClient {
    private static final Logger logger = LoggerFactory.getLogger(JSONPlaceHolderFallback.class);

    @Override
    public List<Post> getPosts() {
        return Collections.emptyList();
    }

    @Override
    public Post getPostById(Long postId) {
        logger.info("fallBack");
        return null;
    }
}
