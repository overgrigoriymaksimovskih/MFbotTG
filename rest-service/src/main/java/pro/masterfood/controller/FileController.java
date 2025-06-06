package pro.masterfood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.masterfood.service.PhotoService;

@RequestMapping("/api")
@RestController
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final PhotoService photoService;

    public FileController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("id") String id)
    {
        String result;
        Long userId = Long.parseLong(id);
        if(photoService.deletePhotos(userId)){
            result = "Успешно!" + " письмо помечено прочитанным, данные удалены из БД";
        }else{
            result = "Успешно!" + " фото небыли удалены из БД, сообщите администратору...";
        }

        if (result.startsWith("Успешно!")) {
            return ResponseEntity.ok().body(result); // Возвращаем JSON строку
        } else {
            return ResponseEntity.internalServerError().body(result); // Возвращаем JSON строку с ошибкой
        }
    }
}
