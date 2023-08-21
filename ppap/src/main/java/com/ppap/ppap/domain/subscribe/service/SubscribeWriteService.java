package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception400;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap._core.rss.RssReader;
import com.ppap.ppap.domain.subscribe.dto.SubscribeCreateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateRequestDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeUpdateResponseDto;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SubscribeWriteService {
    private final SubscribeJpaRepository subscribeJpaRepository;
    private final NoticeWriteService noticeWriteService;
    private final NoticeReadService noticeReadService;
    private final RssReader rssReader;

    public void create(SubscribeCreateRequestDto requestDto, User user) {

        // http -> https 변환 & 쿼리 스트링 제거
        String makeHttpsAndRemoveQueryString = rssReader.makeHttpsAndRemoveQueryString(requestDto.rssLink());

        // 링크로 주어진 값이 Notice에 없다면 rss링크가 유효한지 확인하고 save()
        Notice notice = noticeReadService.findByRssLink(makeHttpsAndRemoveQueryString).orElseGet(
                () -> {
                    rssReader.validRssLink(makeHttpsAndRemoveQueryString);
                    return noticeWriteService.save(makeHttpsAndRemoveQueryString);
                });

        if(subscribeJpaRepository.existsByUserAndNotice(user, notice)){
            throw new Exception400(BaseExceptionStatus.SUBSCRIBE_ALREADY_EXIST);
        }

        String validNoticeLink = getValidNoticeLink(requestDto.noticeLink(), notice.getRssLink());

        subscribeJpaRepository.save(Subscribe.of(user, notice, requestDto.title(), validNoticeLink, true));
    }

    // isActive 업데이트는 따로 만드는게 좋을 듯 하다.
    public SubscribeUpdateResponseDto update(SubscribeUpdateRequestDto requestDto, Long subscribeId, User user) {
        Subscribe subscribe = subscribeJpaRepository.findById(subscribeId).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND));

        // 현재 접속한 회원과 접근한 구독의 작성자가 같은지 체크
        validSubscribeWriter(subscribe, user);

        String validNoticeLink = getValidNoticeLink(requestDto.noticeLink(), subscribe.getNotice().getRssLink());

        subscribe.changeNoticeLinkAndTitle(validNoticeLink, requestDto.title());
        subscribeJpaRepository.saveAndFlush(subscribe);

        return SubscribeUpdateResponseDto.from(subscribe);
    }

    public SubscribeUpdateResponseDto activeUpdate(Long subscribeId, User user) {
        Subscribe subscribe = subscribeJpaRepository.findById(subscribeId).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND));

        // 현재 접속한 회원과 접근한 구독의 작성자가 같은지 체크
        validSubscribeWriter(subscribe, user);

        subscribe.changeActive();
        subscribeJpaRepository.saveAndFlush(subscribe);

        return SubscribeUpdateResponseDto.from(subscribe);
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

        // 공지사항 학과 사이트 ex) cse.pusan.ac.kr
        String noticeDepartment = noticeLink.replace("http://", "")
                .replace("https://", "")
                .split("/")[0];

        if(!rssDepartment.equals(noticeDepartment)) {
            throw new Exception400(BaseExceptionStatus.RSS_NOTICE_LINK_MISMATCH);
        }

        // 쿼리 스트링 제거 후 return
        return noticeLink.split("\\?")[0];
    }

    private void validSubscribeWriter(Subscribe subscribe, User user) {
        if (!subscribe.getUser().getId().equals(user.getId()))
            throw new Exception403(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN);
    }
}
