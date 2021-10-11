package com.project.blog.controller;

import com.project.blog.entity.Comment;
import com.project.blog.entity.Post;
import com.project.blog.entity.Tag;
import com.project.blog.entity.User;
import com.project.blog.service.CommentService;
import com.project.blog.service.PostService;
import com.project.blog.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private Object ObjectNotFoundException;
    private Object UsernameNotFoundException;

    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/create")
    public Map<String,Object> createPost(Principal principal) throws Throwable {
        Post post = new Post();
        Tag tag = new Tag();
        Optional<User> userOptional = userService.findByName(principal.getName());
        Map<String,Object> defaultSettings = new HashMap<>();
        if (userOptional.isPresent()){
            User user = userOptional.get();
            post.setAuthor(user.getName());
            defaultSettings.put("post", post);
            defaultSettings.put("tag", tag);
            return defaultSettings;
        }else {
            throw (Throwable) UsernameNotFoundException;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void savePost(@RequestBody Post post,
                         @RequestBody Tag tag) {
        String[] tagList = tag.getName().split(",");
        Set<Tag> tagSet = new HashSet<Tag>();
        for (String data : tagList) {
            Tag tempTag = new Tag();
            tempTag.setName(data);
            tempTag.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tempTag.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            tagSet.add(tempTag);
        }
        post.setTags(tagSet);
        String excerpt = post.getContent().substring(0, 500);
        post.setExcerpt(excerpt);
        postService.save(post);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void updatePost(@RequestBody Post post,
                             @RequestBody Tag tag) {
        Optional<Post> optionalPost = postService.findPostById(post.getId());
        Post updatePost = optionalPost.get();
        String[] tagList = tag.getName().split(",");
        Set<Tag> tagSet = new HashSet<Tag>();

        for (String data : tagList) {
            Tag tempTag = new Tag();
            tempTag.setName(data);
            tempTag.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tempTag.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            tagSet.add(tempTag);
        }

        updatePost.setTags(tagSet);
        updatePost.setUpdatedAt(post.getUpdatedAt());
        updatePost.setContent(post.getContent());
        updatePost.setTitle(post.getTitle());
        updatePost.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        String excerpt = post.getContent().substring(0, 500);
        updatePost.setExcerpt(excerpt);

        postService.save(updatePost);
    }

    @GetMapping("/post/{id}")
    public Map<String,Object> getPost(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.findPostById(id);
        List<Comment> comments = commentService.getCommentByPostId(id);
        Post post = new Post();

        if (optionalPost.isPresent()) {
            post = optionalPost.get();
        }
            Map<String,Object> postData= new HashMap<>();
        postData.put("post", post);
        postData.put("comments", comments);
        return postData;
    }

    @RequestMapping(value = "/editPost/{id}", method = RequestMethod.GET)
    public Map<String,Object> editPost(@PathVariable Long id,
                                 Principal principal) throws Throwable {
        Optional<Post> optionalPost = postService.findPostById(id);
        Tag tag = new Tag();
        Map<String,Object> objectsToEdit = new HashMap<>();

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post) || hasRoleAdmin()) {
                objectsToEdit.put("post", post);
                objectsToEdit.put("tag", tag);
                return objectsToEdit;
            } else {
                throw (Throwable) ObjectNotFoundException;
            }
        } else {
            throw (Throwable) ObjectNotFoundException;        }
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.DELETE)
    public void deletePost(@PathVariable Long id,
                                   Principal principal) {
        Optional<Post> optionalPost = postService.findPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post) || hasRoleAdmin()) {
                postService.delete(post);
            }
        }
    }

    private boolean isPrincipalOwnerOfPost(Principal principal, Post post) {
        return principal != null && principal.getName().equals(post.getAuthor());
    }
    private boolean hasRoleAdmin()
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}