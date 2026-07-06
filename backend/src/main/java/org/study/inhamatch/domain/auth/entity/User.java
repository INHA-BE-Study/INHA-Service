package org.study.inhamatch.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Integer grade;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private User(String email, String studentId, Role role, UserStatus status,
                   Integer grade, Gender gender, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.email = email;
        this.studentId = studentId;
        this.role = role;
        this.status = status;
        this.grade = grade;
        this.gender = gender;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    public static User create(String email, String studentId) {
        return new User(
                email,
                studentId,
                Role.USER,
                UserStatus.PENDING,
                null,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public void completeSignup(Integer grade, Gender gender) {
        if (this.status == UserStatus.ACTIVE) {
            throw new IllegalStateException("이미 가입이 완료된 사용자입니다.");
        }
        this.grade = grade;
        this.gender = gender;
        this.status = UserStatus.ACTIVE;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
