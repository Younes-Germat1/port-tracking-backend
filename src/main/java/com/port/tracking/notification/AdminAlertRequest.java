package com.port.tracking.notification;

import com.port.tracking.user.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAlertRequest {

    @NotNull
    private UserRole targetRole;

    @NotBlank
    private String message;

    // Optional — if set, the alert is linked to this fiche and shows up
    // when the recipient clicks through, same as other fiche notifications.
    private Long ficheId;
}