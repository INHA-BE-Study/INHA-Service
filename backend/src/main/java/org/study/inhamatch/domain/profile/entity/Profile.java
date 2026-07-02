package org.study.inhamatch.domain.profile.entity;

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
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String bio;

    private String mbti;

    @Enumerated(EnumType.STRING)
    private LoveStyle loveStyle;

    @Enumerated(EnumType.STRING)
    private SmokingStatus smokingStatus;

    @Enumerated(EnumType.STRING)
    private DrinkingStatus drinkingStatus;

    private String photoKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Profile(Long userId) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public static Profile create(Long userId) {
        return new Profile(userId);
    }

    public void update(String bio, String mbti, LoveStyle loveStyle,
                       SmokingStatus smokingStatus, DrinkingStatus drinkingStatus) {
        this.bio = bio;
        this.mbti = mbti;
        this.loveStyle = loveStyle;
        this.smokingStatus = smokingStatus;
        this.drinkingStatus = drinkingStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePhotoKey(String photoKey) {
        this.photoKey = photoKey;
        this.updatedAt = LocalDateTime.now();
    }

    public void removePhotoKey() {
        this.photoKey = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasPhoto() {
        return photoKey != null;
    }
}
