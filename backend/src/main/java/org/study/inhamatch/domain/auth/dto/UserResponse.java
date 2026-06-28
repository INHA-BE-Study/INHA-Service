package org.study.inhamatch.domain.auth.dto;

import org.study.inhamatch.domain.auth.entity.Gender;
import org.study.inhamatch.domain.auth.entity.Role;
import org.study.inhamatch.domain.auth.entity.User;
import org.study.inhamatch.domain.auth.entity.UserStatus;

public record UserResponse(
        Long id,
        String email,
        String studentId,
        Role role,
        UserStatus status,
        Integer grade,
        Gender gender
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStudentId(),
                user.getRole(),
                user.getStatus(),
                user.getGrade(),
                user.getGender()
        );
    }
}
