-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table notice_tb;
truncate table subscribe_tb;
truncate table device_tb;
truncate table scrap_tb;
truncate table content_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화

INSERT INTO user_tb (`user_id`, `email`, `password`, `role`, `provider`, `created_at`, `last_modified_at`)
VALUES (1, 'rjsdnxogh@naver.com', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'ROLE_USER', 'PROVIDER_KAKAO', '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO user_tb (`user_id`, `email`, `password`, `role`, `provider`, `created_at`, `last_modified_at`)
VALUES (2, 'rjsdnxogh12@kakao.com', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'ROLE_USER', 'PROVIDER_KAKAO', '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');


INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (1, 'https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do', CURRENT_TIMESTAMP, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (2, 'https://cse.pusan.ac.kr/bbs/cse/12549/rssList.do', CURRENT_TIMESTAMP, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (3, 'https://chemeng.pusan.ac.kr/bbs/chemeng/2870/rssList.do', CURRENT_TIMESTAMP, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (4, 'https://french.pusan.ac.kr/bbs/french/4295/rssList.do', CURRENT_TIMESTAMP, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');

INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (1, '테스트1', 1, 1, NULL, TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (2, '테스트2', 1, 2, 'https://cse.pusan.ac.kr/cse/14655/subview.do', TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (3, '테스트3', 1, 3, NULL, TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');


INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (1, 1, '2022-03-02 17:26:39.089', '박상운', NULL, '"컴퓨터 및 프로그래밍 입문(001분반, 조환규 교수님) 수업을 신청한 수강생은 꼭 읽어주세요."', 'http://his.pusan.ac.kr/bbs/cse/2615/931071/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (2, 1, '2022-02-28 18:30:17.097', '박상운', NULL, '컴퓨터알고리즘(059분반) 조환규 교수님 수업을 신청한 수강생은 꼭 읽어주세요.)', 'http://his.pusan.ac.kr/bbs/cse/2615/930883/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (3, 1, '2021-09-07 18:10:12.523','노요환', NULL, '[등록기간 연장]2021 ICPC 예선 안내(~9/30까지 신청)', 'http://his.pusan.ac.kr/bbs/cse/2615/880989/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (4, 1, '2021-08-27 17:13:37.099', '박상운', NULL, '자료구조(060분반) 수업을 신청한 수강생은 꼭 읽어주세요.)', 'http://his.pusan.ac.kr/bbs/cse/2615/879352/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (5, 1, '2021-03-01 22:42:08.803', '양세양', NULL, '2021.1학기 논리설계 수업관련 (카카오톡 오픈채팅 링크 공지!)', 'http://his.pusan.ac.kr/bbs/cse/2615/859613/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (6, 1, '2021-02-22 10:33:35.092', '고우람', NULL, '조환규 교수님 컴퓨터및프로그래밍입문(001분반)  수업 관련하여 공지드립니다', 'http://his.pusan.ac.kr/bbs/cse/2615/858996/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (7, 1, '2021-02-19 16:06:17.031', '박상운', NULL, '컴퓨터알고리즘(059분반) 수업을 신청한 수강생은 꼭 읽어주세요.', 'http://his.pusan.ac.kr/bbs/cse/2615/858935/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (8, 1, '2020-10-06 15:30:15.683', '노요환', NULL, 'ACM-ICPC  대회 진행 안내', 'http://his.pusan.ac.kr/bbs/cse/2615/835761/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (9, 1, '2020-09-10 16:54:25.913', '노요환', NULL, '프로그래밍 대회 ACM-ICPC 2020 안내', 'http://his.pusan.ac.kr/bbs/cse/2615/833201/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (10, 1, '2020-09-02 17:14:14.000', '박상운', NULL, '자료구조(059분반) 수업을 신청한 학생은 꼭 읽어주세요.', 'http://his.pusan.ac.kr/bbs/cse/2615/832273/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (11, 1, '2020-03-13 11:09:52.313', '양세양', NULL, '논리설계 059분반 수강생', 'http://his.pusan.ac.kr/bbs/cse/2615/816595/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (12, 1, '2020-03-13 11:08:20.947', '양세양', NULL, '논리설계 062분반 수강생', 'http://his.pusan.ac.kr/bbs/cse/2615/816594/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (13, 1, '2020-03-13 11:00:44.058', '양세양' , NULL, 'For those who take Logic Design course taught by Prof. Yang', 'http://his.pusan.ac.kr/bbs/cse/2615/816589/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (14, 1, '2020-03-09 15:24:21.052', '우균', NULL, '"이산수학I(100분반), 컴퓨터및프로그래밍입문(103분반) 강의 홈페이지 개설 공고"', 'http://his.pusan.ac.kr/bbs/cse/2615/816047/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (15, 1, '2020-03-04 15:34:51.607', '고우람', NULL, '컴퓨터및프로그래밍입문(100분반) 수업 관련하여 공지드립니다.', 'http://his.pusan.ac.kr/bbs/cse/2615/815516/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (16, 1, '2020-02-03 17:46:15.017', '박상운', NULL, '컴퓨터알고리즘(059분반) 수업을 신청한 학생은 꼭 읽어주세요. (03/02 수정)', 'http://his.pusan.ac.kr/bbs/cse/2615/812900/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (17, 1, '2019-12-13 12:01:16.453', '양세양', NULL, 'For Self-grading of Final Exam UPDATED!!!', 'http://his.pusan.ac.kr/bbs/cse/2615/806199/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (18, 2, '2023-04-06 11:02:42.157', '김호원', '2023', '[김호원 교수] 2023년 졸업과제 주제 및 상담일정[3팀 마감])', 'http://his.pusan.ac.kr/bbs/cse/12549/1172476/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (19, 2, '2023-04-05 09:12:25.006', '이도훈', NULL, '[이도훈교수] 2023년 졸업과제 주제 및 상담일정(3팀 배정완료)', 'http://his.pusan.ac.kr/bbs/cse/12549/1172072/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (20, 2, '2023-04-05 04:51:08.029', '김태운', '2023', '[김태운 교수] 2023년 졸업과제 주제 및 상담일정 (3팀 선발 마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1172062/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (21, 2, '2023-04-05 00:43:08.079', '감진규', NULL, '[감진규 교수] 2023 졸업과제 주제 및 상담일정', 'http://his.pusan.ac.kr/bbs/cse/12549/1172047/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (22, 2, '2023-04-05 00:20:32.923', '송길태', NULL, '[송길태 교수] 2023 졸업과제 주제 및 상담일정 [3팀 배정 완료]', 'http://his.pusan.ac.kr/bbs/cse/12549/1172045/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (23, 2, '2023-04-05 00:16:49.043', '이명호', '2023', '[이명호 교수] 2023 졸업과제 주제 및 상담 일정 (마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1172044/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (24, 2, '2023-04-04 21:42:53.397', '박진선', '2023', '[박진선 교수] 2023 졸업과제 주제 안내 (3팀 선발 마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1172002/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (25, 2, '2023-04-04 19:41:19.587', '안성용', NULL, '[안성용 교수] 2023 졸업과제 주제 및 상담일정', 'http://his.pusan.ac.kr/bbs/cse/12549/1171998/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (26, 2, '2023-04-04 19:09:51.197', '최윤호', '2023', '[최윤호 교수] 2023 졸업과제 주제 및 상담일정(팀배정완료)', 'http://his.pusan.ac.kr/bbs/cse/12549/1171996/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (27, 2, '2023-04-04 17:24:41.773', '이창홍', NULL, '[김종덕 교수] 2023 졸업과제 주제 및 상담 일정 [3팀 마감]', 'http://his.pusan.ac.kr/bbs/cse/12549/1171964/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (28, 2, '2023-04-04 16:27:28.917', '안현기', '2023', '[김원석 교수] 2023 졸업과제 주제 및 상담 일정', 'http://his.pusan.ac.kr/bbs/cse/12549/1171932/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (29, 2, '2023-04-04 16:25:40.963', '이희준', '2023', '[정상화 교수] 2023 졸업과제 주제 및 상담 일정 (마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1171930/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (30, 2, '2023-04-04 16:04:27.503', 'selab', '2023', '[염근혁 교수] 2023 졸업과제 주제 및 상담 일정(팀배정완료)', 'http://his.pusan.ac.kr/bbs/cse/12549/1171923/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (31, 2, '2023-04-04 14:48:59.593', '정주경', '2023', '[권혁철 교수] 2023 졸업과제 주제 및 상담 일정(마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1171913/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (32, 2, '2023-04-04 14:00:26.197', '임준영', '2023', '[유영환 교수] 2023 졸업과제 주제 및 상담 일정', 'http://his.pusan.ac.kr/bbs/cse/12549/1171854/artclView.do');
INSERT INTO content_tb (`content_id`, `notice_id`, `pub_date`, `author`, `category`, `title`, `link`)
VALUES (33, 2, '2023-04-04 11:49:45.967', '최현재', '2023', '[채흥석 교수] 2023년 졸업과제 주제 및 상담 일정 (마감)', 'http://his.pusan.ac.kr/bbs/cse/12549/1171831/artclView.do');

INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (3, 1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (4, 1, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (5, 1, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (6, 1, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (7, 1, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (8, 1, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (9, 1, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (10, 1, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (11, 1, 17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (12, 1, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (13, 1, 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (14, 1, 22, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (15, 1, 23, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (16, 1, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (17, 1, 27, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (18, 1, 28 , CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (19, 1, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (20, 1, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO scrap_tb (`scrap_id`, `user_id`, `content_id`, `created_at`, `last_modified_at`)
VALUES (21, 1, 31, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

