package pro.masterfood.utils;
//Должен был использоваться при генерации ссылки в письме по которой удаляются сообщения пользователя и его фото
//но вроде как нет смысла потому что ссылку видит только получатель и по ид хоть понятно кого в случае чего искать в БД
import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Decoder {

    private final Hashids hashids;

    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
