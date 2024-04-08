package com.ppap.ppap._core.batch;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.subscribe.dto.NoticeDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.entity.Univ;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.subscribe.repository.UnivJpaRepository;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoticeWriter implements ItemWriter<NoticeDto> {

	private final UserJpaRepository userJpaRepository;
	private final NoticeJpaRepository noticeJpaRepository;
	private final UnivJpaRepository univJpaRepository;
	private final SubscribeJpaRepository subscribeJpaRepository;

	private static final String BATCH_USER = "ppap_notice@notice.com";

	public NoticeWriter(UserJpaRepository userJpaRepository, NoticeJpaRepository noticeJpaRepository,
		UnivJpaRepository univJpaRepository, SubscribeJpaRepository subscribeJpaRepository) {
		this.userJpaRepository = userJpaRepository;
		this.noticeJpaRepository = noticeJpaRepository;
		this.univJpaRepository = univJpaRepository;
		this.subscribeJpaRepository = subscribeJpaRepository;
	}

	@Override
	public void write(Chunk<? extends NoticeDto> chunk) {
		List<String> univConcatData = chunk.getItems()
			.stream()
			.map(noticeDto -> String.format("%s_%s", noticeDto.college(), noticeDto.department()))
			.distinct()
			.toList();

		Map<String, Univ> univIdMap = univJpaRepository.findAllByInCollegeAndDepartment(univConcatData).stream()
			.collect(
				toMap(
					univ -> String.format("%s_%s", univ.getCollege(), univ.getDepartment()),
					univ->univ
			));

		Map<String, Notice> existNoticeMap = noticeJpaRepository.findByLinkIn(chunk.getItems().stream()
			.map(NoticeDto::link)
			.toList())
			.stream()
			.collect(toMap(Notice::getLink
				, notice -> notice));

		chunk.forEach(
			noticeDto -> {
				if (existNoticeMap.containsKey(noticeDto.link())) {
					if (syncNoticeData(existNoticeMap.get(noticeDto.link()), noticeDto, univIdMap)) {
						noticeJpaRepository.saveAndFlush(existNoticeMap.get(noticeDto.link()));
					}
				} else {
					Notice notice = noticeJpaRepository.save(
						Notice.builder()
							.link(noticeDto.link())
							.noticeType(noticeDto.noticeType())
							.lastNoticeTime(noticeDto.lastNoticeTime())
							.univ(getUniv(univIdMap, noticeDto.college(), noticeDto.department()))
							.build());
					existNoticeMap.put(noticeDto.link(), notice);
				}
			}
		);

		User user = userJpaRepository.findByEmail(BATCH_USER).orElseThrow(
			() -> new Exception404(BaseExceptionStatus.USER_NOT_FOUND)
		);

		List<Subscribe> subscribeList = subscribeJpaRepository.findByUserIdFetchJoinNotice(user.getId());
		Map<String, Subscribe> existSubscribeMap = subscribeList.stream()
			.collect(toMap(
				subscribe -> subscribe.getNotice().getLink(),
				subscribe -> subscribe
			));

		chunk.forEach(
			noticeDto -> {
				if (existSubscribeMap.containsKey(noticeDto.link())) {
					if (syncSubscribeData(existSubscribeMap.get(noticeDto.link()), noticeDto)) {
						subscribeJpaRepository.saveAndFlush(existSubscribeMap.get(noticeDto.link()));
					}
				} else {
					subscribeJpaRepository.save(Subscribe.builder()
						.notice(existNoticeMap.get(noticeDto.link()))
						.user(user)
						.title(noticeDto.title())
						.isActive(false)
						.build()
					);
				}
			}
		);

	}

	private Univ getUniv(Map<String, Univ> univIdMap, String college, String department) {
		String key = String.format("%s_%s", college, department);
		if (univIdMap.containsKey(key))
			return univIdMap.get(key);

		Univ univ = univJpaRepository.save(Univ.builder()
			.college(college)
			.department(department)
			.build());

		univIdMap.put(key, univ);
		return univ;
	}

	private boolean syncNoticeData(Notice notice, NoticeDto noticeDto, Map<String, Univ> univIdMap) {
		String collegeDepartment = String.format("%s_%s", noticeDto.college(), noticeDto.department());
		if (notice.getUniv() == null || !notice.getUniv().equals(univIdMap.get(collegeDepartment))){
			notice.changeUniv(univIdMap.get(collegeDepartment));
			return true;
		}
		return false;
	}

	private boolean syncSubscribeData(Subscribe subscribe, NoticeDto noticeDto) {
		if (!subscribe.getTitle().equals(noticeDto.title())) {
			subscribe.changeTitle(noticeDto.title());
			return true;
		}
		return false;
	}
}
