package com.project.blog.controller;

import com.project.blog.entity.Comment;
import com.project.blog.entity.Post;
import com.project.blog.service.CommentService;
import com.project.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private Object ObjectNotFoundException;

    @Autowired
    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @RequestMapping(value = "/createComment", method = RequestMethod.POST)
    public void createNewComment(@RequestBody Comment comment) {
            comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            comment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            commentService.save(comment);
    }

    @RequestMapping(value = "/commentPost/{id}", method = RequestMethod.GET)
    public Comment commentPostWithId(@PathVariable Long id) {
        Comment comment = new Comment();
        comment.setPostId(id);
        return comment;
    }

    @RequestMapping(value = "/editComment/{id}/{commentId}", method = RequestMethod.GET)
    public Comment editComment(@PathVariable Long id,
                                          @PathVariable Long commentId,
                                          Principal principal) throws Throwable {
        Optional<Post> optionalPost = postService.findPostById(id);
        Optional<Comment> optionalComment = commentService.getComment(commentId);
        Comment comment = new Comment();
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (isPrincipalOwnerOfPost(principal, post)) {
                if (optionalComment.isPresent()) {
                    comment = optionalComment.get();
                }
                return comment;
            } else {
                throw (Throwable) ObjectNotFoundException;
            }
        } else {
            throw (Throwable) ObjectNotFoundException;
        }
    }

    @RequestMapping(value = "/updateComment", method = RequestMethod.PUT)
    public void updateComment(@RequestBody Comment comment){
        Optional<Comment> optionalComment = commentService.getComment(comment.getId());
        Comment updateComment = new Comment();
        if(optionalComment.isPresent()){
            updateComment = optionalComment.get();
        }
        updateComment.setComment(comment.getComment());
        updateComment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            commentService.save(updateComment);
    }

    @RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE)
    public void deleteComment(@PathVariable Long id,
                                   Principal principal) {
        Optional<Comment> optionalComment = commentService.getComment(id);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Optional<Post> optionalPost = postService.findPostById(comment.getPostId());
            if(optionalPost.isPresent()){
                Post post = optionalPost.get();

              if (isPrincipalOwnerOfPost(principal, post)) {
                  commentService.delete(comment);
              }
            }
        }
    }

    private boolean isPrincipalOwnerOfPost(Principal principal, Post post) {
        return principal != null && principal.getName().equals(post.getAuthor());
    }
}