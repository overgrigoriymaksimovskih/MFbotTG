package pro.masterfood.service.impl;

import pro.masterfood.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.entity.AppPhoto;
import pro.masterfood.utils.Decoder;

@Component
public class PhotoServiceImpl implements PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoServiceImpl.class);
    private final AppPhotoDAO appPhotoDAO;
    private final Decoder decoder;

    public PhotoServiceImpl(AppPhotoDAO appPhotoDAO, Decoder decoder) {
        this.appPhotoDAO = appPhotoDAO;
        this.decoder = decoder;
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = decoder.idOf(hash);
        if (id == null) {
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public boolean deletePhotos(Long id) {
        try {
            appPhotoDAO.deleteByOwnerId(id);
        } catch (Exception e) {
            return false;
        } return true;
    }
}
