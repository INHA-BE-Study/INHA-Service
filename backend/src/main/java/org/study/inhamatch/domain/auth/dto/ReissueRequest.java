package org.study.inhamatch.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank String refreshToken
) {
}
