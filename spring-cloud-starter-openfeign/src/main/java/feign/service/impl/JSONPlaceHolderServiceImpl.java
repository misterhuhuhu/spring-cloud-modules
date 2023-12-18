package feign.service.impl;

import feign.client.JSONPlaceHolderClient;
import feign.model.Post;
import feign.service.JSONPlaceHolderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JSONPlaceHolderServiceImpl implements JSONPlaceHolderService {

    @Resource
    private JSONPlaceHolderClient jsonPlaceHolderClient;

    @Override
    public List<Post> getPosts() {
        return jsonPlaceHolderClient.getPosts();
    }

    @Override
    public Post getPostById(Long id) {
        return jsonPlaceHolderClient.getPostById(id);
    }
}
