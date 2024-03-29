package com.ppap.ppap.domain.subscribe.repository;

import com.ppap.ppap.domain.subscribe.entity.Content;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomContentRepositoryImpl implements CustomContentRepository{
    private final static String TABLE = "content_tb";
    private final EntityManager em;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(List<Content> contentList) {
        final int batchSize = 1000;
        String insertQuery = String.format("INSERT INTO %s (notice_id, title, link, pub_date, author, category) " +
                "VALUES (?, ?, ?, ?, ?, ?)", TABLE);

        for (int j=0; j<contentList.size(); j += batchSize) {
            List<Content> contentSubList = contentList.subList(j, Math.min(j + batchSize, contentList.size()));
            jdbcTemplate.getJdbcTemplate().batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Content content = contentSubList.get(i);
                    ps.setLong(1, content.getNotice().getId());
                    ps.setString(2, content.getTitle());
                    ps.setString(3, content.getLink());
                    ps.setTimestamp(4, java.sql.Timestamp.valueOf(content.getPubDate()));
                    ps.setString(5, content.getAuthor());
                    ps.setString(6, content.getCategory());
                }

                @Override
                public int getBatchSize() {
                    return contentSubList.size();
                }
            });
        }
        em.flush();
    }
}
