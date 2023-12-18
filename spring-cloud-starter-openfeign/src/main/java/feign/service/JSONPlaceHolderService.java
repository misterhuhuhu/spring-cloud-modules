package feign.service;


import feign.model.Post;

import java.util.List;

public interface JSONPlaceHolderService {

    List<Post> getPosts();

    Post getPostById(Long id);
}
