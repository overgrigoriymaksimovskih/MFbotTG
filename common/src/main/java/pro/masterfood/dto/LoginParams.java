package pro.masterfood.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginParams {
    private long chatId;

    private String email;

    private String password;
}
