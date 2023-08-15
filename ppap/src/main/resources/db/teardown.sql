-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table notice_tb;
truncate table subscribe_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화

INSERT INTO user_tb (`user_id`, `email`, `password`, `role`, `provider`, `created_at`, `last_modified_at`)
VALUES (1, 'rjsdnxogh@naver.com', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'ROLE_USER', 'PROVIDER_KAKAO', '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO user_tb (`user_id`, `email`, `password`, `role`, `provider`, `created_at`, `last_modified_at`)
VALUES (2, 'rjsdnxogh12@kakao.com', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'ROLE_USER', 'PROVIDER_KAKAO', '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');


INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (1, 'https://cse.pusan.ac.kr/bbs/cse/2615/rssList.do', null, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (2, 'https://cse.pusan.ac.kr/bbs/cse/12549/rssList.do', null, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (3, 'https://chemeng.pusan.ac.kr/bbs/chemeng/2870/rssList.do', null, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO notice_tb (`notice_id`, `rss_link`, `last_time`, `created_at`, `last_modified_at`)
VALUES (4, 'https://french.pusan.ac.kr/bbs/french/4295/rssList.do', null, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');

INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (1, '테스트1', 1, 1, NULL, TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (2, '테스트2', 1, 2, 'https://cse.pusan.ac.kr/cse/14655/subview.do', TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
INSERT INTO subscribe_tb (`subscribe_id`, `title`, `user_id`, `notice_id`, `notice_link`, `is_active`, `created_at`, `last_modified_at`)
VALUES (3, '테스트3', 1, 3, NULL, TRUE, '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');
