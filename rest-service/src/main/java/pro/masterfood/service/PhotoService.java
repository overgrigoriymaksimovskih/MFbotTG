package pro.masterfood.service;

import pro.masterfood.entity.AppPhoto;

public interface PhotoService {
    AppPhoto getPhoto(String id);
    boolean deletePhotos(Long id);
}
