package pro.masterfood.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import pro.masterfood.utils.catalogelements.Offer;
import pro.masterfood.utils.catalogelements.YmlCatalog;

@Component
public class ProductDataHolder {

    private static final Logger log = LoggerFactory.getLogger(ProductDataHolder.class);
    private volatile YmlCatalog ymlCatalog; // volatile для потокобезопасности
    private final String xmlUrl = "https://master-food.pro/pics/uploads/YML/29052023.xml";

    public ProductDataHolder() {
        reloadData(); // Загрузка при старте приложения
    }

    public List<Offer> getAllOffers() {
        if (ymlCatalog == null) {
            log.warn("YmlCatalog is null. May be first start");
            reloadData(); // Попытка загрузки
            if (ymlCatalog == null) {
                return null;
            }
        }
        if (ymlCatalog.getShop() == null || ymlCatalog.getShop().getOffers() == null) {
            return null;
        }
        return ymlCatalog.getShop().getOffers().getOffer();
    }
    @Scheduled(fixedRate = 3600000) // Каждые 60 минут (1 час)
    public void reloadData() {
        log.info("Запуск автоматической перезагрузки данных из URL...");
        YmlCatalog newCatalog = loadDataFromXml();
        if (newCatalog != null) {
            synchronized (this) {  //Синхронизация для потокобезопасности
                ymlCatalog = newCatalog;
            }
            log.info("Данные успешно перезагружены.");
        } else {
            log.error("Не удалось перезагрузить данные.");
        }
    }

    private YmlCatalog loadDataFromXml() {
        try {
            URL url = new URL(xmlUrl);
            try (InputStream inputStream = url.openStream()) {
                if (inputStream == null) {
                    log.error("Не удалось открыть поток из URL: {}", xmlUrl);
                    return null;
                }

                JAXBContext jaxbContext = JAXBContext.newInstance(YmlCatalog.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                YmlCatalog loadedCatalog = (YmlCatalog) unmarshaller.unmarshal(inputStream);
                log.info("XML успешно загружен из URL: {}", xmlUrl);
                return loadedCatalog;

            } catch (JAXBException e) {
                log.error("Ошибка JAXB при загрузке XML файла из URL: {}", e.getMessage(), e);
                return null;
            } catch (Exception e) {
                log.error("Неизвестная ошибка при загрузке XML файла из URL: {}", e.getMessage(), e);
                return null;
            }
        } catch (Exception e) {
            log.error("Ошибка при создании URL: {}", e.getMessage(), e);
            return null;
        }
    }


}