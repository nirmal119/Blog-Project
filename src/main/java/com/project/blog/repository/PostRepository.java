package com.project.blog.repository;

import com.project.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findById(Long id);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE p.title LIKE %?1%"+"OR p.content LIKE %?1%"+"OR p.author LIKE %?1%"+
            "OR t.name LIKE %?1%")
    Page<Post> findAll(String keyword, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    @Query("select DISTINCT p.author from Post p")
    List<String> findAllAuthors();

    @Query("select DISTINCT p.publishedAt from Post p")
    List<Date> findAllPublishedAt();

    List<Post> findAllByAuthor(String author);

    @Query("select DISTINCT p from Post p WHERE CAST(p.publishedAt AS date) = CAST(:date AS date)")
    List<Post> findAllByPublishedAt(Date date);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name LIKE %?1%")
    List<Post> findAll(String tag);
}