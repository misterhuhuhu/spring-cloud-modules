package feign;


import feign.model.Post;
import feign.service.JSONPlaceHolderService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = ExampleApplication.class)
public class ExampleApplicationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplicationTest.class);
    @Resource
    private JSONPlaceHolderService jsonPlaceHolderService;
    
    @Test
    public void getPost() {
        List<Post> posts = jsonPlaceHolderService.getPosts();
//        logger.info("getPosts {}\n",posts.stream().map(Object::toString).collect(Collectors.joining(",\n")));
        Post postById = jsonPlaceHolderService.getPostById(5000L);
        logger.info("getPostById {}\n",postById);
    }
}