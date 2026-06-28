# API Specifications

## 1. Auth (/users)
- `POST /users/auth/signup` : 추가 회원가입 (학번, 학년, 성별 입력)
- `POST /oauth2/authorization/google` : 구글 로그인 리다이렉트
- `POST /users/auth/reissue` : JWT 토큰 재발급
- `DELETE /api/v1/auth/logout` : 로그아웃
- `DELETE /users/{userId}` : 회원 탈퇴

## 2. Profile (/users/me)
- `GET /users/me/profile` : 내 프로필 상세 조회
- `PUT /users/me/profile` : 프로필 저장 및 수정
- `POST /users/me/images` : 프로필 사진 업로드 (multipart/form-data)
- `DELETE /users/me/images/{imageId}` : 프로필 사진 삭제

## 3. Matching (/matching)
- `POST /matching/request` : 매칭 요청 (하루 1회)
- `GET /matching/results` : 매칭 결과(상대방) 조회
- `PATCH /matching/results/{id}/decision` : 매칭 수락/거절 (ACCEPT/REJECT)

## 4. Chat (/chats)
- `GET /chats/rooms/{roomId}` : 채팅방 상세 내역 조회
- `POST /chats/rooms/{roomId}/messages` : 메시지 전송
- `DELETE /chats/rooms/{roomId}` : 채팅방 나가기
- `PATCH /chats/rooms/{roomId}/read` : 읽음 확인 처리

## 5. Report & Block
- `POST /blocks/{userId}` : 유저 차단
- `GET /blocks` : 차단 목록 조회
- `DELETE /blocks/{userId}` : 차단 해제
- `POST /reports` : 유저 신고 접수
- `GET /admin/reports` : 신고 목록 조회 (관리자용)