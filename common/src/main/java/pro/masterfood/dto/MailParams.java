package pro.masterfood.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailParams {
    private Long id;//id пользователя в таблице

    private Long chatId;

    private String email;

    private Long siteUid;

    private String phoneNumber;

    private String message;

    private List<byte[]> photos;
}
