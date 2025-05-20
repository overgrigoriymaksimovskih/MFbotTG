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

    @Override
    public String getOfferDetails(String searchText) {
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
        for (Offer offer : foundOffers) {
            String escapedName = escapeMarkdown(offer.getName());
            String url = offer.getUrl();
            details.append("*").append(escapedName).append("*\n");
            details.append("Цена: ").append(offer.getPrice()).append(" ").append(offer.getCurrencyId()).append("\n");
            details.append("Описание: ").append(escapeMarkdown(offer.getDescription())).append("\n");
            details.append("[Смотреть на сайте](").append(url).append(")\n\n");
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
