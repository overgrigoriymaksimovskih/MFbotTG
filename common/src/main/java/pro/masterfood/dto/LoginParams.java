package pro.masterfood.dto;

import lombok.*;
import pro.masterfood.enums.RequestsToREST;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginParams {

    private RequestsToREST requestType;

    private Long id;

    private Long chatId;

    private String email;

    private String password;
}
