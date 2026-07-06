package org.study.inhamatch.domain.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.study.inhamatch.domain.auth.entity.Gender;

public record SignupRequest(
        @NotNull @Min(1) @Max(4) Integer grade,
        @NotNull Gender gender
) {
}
