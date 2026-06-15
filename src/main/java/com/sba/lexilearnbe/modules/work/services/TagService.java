package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import java.util.List;

public interface TagService {
    List<TagResponse> getAllTags();
}