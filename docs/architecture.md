# 아키텍처 (Architecture)

기술 스택, 인프라 구성, 비용 목표, 로컬 실행 방법을 정의한다.

---

## 1. 기술 스택

### 1.1 백엔드 (현재 레포 기준 확정)
| 구분 | 기술 | 버전/비고 |
|---|---|---|
| 언어 | Java | 21 (toolchain) |
| 프레임워크 | Spring Boot | 4.0.6 |
| 웹 | spring-boot-starter-webmvc | |
| 보안 | Spring Security | |
| 인증 | Spring Security OAuth2 Client | Google |
| 토큰 | JWT — `io.jsonwebtoken:jjwt` | 0.13.0 |
| 데이터 | Spring Data JPA | |
| DB | PostgreSQL | 운영/로컬 공통 |
| 검증 | spring-boot-starter-validation | |
| 실시간 | spring-boot-starter-websocket | STOMP |
| 스케줄 | Spring Scheduler | 매칭 마감/실행 |
| 보일러플레이트 | Lombok | |

### 1.2 추가 예정 (단계적 도입)
| 기술 | 도입 시점 |
|---|---|
| Gemini Flash API (매칭 고도화) | MVP 규칙 기반 이후 |
| 알림: Firebase **또는** SSE | Chat 구현 단계 (수단 검토 중) |
| Redis | 사용자 규모 증가 시 |
| S3 (이미지 저장) | MVP 이후 (프로필 사진 정책 확정 후) |

### 1.3 프론트엔드
| 구분 | 목표 | 현재 레포 상태 |
|---|---|---|
| 라이브러리 | React | React **19.2** |
| 언어 | **TypeScript** | **JavaScript**(`.jsx`) — TS 미적용 |
| 상태관리 | **Zustand** | 미도입 |
| 번들러 | Vite | Vite 8 |

> ⚠️ 기획상 프론트 스택은 **React + TypeScript + Zustand**이나, 현재 레포는 **React 19 + JavaScript(Vite) 스캐폴드** 상태다. TS/Zustand 도입은 별도 작업. 본 서비스는 **백엔드 중심**이다.

---

## 2. 인프라

| 구분 | 구성 | 비고 |
|---|---|---|
| 컴퓨팅 | AWS EC2 **t3.micro** | 프리티어 1년 |
| 컨테이너(로컬) | Docker Compose | PostgreSQL 컨테이너 |
| CI/CD | GitHub Actions | |
| 이미지 저장 | S3 | MVP 이후 |
| 플랫폼 | 웹 (모바일 최적화) | |

### 2.1 컨테이너 구성 (현재)
- `backend/docker-compose.yml`: `postgres:16` 컨테이너 1개.
  - DB: `inhamatch`, 사용자/비번 기본값 `inhamatch` (`${DB_USERNAME}` / `${DB_PASSWORD}`로 오버라이드).
  - 포트 매핑: **호스트 5433 → 컨테이너 5432**.
- `backend/Dockerfile`: `gradle:8.12-jdk21`로 빌드 → `eclipse-temurin:21-jre-jammy` 런타임, 8080 노출.

---

## 3. 환경 변수

`.env`는 커밋 금지(루트 `.gitignore`에서 차단, `.env.example`/`.env.sample` 템플릿만 허용).

> ⚠️ 현재 `backend/.env`에는 **실제 Google OAuth·JWT 시크릿이 평문**으로 들어있다. 절대 커밋/공유 금지. (`.env.example` 템플릿은 아직 없음 — 추후 placeholder 템플릿 추가 권장.)

| 변수 | 용도 | 기본값 |
|---|---|---|
| `DB_USERNAME` | PostgreSQL 사용자 | `inhamatch` |
| `DB_PASSWORD` | PostgreSQL 비밀번호 | `inhamatch` |
| `GOOGLE_CLIENT_ID` | Google OAuth2 클라이언트 ID | (필수) |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 시크릿 | (필수) |
| `jwt.*` | JWT 서명 키 등 | `application.yml`에 키 자리만 있음(미설정) |

- OAuth2 scope: `email`, `profile`.
- `application.yml`의 `jpa.hibernate.ddl-auto`는 현재 `create-drop`(로컬 개발용). 운영 전 정책 필요.

---

## 4. 로컬 실행 개요

현재 레포 구성 기준 로컬 실행 명령이다(작업 디렉터리는 레포 루트).

```bash
# 1. PostgreSQL 기동 (호스트 5433 → 컨테이너 5432)
cd backend && docker compose up -d

# 2. 환경변수: backend/.env 설정 (§3 표 참고)

# 3. 백엔드 실행 (8080)
cd backend && ./gradlew bootRun        # PowerShell: .\gradlew.bat bootRun

# 4. 프론트 dev 서버 (Vite)
cd frontend && npm install && npm run dev
```

- 테스트: `cd backend && ./gradlew test`
- 빌드: `cd backend && ./gradlew build`
- 프론트: `npm run build`(프로덕션 번들) / `npm run lint`(ESLint)

서버 포트: **8080** / DB 포트(로컬): **5433**.

---

## 5. 비용 목표

**월 운영 비용 10만원 이하**를 목표로 한다.

| 항목 | 단계 | 예상 비용 수준 |
|---|---|---|
| EC2 t3.micro | 프리티어 1년 | 무료 (이후 인스턴스 비용 발생) |
| PostgreSQL | EC2 내 컨테이너로 자체 운영 | 추가 비용 최소화 |
| Gemini Flash API | MVP 이후 | 사용량 기반(저비용 모델 선택) |
| S3 (이미지) | MVP 이후 | 사용량 기반 |
| Redis | 사용자 증가 시 | 도입 시 재산정 |

> 비용 절감 전략: 프리티어 활용, 관리형 DB 대신 컨테이너 자체 운영, 외부 API는 저비용 모델(Gemini Flash) 우선, Redis·S3는 필요 시점까지 도입 보류. 구체적 금액은 운영 단계에서 재산정한다.
