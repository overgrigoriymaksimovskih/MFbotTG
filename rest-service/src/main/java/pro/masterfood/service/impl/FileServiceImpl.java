package pro.masterfood.service.impl;

import pro.masterfood.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppDocumentDAO;
import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.entity.AppDocument;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.entity.BinaryContent;
import pro.masterfood.utils.CryptoTool;

import java.io.File;
import java.io.IOException;

@Component
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final AppPhotoDAO appPhotoDAO;
    private final AppDocumentDAO appDocumentDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppPhotoDAO appPhotoDAO, AppDocumentDAO appDocumentDAO, CryptoTool cryptoTool) {
        this.appPhotoDAO = appPhotoDAO;
        this.appDocumentDAO = appDocumentDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);
        if (id == null){
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }
}
