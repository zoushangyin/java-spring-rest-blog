package com.pluralsight.blog.data;

import com.pluralsight.blog.model.Author;
import com.pluralsight.blog.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PostRepository {
    public List<Post> findAll() {
        return null;
    }

    public List<Post> saveAll(List<Post> posts) {
        return null;
    }

    public Post save(Post post) {
        return null;
    }

    public Optional<Post> findById(Long id) {
        return null;
    }
}