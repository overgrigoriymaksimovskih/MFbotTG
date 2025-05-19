package pro.masterfood.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProductConsultant {

    private static final Logger log = LoggerFactory.getLogger(ProductConsultant.class);
    private final YmlCatalog ymlCatalog;

    public ProductConsultant() {
        this.ymlCatalog = loadDataFromXml();
    }

    private YmlCatalog loadDataFromXml() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("products.xml")) {
            if (inputStream == null) {
                log.error("Не удалось найти файл products.xml");
                return null;
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(YmlCatalog.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (YmlCatalog) unmarshaller.unmarshal(inputStream);

        } catch (JAXBException e) {
            log.error("Ошибка при загрузке XML файла: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("Неизвестная ошибка при загрузке XML файла: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getOfferDetails(String offerId) {
        if (ymlCatalog == null || ymlCatalog.getShop() == null || ymlCatalog.getShop().getOffers() == null) {
            return "Товар не найден.";
        }

        List<Offer> allOffers = ymlCatalog.getShop().getOffers().getOffer();
        if (allOffers == null || allOffers.isEmpty()) {
            return "Товар не найден.";
        }

        Offer offer = allOffers.stream()
                .filter(o -> Objects.equals(o.getId(), offerId))
                .findFirst()
                .orElse(null);

        if (offer == null) {
            return "Мы искали товар " + offerId + " - Товар не найден.";
        }

        StringBuilder details = new StringBuilder();
        details.append("<b>").append(offer.getName()).append("</b>\n");
        details.append("Цена: ").append(offer.getPrice()).append(" ").append(offer.getCurrencyId()).append("\n");
        details.append("Описание: ").append(offer.getDescription()).append("\n");
        details.append("Ссылка: <a href=\"").append(offer.getUrl()).append("\">Перейти на сайт</a>\n");

        return details.toString();
    }
    // Добавляем метод для поиска по имени
    public String findOffersByName(String productName) {
        if (ymlCatalog == null || ymlCatalog.getShop() == null || ymlCatalog.getShop().getOffers() == null) {
            return "Товары не найдены.";
        }

        List<Offer> allOffers = ymlCatalog.getShop().getOffers().getOffer();
        if (allOffers == null || allOffers.isEmpty()) {
            return "Товары не найдены.";
        }

        List<Offer> foundOffers = allOffers.stream()
                .filter(offer -> offer.getName().toLowerCase().contains(productName.toLowerCase()))
                .collect(Collectors.toList());

        if (foundOffers.isEmpty()) {
            return "Товары с названием \"" + productName + "\" не найдены.";
        }

        StringBuilder result = new StringBuilder();
        for (Offer offer : foundOffers) {
            result.append("<b>").append(offer.getName()).append("</b>\n");
            result.append("Цена: ").append(offer.getPrice()).append(" ").append(offer.getCurrencyId()).append("\n");
            result.append("Описание: ").append(offer.getDescription()).append("\n");
            result.append("Ссылка: <a href=\"").append(offer.getUrl()).append("\">Перейти на сайт</a>\n");
            result.append("------\n"); // Разделитель между товарами
        }
        return result.toString();
    }

}