package pro.masterfood.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pro.masterfood.service.OfferService;
import pro.masterfood.utils.ProductDataHolder;

import pro.masterfood.utils.catalogelements.Offer;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OfferServiceImpl implements OfferService {

    private static final Logger log = LoggerFactory.getLogger(OfferServiceImpl.class);
    private final ProductDataHolder productDataHolder; // Используем класс для хранения данных

    public OfferServiceImpl(ProductDataHolder productDataHolder) {
        this.productDataHolder = productDataHolder;
    }

//    @Override
//    public String getOfferDetails(String searchText) {
//        List<Offer> allOffers = productDataHolder.getAllOffers();
//        if (allOffers == null || allOffers.isEmpty()) {
//            return "Товары не найдены.";
//        }
//
//        // Фильтруем товары по тексту запроса
//        List<Offer> foundOffers = allOffers.stream()
//                .filter(offer -> offer.getName().toLowerCase().contains(searchText.toLowerCase()) ||
//                        offer.getDescription().toLowerCase().contains(searchText.toLowerCase()))
//                .collect(Collectors.toList());
//
//        if (foundOffers.isEmpty()) {
//            return "Товары по запросу \"" + searchText + "\" не найдены.";
//        }
//
//        // Формируем ответ
//        StringBuilder details = new StringBuilder();
//        details.append("***");
//
//        if (foundOffers.isEmpty()) {
//            return "Товары по запросу \"" + searchText + "\" не найдены.";
//        }
//
//        try {
//            for (Offer offer : foundOffers) {
//                String escapedName = (offer.getName() != null) ? escapeMarkdown(offer.getName()) : "Название отсутствует";
//                String url = (offer.getUrl() != null) ? offer.getUrl() : ""; // Можно не экранировать, если URL нет
//                Double price = (offer.getPrice() != null) ? Double.valueOf(offer.getPrice()) : 0.0;
//                String currencyId = (offer.getCurrencyId() != null) ? offer.getCurrencyId() : "";
//                String description = (offer.getDescription() != null) ? escapeMarkdown(offer.getDescription()) : "Описание отсутствует";
//
//                details.append("*").append(escapedName).append("*\n");
//                details.append("Цена: ").append(price).append(" ").append(currencyId).append("\n");
//                details.append("Описание: ").append(description).append("\n");
//                details.append("[Смотреть на сайте](").append(url).append(")\n\n");
//            }
//        } catch (Exception e) {
//            log.error("Ошибка при формировании ответа: {}", e.getMessage(), e);
//            return "Произошла ошибка при формировании ответа." + e.getMessage();
//        }
//
//        return details.toString();
//    }

    @Override
    public String getOfferDetails(String searchText) {
        int maxLength = 3000;
        List<Offer> allOffers = productDataHolder.getAllOffers();
        if (allOffers == null || allOffers.isEmpty()) {
            return "Товары не найдены.";
        }

        // Фильтруем товары по тексту запроса
        List<Offer> foundOffers = allOffers.stream()
                .filter(offer -> offer.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        offer.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        if (foundOffers.isEmpty()) {
            return "Товары по запросу \"" + searchText + "\" не найдены.";
        }

        // Формируем ответ
        StringBuilder details = new StringBuilder();
        int counter = 0;
        try {
            for (Offer offer : foundOffers) {
                String escapedName = (offer.getName() != null) ? escapeMarkdown(offer.getName()) : "Название отсутствует";
                String url = (offer.getUrl() != null) ? offer.getUrl() : ""; // Можно не экранировать, если URL нет
                Double price = (offer.getPrice() != null) ? Double.valueOf(offer.getPrice()) : 0.0;
                String currencyId = (offer.getCurrencyId() != null) ? offer.getCurrencyId() : "";
                String description = (offer.getDescription() != null) ? escapeMarkdown(offer.getDescription()) : "Описание отсутствует";

                String offerDetails = "*" + escapedName + "*\n" +
                        "Цена: " + price + " " + currencyId + "\n" +
                        "Описание: " + description + "\n" +
                        "[Смотреть на сайте](" + url + ")\n\n";

                if (details.length() + offerDetails.length() <= maxLength) {
                    details.append(offerDetails);
                } else {
                    counter++;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при формировании ответа: {}", e.getMessage(), e);
            return "Произошла ошибка при формировании ответа." + e.getMessage();
        }

        if (counter > 0) {
            details.append("И еще ").append(counter).append(" товаров, которые можно посмотреть на сайте.");
        }

        return details.toString();
    }
    private String escapeMarkdown(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if ("[]()".contains(String.valueOf(c))) { // Экранируем только скобки
                sb.append("\\");
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
