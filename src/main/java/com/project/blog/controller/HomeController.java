package com.project.blog.controller;

import com.project.blog.entity.Post;
import com.project.blog.service.PostService;
import com.project.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class HomeController {
    private final PostService postService;

    @Autowired
    public HomeController(PostService postService, TagService tagService) {
        this.postService = postService;
    }

    @GetMapping
    public Page<Post> home(@RequestParam(value = "start", defaultValue = "1") int start,
                                             @RequestParam(value = "sortField", defaultValue = "publishedAt") String sortField,
                                             @RequestParam(value = "order", defaultValue = "desc") String order) {

        return postService.getAllPosts(start, sortField, order);
    }

    @GetMapping("/search")
    public Page<Post> search(@RequestParam(value = "start", defaultValue = "1") int start,
                       @RequestParam(value="sortField", defaultValue = "publishedAt") String sortField,
                       @RequestParam(value = "order", defaultValue = "desc") String order,
                             @RequestParam(value = "search", required = false) String search,
                       Model model) {

        return postService.searchPosts(start, sortField, order, search);
    }

    @GetMapping("/filter")
    public Page<Post> filter(@RequestParam(value = "start", defaultValue = "1") int start,
                         @RequestParam(value="sortField", defaultValue = "publishedAt") String sortField,
                         @RequestParam(value = "order", defaultValue = "desc") String order,
                         @RequestParam(value = "author", required = false) List<String> authors,
                         @RequestParam(value = "tag", required = false) List<String> tags,
                         @RequestParam(value = "publishedAt", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<Date> publishedAt
                             ) throws ParseException {

        if(publishedAt == null){
            publishedAt = new ArrayList<>();
            String date = "0001-02-01";
            Date defaultDate=new SimpleDateFormat("yyyy-MM-dd").parse(date);
            publishedAt.add(defaultDate);
        }
        if(authors == null){
            authors = new ArrayList<>();
            authors.add("~");
        }if(tags == null){
            tags = new ArrayList<>();
            tags.add("~");
        }

        return postService.getPageOfFilteredPost(authors, tags, publishedAt, start, sortField, order);
    }

    private boolean isPrincipalOwnerOfPost(Principal principal, Post post) {
        return principal != null && principal.getName().equals(post.getAuthor());
    }

    private boolean hasRole()
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}