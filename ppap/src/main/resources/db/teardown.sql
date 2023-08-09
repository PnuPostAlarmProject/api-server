-- 모든 제약 조건 비활성화
SET REFERENTIAL_INTEGRITY FALSE;
truncate table user_tb;
truncate table notice_tb;
truncate table subscribe_tb;
SET REFERENTIAL_INTEGRITY TRUE;
-- 모든 제약 조건 활성화

INSERT INTO user_tb (`user_id`, `email`, `password`, `role`, `provider`, `created_at`, `last_modified_at`)
VALUES ('1', 'rjsdnxogh@naver.com', '{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'ROLE_USER', 'PROVIDER_KAKAO', '2023-08-08 23:54:29.966', '2023-08-08 23:54:29.966');