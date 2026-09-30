package factoriaf5.team2.goxu.profile.dtos;

import jakarta.validation.constraints.NotBlank;

public record CustomerProfileUpdateDTORequest(

        @NotBlank
        String surname,

        String phone,

        String address,

        String postalCode,

        @NotBlank
        String city,

        String avatar
) {
}
