package com.ppap.ppap;

import com.ppap.ppap.domain.subscribe.entity.Notice;
import com.ppap.ppap.domain.subscribe.entity.Subscribe;
import com.ppap.ppap.domain.subscribe.repository.NoticeJpaRepository;
import com.ppap.ppap.domain.subscribe.repository.SubscribeJpaRepository;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.entity.User;
import com.ppap.ppap.domain.user.entity.constant.Provider;
import com.ppap.ppap.domain.user.entity.constant.Role;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;
import com.ppap.ppap.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitDataGenerator implements ApplicationRunner {

    private final NoticeJpaRepository noticeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final SubscribeJpaRepository subscribeJpaRepository;
    private final DeviceJpaRepository deviceJpaRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<User> userList = List.of(
                User.builder().email("rjsdnxogh@naver.com").password("test1234").role(Role.ROLE_USER).provider(Provider.PROVIDER_KAKAO).build(),
                User.builder().email("rjsdnxogh12@naver.com").password("test1234").role(Role.ROLE_USER).provider(Provider.PROVIDER_KAKAO).build(),
                User.builder().email("rjsdnxogh55@gmail.com").password("test1234").role(Role.ROLE_USER).provider(Provider.PROVIDER_KAKAO).build()
        );
        userJpaRepository.saveAll(userList);

        List<String> rssLinkList = List.of(
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

        List<Notice> noticeList = rssLinkList.stream()
                .map(Notice::of)
                .toList();
        noticeJpaRepository.saveAll(noticeList);


        List<Subscribe> subscribeList = List.of(
                Subscribe.builder().title("테스트1").user(userList.get(0)).notice(noticeList.get(0)).isActive(true).build(),
                Subscribe.builder().title("테스트2").user(userList.get(0)).notice(noticeList.get(1)).isActive(true).build(),
                Subscribe.builder().title("테스트3").user(userList.get(0)).notice(noticeList.get(2)).isActive(true).build(),
                Subscribe.builder().title("테스트4").user(userList.get(1)).notice(noticeList.get(2)).isActive(true).build(),
                Subscribe.builder().title("테스트5").user(userList.get(1)).notice(noticeList.get(3)).isActive(true).build(),
                Subscribe.builder().title("테스트6").user(userList.get(2)).notice(noticeList.get(2)).isActive(true).build());

        subscribeJpaRepository.saveAll(subscribeList);

        List<Device> deviceList = List.of(
                Device.builder().fcmToken("testToken1").user(userList.get(0)).build(),
                Device.builder().fcmToken("testToken2").user(userList.get(0)).build(),
                Device.builder().fcmToken("testToken3").user(userList.get(1)).build(),
                Device.builder().fcmToken("testToken4").user(userList.get(2)).build(),
                Device.builder().fcmToken("testToken5").user(userList.get(2)).build(),
                Device.builder().fcmToken("testToken6").user(userList.get(2)).build()
        ) ;
        deviceJpaRepository.saveAll(deviceList);

    }
}
