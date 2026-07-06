package org.study.inhamatch.domain.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false)
    private String tagValue;

    private ProfileTag(Profile profile, String tagValue) {
        this.profile = profile;
        this.tagValue = tagValue;
    }

    public static ProfileTag create(Profile profile, String tagValue) {
        return new ProfileTag(profile, tagValue);
    }
}
