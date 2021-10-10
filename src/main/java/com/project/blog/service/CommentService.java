package com.project.blog.service;

import com.project.blog.entity.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {

    Comment save(Comment comment);

    List<Comment> getCommentByPostId(Long postId);

    Optional<Comment> getComment(Long Id);

    void delete(Comment comment);
}
