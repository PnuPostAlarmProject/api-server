package com.ppap.ppap.domain.notice.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.rss.RssReader;
import com.ppap.ppap.domain.notice.dto.SubscribeRequestDto;
import com.ppap.ppap.domain.notice.entity.Notice;
import com.ppap.ppap.domain.notice.entity.Subscribe;
import com.ppap.ppap.domain.notice.repository.SubscribeRepository;
import com.ppap.ppap.domain.user.Entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SubscribeWriteService {
    private final SubscribeRepository subscribeRepository;
    private final NoticeWriteService noticeWriteService;
    private final NoticeReadService noticeReadService;
    private final RssReader rssReader;

    public void create(SubscribeRequestDto requestDto, User user) {
        String validRssLink = rssReader.getValidRssLink(requestDto.rssLink());
        String validNoticeLink = getValidNoticeLink(requestDto.noticeLink(), validRssLink);

        Notice notice = noticeReadService.findByRssLink(validRssLink).orElseGet(
                () -> noticeWriteService.save(validRssLink));

        if(subscribeRepository.existsByUserAndNotice(user, notice)){
            throw new Exception400(BaseExceptionStatus.SUBSCRIBE_ALREADY_EXIST);
        }

        subscribeRepository.save(Subscribe.of(user, notice, requestDto.title(), validNoticeLink));
    }

    /**
     * rss 링크와 공지사항 링크가 같은 학과인지 체크하는 메소드
     * 단, 사용자가 공지사항 링크를 입력하지 않은 경우(null)에는 유효성 검사를 실시하지 않는다.
     */
    private String getValidNoticeLink(String noticeLink, String rssLink) {
        // 사용자가 공지사항 링크를 입력하지 않았을 경우에는 유효성 검사를 하지 않는다.
        if(noticeLink == null) return null;

        // rss 학과 사이트 ex) cse.pusan.ac.kr
        String rssDepartment = rssLink.replace("http://", "")
                .replace("https://", "")
                .split("/")[0];

        // 공지사항 학과 사이트 ex) cse.pusn.ac.kr
        String noticeDepartment = noticeLink.replace("http://", "")
                .replace("https://", "")
                .split("/")[0];

        if(!rssDepartment.equals(noticeDepartment)) {
            throw new Exception400(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH);
        }

        // 쿼리 스트링 제거 후 return
        return noticeLink.split("\\?")[0];
    }
}
