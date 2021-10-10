package com.project.blog.controller;

import com.project.blog.entity.Comment;
import com.project.blog.entity.Post;
import com.project.blog.entity.Tag;
import com.project.blog.entity.User;
import com.project.blog.service.CommentService;
import com.project.blog.service.PostService;
import com.project.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@Controller
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/create")
    public String createPost(Principal principal, Model model){
        Post post = new Post();
        Tag tag = new Tag();
        Optional<User> userOptional = userService.findByName(principal.getName());

        if (userOptional.isPresent()){
            User user = userOptional.get();
            post.setAuthor(user.getName());
            model.addAttribute("post", post);
            model.addAttribute("tag", tag);
            return "newPost";
        }else {
            return "/fail";
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String savePost(@ModelAttribute("post") Post post,
                           @ModelAttribute("tag") Tag tag,
                           BindingResult bindingResult) {
        String[] tagList = tag.getName().split(",");
        Set<Tag> tagSet = new HashSet<Tag>();
        for(String data : tagList){
            Tag tempTag  = new Tag();
            tempTag.setName(data);
            tempTag.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            tempTag.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            tagSet.add(tempTag);
        }

        post.setTags(tagSet);
        String excerpt = post.getContent().substring(0,500);
        post.setExcerpt(excerpt);

        if (bindingResult.hasErrors()) {
            return "fail";
        }
        else {
            postService.save(post);
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updatePost(@ModelAttribute("post") Post post,
                             @ModelAttribute("tag") Tag tag,
                             BindingResult bindingResult){
        Optional<Post> optionalPost = postService.findPostById(post.getId());
       Post  updatePost = optionalPost.get();
        String[] tagList = tag.getName().split(",");
        Set<Tag> tagSet = new HashSet<Tag>();

        for(String data : tagList){
            Tag tempTag  = new Tag();
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
        String excerpt = post.getContent().substring(0,500);
        updatePost.setExcerpt(excerpt);

        if (bindingResult.hasErrors()) {
            return "fail";
        }
        else {
            postService.save(updatePost);
            return "redirect:/";
        }
    }
    @RequestMapping(value = "/post/{id}", method = RequestMethod.GET)
    public String getPost(@PathVariable Long id,
                                Principal principal,
                                Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        List<Comment> comments = commentService.getCommentByPostId(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            if (isPrincipalOwnerOfPost(principal, post)) {
                model.addAttribute("username", principal.getName());
            }
            return "/post";
        } else {
            return "/fail";
        }
    }

    @RequestMapping(value = "/editPost/{id}", method = RequestMethod.GET)
    public String editPost(@PathVariable Long id,
                                 Principal principal,
                                 Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        Tag tag = new Tag();

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post)) {
                model.addAttribute("post", post);
                model.addAttribute("tag", tag);
                return "/updatePost";
            } else {
                return "/fail";
            }
        } else {
            return "/fail";
        }
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.DELETE)
    public String deletePost(@PathVariable Long id,
                                   Principal principal) {
        Optional<Post> optionalPost = postService.findPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post)) {
                postService.delete(post);
                return "redirect:/";
            } else {
                return "/403";
            }
        } else {
            return "/fail";
        }
    }

    @RequestMapping(value = "/editComment/{id}/{commentId}", method = RequestMethod.GET)
    public String editComment(@PathVariable Long id,
                              @PathVariable Long commentId,
                                 Principal principal,
                                 Model model) {
        Optional<Post> optionalPost = postService.findPostById(id);
        Optional<Comment> optionalComment = commentService.getComment(commentId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post)) {
                model.addAttribute("username", principal.getName());
                if (optionalComment.isPresent()) {
                    Comment comment = optionalComment.get();
                    model.addAttribute("comment", comment);
                }
                return "/updateComment";
            } else {
                return "/fail";
            }
        } else {
            return "/fail";
        }
    }

    private boolean isPrincipalOwnerOfPost(Principal principal, Post post) {
        return principal != null && principal.getName().equals(post.getAuthor());
    }
}