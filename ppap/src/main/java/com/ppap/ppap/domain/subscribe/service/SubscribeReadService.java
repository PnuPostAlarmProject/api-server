package com.ppap.ppap.domain.subscribe.service;

import com.ppap.ppap._core.exception.BaseExceptionStatus;
import com.ppap.ppap._core.exception.Exception403;
import com.ppap.ppap._core.exception.Exception404;
import com.ppap.ppap.domain.subscribe.dto.SubscribeGetListResponseDto;
import com.ppap.ppap.domain.subscribe.dto.SubscribeGetResponseDto;
import com.ppap.ppap.domain.subscribe.dto.query.FindByUnivAndRoleQueryDto;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SubscribeReadService {
    private final SubscribeJpaRepository subscribeJpaRepository;

    public List<Subscribe> getSubscribeEntityList(User user) {
        List<Subscribe> subscribeList = subscribeJpaRepository.findByUserId(user.getId());
        if (subscribeList.isEmpty()) throw new Exception404(BaseExceptionStatus.SUBSCRIBE_EMPTY);

        return subscribeList;
    }

    public List<SubscribeGetListResponseDto> getSubscribeList(User user) {
        List<Subscribe> subscribeList = subscribeJpaRepository.findByUserId(user.getId());

        return subscribeList.stream()
                .map(SubscribeGetListResponseDto::of)
                .toList();
    }

    public Set<Subscribe> getSubscribeByNoticeIdIn(List<Long> noticeIds) {
        return subscribeJpaRepository.findByNoticeIdIn(noticeIds);
    }

    public List<Subscribe> getSubscribeByNoticeId(Long noticeId) {
        return subscribeJpaRepository.findByNoticeId(noticeId);
    }

    public SubscribeGetResponseDto getSubscribe(User user, Long subscribeId) {
        Subscribe subscribe = subscribeJpaRepository.findByIdFetchJoinNotice(subscribeId).orElseThrow(
                () -> new Exception404(BaseExceptionStatus.SUBSCRIBE_NOT_FOUND)
        );

        validateIsWriter(user, subscribe);

        return SubscribeGetResponseDto.from(subscribe);
    }

    // 배치 프로그램으로 등록된 구독 데이터 조회
    public List<FindByUnivAndRoleQueryDto> getSubscribeByUnivId(Long univId) {
        return subscribeJpaRepository.findByUnivIdAndRoleFetchJoin(univId, Role.ROLE_BATCH);
    }

    private void validateIsWriter(User user, Subscribe subscribe) {
        if (!user.getId().equals(subscribe.getUser().getId())) {
            throw new Exception403(BaseExceptionStatus.SUBSCRIBE_FORBIDDEN);
        }
    }
}
