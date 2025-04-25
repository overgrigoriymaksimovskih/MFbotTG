package pro.masterfood.service.utils;

import pro.masterfood.dao.AppPhotoDAO;
import pro.masterfood.entity.AppPhoto;

import java.util.List;

public class PhotoDbHarvester {
    private final AppPhotoDAO appPhotoDAO;

    public PhotoDbHarvester(AppPhotoDAO appPhotoDAO) {
        this.appPhotoDAO = appPhotoDAO;
    }

    public List<AppPhoto> getAllPhotosByUserId(Long key) {
        return appPhotoDAO.findByKey(key);
    }
}
