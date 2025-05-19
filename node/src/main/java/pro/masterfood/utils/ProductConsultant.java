package pro.masterfood.utils;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProductConsultant {

//    private static final Logger log = LoggerFactory.getLogger(ProductConsultant.class);
    private final Map<String, Object> data;

    public ProductConsultant() {
        this.data = loadDataFromYaml();
    }

    private Map<String, Object> loadDataFromYaml() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("products.xml")) { // Changed to xml
            if (inputStream == null) {
//                log.error("Не удалось найти файл products.xml");
                return new HashMap<>();
            }
            Yaml yaml = new Yaml();
            return yaml.load(inputStream); // Load as generic Map
        } catch (Exception e) {
//            log.error("Ошибка при загрузке YML файла: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    public List<Map<String, Object>> getAllOffers() {
        try {
            Map<String, Object> ymlCatalog = (Map<String, Object>) data.get("yml_catalog");
            Map<String, Object> shop = (Map<String, Object>) ymlCatalog.get("shop");
            Map<String, Object> offers = (Map<String, Object>) shop.get("offers");
            List<Map<String, Object>> offerList = (List<Map<String, Object>>) ((Map<?, ?>) offers).get("offer"); // Corrected type casting
            return offerList;
        } catch (Exception e) {
//            log.error("Ошибка при получении списка товаров: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> findOffersByCategoryId(String categoryId) {
        List<Map<String, Object>> allOffers = getAllOffers();
        if (allOffers == null || allOffers.isEmpty()) {
            return new ArrayList<>();
        }
        return allOffers.stream()
                .filter(offer -> Objects.equals(offer.get("categoryId"), categoryId))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> findOffersByName(String name) {
        List<Map<String, Object>> allOffers = getAllOffers();
        if (allOffers == null || allOffers.isEmpty()) {
            return new ArrayList<>();
        }
        return allOffers.stream()
                .filter(offer -> {
                    String offerName = (String) offer.get("name");
                    return offerName != null && offerName.toLowerCase().contains(name.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public String getOfferDetails(String offerId) {
        List<Map<String, Object>> allOffers = getAllOffers();
        if (allOffers == null) {
            return "Товар не найден. Потому что список всех товаров полученный из getOfferDetails is null";
        }else if (allOffers.isEmpty()){
            return "Товар не найден. Потому что список всех товаров полученный из getOfferDetails пуст";
        }

        Map<String, Object> offer = allOffers.stream()
                .filter(o -> Objects.equals(o.get("id"), offerId))
                .findFirst()
                .orElse(null);

        if (offer == null) {
            return "Мы искали товар " + offerId + " - Товар не найден.";
        }

        StringBuilder details = new StringBuilder();
        details.append("<b>").append(offer.get("name")).append("</b>\n");
        details.append("Цена: ").append(offer.get("price")).append(" ").append(offer.get("currencyId")).append("\n");
        details.append("Описание: ").append(offer.get("description")).append("\n");
        details.append("Ссылка: <a href=\"").append(offer.get("url")).append("\">Перейти на сайт</a>\n");

        return details.toString();
    }
}