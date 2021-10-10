package com.project.blog.service.impl;

import com.project.blog.service.PostService;
import com.project.blog.entity.Post;
import com.project.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository thePostRepository) {
        postRepository = thePostRepository;
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Page<Post> getAllPosts(int pageNo, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        return postRepository.findAll(PageRequest.of(pageNo-1, 5, sort));
    }

    @Override
    public Page<Post> searchPosts(int pageNo, String sortField, String sortDirection, String search) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        return postRepository.findAll(search,PageRequest.of(pageNo-1, 4, sort));
    }

    @Override
    public void delete(Post post) {
        postRepository.delete(post);
    }

    @Override
    public List<String> getAuthors() {
        return postRepository.findAllAuthors();
    }

    @Override
    public List<Date> getPublishDate() {
        return postRepository.findAllPublishedAt();
    }

    @Override
    public List<Post> getPostByAuthors(String author) {
        return postRepository.findAllByAuthor(author);
    }

    @Override
    public List<Post> getPostByTag(String tag) {
        return postRepository.findAll(tag);
    }

    @Override
    public List<Post> getPostByPublishedAt(Date date) {
        return postRepository.findAllByPublishedAt(date);
    }

    public Page<Post> getPageOfFilteredPost(List<String> authors, List<String> tags, List<Date> publishedAt, int pageNumber, String sortField, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Set<Post> postSet = new HashSet<>();
        for(String author : authors){
            List<Post> posts = getPostByAuthors(author);
            postSet.addAll(posts);
        }
        for(String tag : tags){
            List<Post> posts = getPostByTag(tag);
            postSet.addAll(posts);
        }
        for(Date date : publishedAt){
            List<Post> posts = getPostByPublishedAt(date);
            postSet.addAll(posts);
        }
        List<Post> postsList = new ArrayList<>(postSet);
        int pageOffset = (int) PageRequest.of(pageNumber-1, 4).getOffset();
        int pageEnd = (pageOffset + PageRequest.of(pageNumber-1, 4).getPageSize()) > postsList.size() ? postsList.size() : (pageNumber-1 + PageRequest.of(pageNumber-1, 4).getPageSize());

        PageRequest pageRequest = PageRequest.of(pageNumber-1, 1, sort);
        return new PageImpl<>(postsList.subList(pageOffset, pageEnd), pageRequest, postsList.size());
    }
}