package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.mapper.TagMapper;
import com.sba.lexilearnbe.modules.work.repository.TagRepository;
import com.sba.lexilearnbe.modules.work.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper; // Inject TagMapper vào

    @Override
    public List<TagResponse> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc().stream()
                .map(tagMapper::toResponse)
                .toList();
    }
}