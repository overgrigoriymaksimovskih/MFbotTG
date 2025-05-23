package pro.masterfood.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.masterfood.service.FileService;

import pro.masterfood.dao.AppPhotoDAO;

import java.io.IOException;

@RequestMapping("/api")
@RestController
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    public String hello() {
        return "Hello from FileController!";
    }

    //    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
//    public String getDoc(@RequestParam("id") String id, HttpServletResponse response) {
//        if (id == "999"){
//            return "999";
//        }else{
//            return "fuck you";
//        }
//    }
    @RequestMapping(method = RequestMethod.GET, value = "/file/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response){
        //TODO для формирования БадРеквест добавить ControllerAdvice
        var doc = fileService.getDocument(id);

        if (doc == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.parseMediaType(doc.getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment; filename=" + doc.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);


        var binaryContent = doc.getBinaryContent();
        try (var out = response.getOutputStream()) {
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/file/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response){
        //TODO для формирования БадРеквест добавить ControllerAdvice
        var photo = fileService.getPhoto(id);
        if (photo == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = photo.getBinaryContent();
        try (var out = response.getOutputStream()) {
            out.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("id") String id)
    {
        String result;
        Long userId = Long.parseLong(id);
        if(fileService.deletePhotos(userId)){
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
