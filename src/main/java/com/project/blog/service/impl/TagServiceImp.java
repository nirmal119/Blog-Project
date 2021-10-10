package com.project.blog.service.impl;

import com.project.blog.repository.TagRepository;
import com.project.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagServiceImp implements TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImp(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<String> getTags() {
        return tagRepository.findAllNames();
    }
}