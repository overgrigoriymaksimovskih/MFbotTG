package pro.masterfood.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.hashids.Hashids;
import org.json.JSONObject;
import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.dao.BinaryContentDAO;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.AppUser;
import pro.masterfood.entity.BinaryContent;
import pro.masterfood.exceptions.UploadFileException;
import pro.masterfood.service.FileService;
import pro.masterfood.service.enums.LinkType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.adress}")
    private String linkAddress;

    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final Hashids hashids;

    public FileServiceImpl(AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, Hashids hashids) {
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.hashids = hashids;
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage, AppUser owner, String message) {

        var photoSizeCount = telegramMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;

        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppDoc = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent, owner, message);
            return appPhotoDAO.save(transientAppDoc);
        } else {
            log.error("Bad response from telegram service " + response);
            throw new UploadFileException("Bad response from telegram service " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent, AppUser owner, String message) {
        return AppPhoto.builder()
                .telegramField(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .owner(owner)
                .message(message)
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try{
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e){
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()){
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = hashids.encode(docId);
        return linkAddress + "/api/" + linkType + "?id=" + hash;
    }
}
