package pro.masterfood.dto;

import lombok.*;
import pro.masterfood.entity.AppUser;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailParams {
    //
//    private String id;
//
//    private String emailTo;
    private Long chatId;
    private AppUser appUser;
}
