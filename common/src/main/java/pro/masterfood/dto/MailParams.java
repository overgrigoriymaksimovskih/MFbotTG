package pro.masterfood.dto;

import lombok.*;
import pro.masterfood.entity.AppUser;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailParams {
    private Long id;//id пользователя в БД

    private Long chatId;
}
