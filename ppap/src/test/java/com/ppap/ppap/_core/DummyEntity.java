package com.ppap.ppap._core;

import com.ppap.ppap._core.rss.RssData;
import com.ppap.ppap.domain.subscribe.entity.Content;
import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Provider;
import com.ppap.ppap.domain.user.entity.constant.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DummyEntity {

    public User getTestUser(String email, Long id) {
        return User.builder()
                .id(id)
                .email(email)
                .role(Role.ROLE_USER)
                .provider(Provider.PROVIDER_KAKAO)
                .build();
    }

    private final List<String> testRssLink = List.of(
            "https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/12549/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/12278/rssList.do",
            "https://chemeng.pusan.ac.kr/bbs/chemeng/2870/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2606/rssList.do",
            "https://french.pusan.ac.kr/bbs/french/4295/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2617/rssList.do",
            "https://ocean.pusan.ac.kr/bbs/ocean/2877/rssList.do",
            "https://fsn.pusan.ac.kr/bbs/fsn/2783/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2618/rssList.do",
            "https://molbiology.pusan.ac.kr/bbs/molbiology/3918/rssList.do",
            "https://phys.pusan.ac.kr/bbs/phys/2658/rssList.do",
            "https://sct.pusan.ac.kr/bbs/sct/17403/rssList.do",
            "https://nanomecha.pusan.ac.kr/bbs/nanomecha/3264/rssList.do",
            "https://biology.pusan.ac.kr/bbs/biology/3143/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2616/rssList.do",
            "https://pnuecon.pusan.ac.kr/bbs/pnuecon/3210/rssList.do",
            "https://biz.pusan.ac.kr/bbs/biz/2557/rssList.do",
            "https://eec.pusan.ac.kr/bbs/eehome/2650/rssList.do",
            "https://cse.pusan.ac.kr/bbs/cse/2605/rssList.do",
            "https://his.pusan.ac.kr/bbs/ee/2635/rssList.do",
            "https://ee.pusan.ac.kr/bbs/ee/2635/rssList.do"
    );


    public List<Notice> getTestNoticeList() {
        List<Notice> testNoticeList = new ArrayList<>();
        for(int i=0; i< testRssLink.size(); i++) {
            testNoticeList.add(new Notice(i+1L, testRssLink.get(i), LocalDateTime.now()));
        }
        return testNoticeList;
    }

    public List<Subscribe> getTestSubscribeList(User user, List<Notice> noticeList){
        List<Subscribe> testSubscribeList = new ArrayList<>();
        for(int i=0; i<noticeList.size(); i++) {
            testSubscribeList.add(
                    Subscribe.builder()
                            .id(i+1L)
                            .title("테스트 " + (i+1))
                            .user(user)
                            .notice(noticeList.get(i))
                            .noticeLink(null)
                            .isActive(true)
                            .build());
        }
        return testSubscribeList;
    }

    public List<Content> getTestContentList(Notice notice) {
        return List.of(
                new Content(1L, notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(2L, notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(3L, notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(4L, notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(5L, notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(6L, notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(7L, notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(8L, notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(9L, notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(10L, notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(11L, notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new Content(12L, notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null)
        );
    }

    public List<RssData> getTestRssList(Notice notice){
        return List.of(
                new RssData(notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)", "http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null),
                new RssData(notice, "자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.", "http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do", LocalDateTime.parse("2022-02-28T18:30:17.097"), null, null)
        );
    }
}
