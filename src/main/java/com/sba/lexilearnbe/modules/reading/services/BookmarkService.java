package com.sba.lexilearnbe.modules.reading.services;

import com.sba.lexilearnbe.modules.reading.dto.request.UpsertBookmarkRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkResponse;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {

    List<BookmarkResponse> getBookmarks(UUID accountId);

    BookmarkResponse upsertBookmark(UUID accountId, UUID workId, UpsertBookmarkRequest request);

    void deleteBookmark(UUID accountId, UUID workId);
}
