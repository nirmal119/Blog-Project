package com.project.blog.controller;

import com.project.blog.entity.Comment;
import com.project.blog.entity.Post;
import com.project.blog.service.CommentService;
import com.project.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Optional;

@Controller
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    @Autowired
    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @RequestMapping(value = "/createComment", method = RequestMethod.POST)
    public String createNewComment(@ModelAttribute("newComment") Comment comment,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "/commentForm";
        } else {
            System.out.println(comment.getComment());
            comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            commentService.save(comment);
            return "redirect:/post/" + comment.getPostId();
        }
    }

    @RequestMapping(value = "/commentPost/{id}", method = RequestMethod.GET)
    public String commentPostWithId(@PathVariable Long id,
                                    Model model) {
        Comment comment = new Comment();
        comment.setPostId(id);
        model.addAttribute("newComment", comment);
        return "/writeComment";
    }

    @RequestMapping(value = "/updateComment", method = RequestMethod.POST)
    public String updateComment(@ModelAttribute("comment") Comment comment,
                                BindingResult bindingResult){
        Optional<Comment> optionalComment = commentService.getComment(comment.getId());
        Comment updateComment = optionalComment.get();
        updateComment.setComment(comment.getComment());
        updateComment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        if (bindingResult.hasErrors()) {
            return "fail";
        }
        else {
            commentService.save(updateComment);
            return  "redirect:/post/" + comment.getPostId();
        }
    }

    @RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE)
    public String deleteComment(@PathVariable Long id,
                                   Principal principal) {
        Optional<Comment> optionalComment = commentService.getComment(id);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Optional<Post> optionalPost = postService.findPostById(comment.getPostId());
            Post post = optionalPost.get();

            if (isPrincipalOwnerOfPost(principal, post)) {
                commentService.delete(comment);
                return "redirect:/";
            } else {
                return "/403";
            }
        } else {
            return "/fail";
        }
    }

    private boolean isPrincipalOwnerOfPost(Principal principal, Post post) {
        return principal != null && principal.getName().equals(post.getAuthor());
    }
}