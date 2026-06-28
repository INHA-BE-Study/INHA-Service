# 코딩 컨벤션 & 규칙 (Conventions)

패키지 구조, 계층 구성, 컨텍스트 간 의존, 인증/권한, DTO/엔티티 규칙을 정의한다.
본 문서는 **실제 레포(`org.study.inhamatch`) 구조를 기준**으로 한다.

---

## 1. 패키지 구조

- **베이스 패키지:** `org.study.inhamatch`
- 1차 분리는 **바운디드 컨텍스트 기준**: `domain.<context>` + `global`(공통).

```
org.study.inhamatch
├─ InhaMatchApplication
├─ domain
│  ├─ auth
│  ├─ profile
│  ├─ match      ← 개념명 "Matching"
│  ├─ chat
│  └─ report
└─ global         ← 공통(설정/보안/예외/유틸)
```

| 컨텍스트 | 패키지 |
|---|---|
| Auth | `org.study.inhamatch.domain.auth` |
| Profile | `org.study.inhamatch.domain.profile` |
| Matching | `org.study.inhamatch.domain.match` |
| Chat | `org.study.inhamatch.domain.chat` |
| Report | `org.study.inhamatch.domain.report` |
| 공통 | `org.study.inhamatch.global` |

---

## 2. 계층 구성 (컨텍스트 내부)

각 컨텍스트 내부는 **계층형**으로 구성한다.

| 계층 | 패키지 | 책임 |
|---|---|---|
| Controller | `controller` | HTTP 요청/응답, 입력 검증, DTO 입출력 |
| Service | `service` | 비즈니스 로직, 트랜잭션 경계 |
| Repository | `repository` | 영속성(Spring Data JPA) |
| Entity | `entity` | JPA 엔티티(도메인 모델) |
| DTO | `dto` | 요청/응답 객체 *(아직 미생성, 생성 예정)* |

> 현재 레포는 컨텍스트별로 `controller / service / repository / entity` 4계층이 스캐폴드돼 있고(`abc` 플레이스홀더), `dto`는 아직 없다. **DTO는 생성해서 사용한다**(§5).

---

## 3. 컨텍스트 간 의존 규칙

- 컨텍스트 간 **직접 의존을 지양**한다. (다른 컨텍스트의 엔티티/리포지토리를 직접 import 하지 않는다.)
- 경계를 넘는 참조는 다음 중 하나로만 한다:
  - **ID 기반 참조** — 다른 컨텍스트의 식별자(`Long userId` 등)만 보관.
  - **명시적 인터페이스** — 제공 측이 노출한 포트(인터페이스)를 통해 질의/호출.
- 모든 컨텍스트는 `global`(공통)에만 의존할 수 있다.
- 컨텍스트 맵은 [domain-design.md](domain-design.md) 참조.

---

## 4. 인증 / 권한

- 인증은 **JWT 기반**(액세스 토큰 발급 + 리프레시 재발급).
- 권한은 `enum Role { USER, ADMIN }` 으로 표현한다.
  - 각 값은 Spring Security 키를 보유: `USER → ROLE_USER`, `ADMIN → ROLE_ADMIN`.
- 관리자/보호 기능은 **`@PreAuthorize`**로 통제한다.
- OAuth2 로그인은 `@inha.ac.kr` 도메인만 허용한다(`CustomOAuth2UserService`).

> 참고: 기획서는 enum명을 `UserRole`로 표기했으나, **실제 코드의 enum명은 `Role`**이다(§9 정합성 참조).

---

## 5. DTO / 엔티티 규칙

- **DTO와 엔티티를 분리**한다.
- **엔티티를 컨트롤러 응답으로 직접 노출하지 않는다.** 항상 DTO로 변환하여 반환한다.
- 요청 바디도 엔티티가 아닌 요청 DTO로 받는다.

---

## 6. 엔티티 작성 규칙 (기존 `User` 패턴)

기존 `User` 엔티티의 패턴을 기본 규칙으로 따른다.

- 기본 생성자는 **`protected`** (`@NoArgsConstructor(access = PROTECTED)`).
- 생성은 **정적 팩토리 메서드**(예: `User.create(...)`) 또는 빌더를 사용(빌더는 `private`).
- `@Getter`만 노출하고 무분별한 `@Setter`는 지양한다.
- enum 매핑은 **`@Enumerated(EnumType.STRING)`**.
- soft delete가 필요한 엔티티는 `deletedAt` 컬럼을 둔다(예: `User`).
- 테이블명은 명시한다(예: `@Table(name = "users")`).

---

## 7. 네이밍 규칙

| 대상 | 규칙 | 예 |
|---|---|---|
| 패키지 | 소문자, 컨텍스트 단수형 | `match`, `report` |
| 클래스 | PascalCase | `MatchRequest`, `ChatRoom` |
| 엔티티 | 도메인 명사 | `User`, `Profile`, `Block` |
| 컨트롤러 | `*Controller` | `MatchController` |
| 서비스 | `*Service` | `MatchService` |
| 리포지토리 | `*Repository` | `UserRepository` |
| DTO | 용도 접미사 | `*Request`, `*Response` |

---

## 8. 문서/코드 동기화

- 정책·설계·스택이 바뀌면 **코드와 함께 관련 `docs/` 문서를 갱신**한다.
- 미결정 항목([open-decisions.md](open-decisions.md))은 **임의로 구현하지 않는다.** 확정 시 문서에 먼저 반영 후 구현한다.

---

## 9. ⚠️ 코드–문서 정합성 (네이밍 정렬 필요)

본 문서는 **실제 레포 구조**를 기준으로 썼다. 초기 기획서 표기와 실제 코드가 다른 항목이 있으며, 통일이 필요하면 팀이 결정한다(→ [open-decisions.md](open-decisions.md) "엔지니어링 정합성" 섹션).

| 항목 | 기획서 표기 | 실제 코드 | 본 문서 채택 |
|---|---|---|---|
| 베이스 패키지 | `com.inha.dating` *(예시로 표기)* | `org.study.inhamatch` | **실제** |
| 공통 패키지 | `common` | `global` | **실제** |
| 매칭 폴더 | `matching` | `match` | **실제** |
| 계층 명칭 | `domain` | `entity` | **실제**(`entity`) |
| 권한 enum | `UserRole` | `Role` | **실제**(`Role`) |
| 프론트 언어 | TypeScript + Zustand | JavaScript(JSX), 미도입 | 목표는 TS+Zustand, 현재 JS |

> 채택 원칙: **실제 레포가 진실의 원천**이다. 기획서의 패키지명은 "예:"로 제시된 예시였고, 코드가 이미 일관되게 `org.study.inhamatch`를 사용하므로 이를 따랐다. 만약 팀이 기획서 명칭으로 **통일(리네임)**하기로 하면, 본 문서들과 코드를 함께 수정한다.
