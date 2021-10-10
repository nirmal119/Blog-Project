package com.project.blog.service.impl;

import com.project.blog.entity.Comment;
import com.project.blog.repository.CommentRepository;
import com.project.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImp(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Optional<Comment> getComment(Long Id) {
        return commentRepository.findById(Id);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}