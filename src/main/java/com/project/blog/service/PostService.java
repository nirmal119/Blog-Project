package com.project.blog.service;

import com.project.blog.entity.Post;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostService {
    public void save(Post post);

    Optional<Post> findPostById(Long id);

    Page<Post> getAllPosts(int pageNo, String sortField, String sortDirection);

    Page<Post> searchPosts(int pageNo, String sortField, String sortDirection, String search);

    void delete(Post post);

    List<String> getAuthors();

    List<Date> getPublishDate();

    List<Post> getPostByAuthors(String author);

    List<Post> getPostByTag(String tag);

    List<Post> getPostByPublishedAt(Date date);

    Page<Post> getPageOfFilteredPost(List<String> authors, List<String> tags, List<Date> publishedAt, int pageNo, String sortField, String sortDirection);
}