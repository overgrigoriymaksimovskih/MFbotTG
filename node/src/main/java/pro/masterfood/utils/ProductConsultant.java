//package pro.masterfood.utils;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Unmarshaller;
//import jakarta.xml.bind.annotation.*;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Component
//public class ProductConsultant {
//
//    private static final Logger log = LoggerFactory.getLogger(ProductConsultant.class);
//    private volatile YmlCatalog ymlCatalog; // volatile для потокобезопасности
//    private final String xmlUrl = "https://master-food.pro/pics/uploads/YML/29052023.xml";
//
//    public ProductConsultant() {
//        reloadData(); // Загрузка при старте приложения
//    }
//
//    public String getOfferDetails(String offerId) {
//        if (ymlCatalog == null) {
//            log.warn("YmlCatalog is null. May be first start");
//            reloadData(); // Попытка загрузки
//            if (ymlCatalog == null) {
//                return "Сервис временно недоступен. Попробуйте позже.";
//            }
//        }
//        List<Offer> allOffers = ymlCatalog.getShop().getOffers().getOffer();
//        if (allOffers == null || allOffers.isEmpty()) {
//            return "Товар не найден.";
//        }
//
//        Offer offer = allOffers.stream()
//                .filter(o -> Objects.equals(o.getId(), offerId))
//                .findFirst()
//                .orElse(null);
//
//        if (offer == null) {
//            return "Мы искали товар " + offerId + " - Товар не найден.";
//        }
//
//        String escapedName = escapeMarkdown(offer.getName());
//        String url = offer.getUrl(); // Не экранируем URL целиком!
//        StringBuilder details = new StringBuilder();
//        details.append("").append(escapedName).append("\n");
//        details.append("Цена: ").append(offer.getPrice()).append(" ").append(offer.getCurrencyId()).append("\n");
//        details.append("\n").append(escapeMarkdown(offer.getDescription())).append("\n");
//        details.append("").append(url); // Используем URL без экранирования
//        return details.toString();
//
//    }
//    @Scheduled(fixedRate = 3600000) // Каждые 60 минут (1 час)
//    public void reloadData() {
//        log.info("Запуск автоматической перезагрузки данных из URL...");
//        YmlCatalog newCatalog = loadDataFromXml();
//        if (newCatalog != null) {
//            synchronized (this) {  //Синхронизация для потокобезопасности
//                ymlCatalog = newCatalog;
//            }
//            log.info("Данные успешно перезагружены.");
//        } else {
//            log.error("Не удалось перезагрузить данные.");
//        }
//    }
//
//    private YmlCatalog loadDataFromXml() {
//        try {
//            URL url = new URL(xmlUrl);
//            try (InputStream inputStream = url.openStream()) {
//                if (inputStream == null) {
//                    log.error("Не удалось открыть поток из URL: {}", xmlUrl);
//                    return null;
//                }
//
//                JAXBContext jaxbContext = JAXBContext.newInstance(YmlCatalog.class);
//                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//                YmlCatalog loadedCatalog = (YmlCatalog) unmarshaller.unmarshal(inputStream);
//                log.info("XML успешно загружен из URL: {}", xmlUrl);
//                return loadedCatalog;
//
//            } catch (JAXBException e) {
//                log.error("Ошибка JAXB при загрузке XML файла из URL: {}", e.getMessage(), e);
//                return null;
//            } catch (Exception e) {
//                log.error("Неизвестная ошибка при загрузке XML файла из URL: {}", e.getMessage(), e);
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("Ошибка при создании URL: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//    private String escapeMarkdown(String text) {
//        if (text == null) return "";
//        StringBuilder sb = new StringBuilder();
//        for (char c : text.toCharArray()) {
//            if ("[]()".contains(String.valueOf(c))) { // Экранируем только скобки
//                sb.append("\\");
//            }
//            sb.append(c);
//        }
//        return sb.toString();
//    }
//
//}