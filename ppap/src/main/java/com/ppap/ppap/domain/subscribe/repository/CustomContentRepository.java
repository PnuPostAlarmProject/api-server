package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Content;

import java.util.List;

public interface CustomContentRepository {

    void saveAllBatch(List<Content> contentList);
}
