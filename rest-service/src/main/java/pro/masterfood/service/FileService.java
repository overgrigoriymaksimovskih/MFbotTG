package pro.masterfood.service;

import org.springframework.core.io.FileSystemResource;
import pro.masterfood.entity.AppDocument;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    boolean deletePhotos(Long id);
}
