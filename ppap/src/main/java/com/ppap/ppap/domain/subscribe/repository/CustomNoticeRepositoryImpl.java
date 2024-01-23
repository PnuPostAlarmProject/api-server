package com.ppap.ppap.domain.subscribe.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ppap.ppap.domain.subscribe.entity.Notice;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomNoticeRepositoryImpl implements CustomNoticeRepository{
	private final EntityManager em;
	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final static String TABLE = "notice_tb";

	@Override
	public void updateAll(Collection<Notice> noticeList) {
		String updateQuery = String.format("UPDATE %s SET last_time = ? WHERE notice_id = ?", TABLE);

		jdbcTemplate.getJdbcTemplate().batchUpdate(updateQuery,
			noticeList,
			Math.min(noticeList.size(), 1000),
			(ps, notice) -> {
				ps.setTimestamp(1, Timestamp.valueOf(notice.getLastNoticeTime()));
				ps.setLong(2, notice.getId());
			});

		em.clear();
	}
}
