# CLAUDE.md

> 이 파일은 Claude Code가 **매 작업마다 가장 먼저 참조**하는 프로젝트 핵심 컨텍스트다.
> 세부 정책·설계·규칙은 모두 `docs/` 하위 문서에 있으니, 작업 전 해당 문서를 반드시 확인한다.

---

## 1. 서비스 한 줄 요약

**인하대학교 재학생 전용 교내 소개팅 웹 서비스** — Google OAuth2(`@inha.ac.kr` 또는 `@inha.edu` 계정만)로 가입하고, 매일 저녁 1:1 매칭과 1:1 채팅을 제공한다. 모바일 웹 최적화, 백엔드 중심 프로젝트.

## 2. 기술 스택 요약

- **백엔드:** Java 21 · Spring Boot 4.0.6 · Spring Security + OAuth2 Client · JWT(jjwt) · Spring Data JPA · PostgreSQL · WebSocket(STOMP) · Spring Scheduler
- **프론트엔드(목표):** React + TypeScript + Zustand *(현재 레포는 React 19 + JavaScript/Vite 스캐폴드 — `docs/architecture.md` 참조)*
- **인프라:** AWS EC2 t3.micro(프리티어) · Docker Compose(로컬) · GitHub Actions(CI/CD)
- **월 운영 비용 목표:** 10만원 이하
- 자세한 버전·의존성·인프라 구성은 → **[docs/architecture.md](docs/architecture.md)**

## 3. 바운디드 컨텍스트

서비스는 5개 바운디드 컨텍스트 + 공통(global)으로 구성한다. 컨텍스트별 책임·핵심 엔티티는 → **[docs/domain-design.md](docs/domain-design.md)**

| 컨텍스트 | 실제 패키지 | 핵심 책임 | 오너 |
|---|---|---|---|
| **Auth** | `org.study.inhamatch.domain.auth` | OAuth2 로그인, 재학생/성별 검증, JWT 발급·재발급, 권한 | 최지원 |
| **Profile** | `org.study.inhamatch.domain.profile` | 프로필·성향 정보 관리 | 최지원 |
| **Matching** | `org.study.inhamatch.domain.match` | 매칭 요청·필터링·일괄 매칭 스케줄링 | 배서연 |
| **Chat** | `org.study.inhamatch.domain.chat` | 채팅방 자동 생성, 실시간 메시지, 알림 | 윤세윤 |
| **Report** | `org.study.inhamatch.domain.report` | 신고 접수, 차단 처리 | 준호 |
| (공통) | `org.study.inhamatch.global` | 공통 설정·예외·유틸·보안 필터 | 준호 |

> ⚠️ 매칭 컨텍스트의 **패키지 폴더명은 `match`** 이다(개념명은 "Matching"). 공통 패키지는 **`global`** 이다.

## 4. 패키지 구조 규칙

- **베이스 패키지:** `org.study.inhamatch`
- 패키지는 **바운디드 컨텍스트 기준**으로 1차 분리한다: `domain.<context>` + `global`
- 각 컨텍스트 내부는 **계층형**으로 구성한다: `controller` / `service` / `repository` / `entity` / `dto`
  - `entity` = JPA 엔티티(도메인 모델), `dto` = 요청/응답 객체(아직 미생성, 생성 예정)
- 컨텍스트 간 **직접 의존을 지양**한다. 경계를 넘는 참조는 **ID 기반** 또는 **명시적 인터페이스**로만 한다.
- 전체 규칙은 → **[docs/conventions.md](docs/conventions.md)**

```
org.study.inhamatch
├─ InhaMatchApplication
├─ domain
│  ├─ auth      └ controller / service / repository / entity / dto
│  ├─ profile   └ controller / service / repository / entity / dto
│  ├─ match     └ controller / service / repository / entity / dto
│  ├─ chat      └ controller / service / repository / entity / dto
│  └─ report    └ controller / service / repository / entity / dto
└─ global       (공통 설정·보안·예외·유틸)
```

## 5. 코딩 컨벤션 핵심

- **DTO ↔ 엔티티 분리.** 엔티티를 컨트롤러 응답으로 **직접 노출 금지**. 항상 DTO로 변환한다.
- **인증은 JWT 기반**, 권한은 `enum Role { USER, ADMIN }` + `@PreAuthorize`로 통제한다.
- 컨텍스트 간 결합을 만들지 않는다(§4의 ID/인터페이스 규칙).
- 엔티티 기본 생성자는 `protected`, 생성은 정적 팩토리/빌더로 한다(기존 `User` 패턴 따름).
- 자세한 컨벤션·네이밍·예외 처리 규칙은 → **[docs/conventions.md](docs/conventions.md)**

## 6. 작업 시작 전 참조 문서

| 작업 성격 | 먼저 볼 문서 |
|---|---|
| 회원/매칭/프로필/채팅/신고 **정책 확인** | [docs/service-policy.md](docs/service-policy.md) |
| 엔티티·컨텍스트 **설계/경계 확인** | [docs/domain-design.md](docs/domain-design.md) |
| 스택·인프라·환경변수·로컬 실행 | [docs/architecture.md](docs/architecture.md) |
| 패키지/계층/네이밍/컨벤션 | [docs/conventions.md](docs/conventions.md) |
| 담당자·오너십·브랜치/PR 규칙 | [docs/team.md](docs/team.md) |
| **아직 결정 안 된 항목(임의 결정 금지)** | [docs/open-decisions.md](docs/open-decisions.md) |

## 7. 작업 시 주의

- **미결정 항목을 임의로 구현하지 않는다.** 대학원생 포함 여부, 매칭 실행 시각, 활성 유저 기준 N일, 프로필 사진 공개 여부, 필수 입력 항목 등은 [docs/open-decisions.md](docs/open-decisions.md)에서 "결정 필요" 상태다. 해당 기능을 건드릴 때는 먼저 확인하고, 결정이 없으면 사용자에게 묻는다.
- **문서-코드 정합성:** 본 문서는 **실제 레포 구조**를 기준으로 작성됐다. 일부 명칭은 초기 기획서와 다르며(예: 패키지 `org.study.inhamatch`, 공통 `global`, 폴더 `match`, 계층 `entity`, enum `Role`), 정렬이 필요한 항목은 [docs/open-decisions.md](docs/open-decisions.md)의 "엔지니어링 정합성" 섹션에 정리돼 있다.
- 이 셋업 단계의 산출물은 **문서뿐**이다. 코드/빌드 파일 작성은 별도 작업으로 진행한다.
