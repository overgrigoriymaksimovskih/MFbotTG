package pro.masterfood.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SimpleHttpClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleHttpClient.class);

    @Value("${server.user}")
    private String username; //  Удален static
    @Value("${server.password}")
    private String password; // Удален static

    //Этот метод для получения баланса
    public String getBalance(String clientId) {
        HttpURLConnection con = null;
        String url = "http://78.29.24.26:54321/sushi2/hs/PC//GetBalance/" + clientId + "/";

        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Базовая аутентификация
            String authString = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8)); // Указана кодировка
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    Gson gson = new Gson();
                    Map<String, Object> responseMap = gson.fromJson(in, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    //Безопасное получение данных и обработка возможных исключений
                    Double balanceValue = (Double) responseMap.get("balance");
                    Double giftSumValue = (Double) responseMap.get("giftSum");

                    if (balanceValue == null || giftSumValue == null) {
                        log.warn("One or more values (balance, giftSum) is null in the response for clientId: {}", clientId);
                        return "Не удалось получить данные о балансе."; // Или другое сообщение по умолчанию
                    }

                    int balance = Math.abs((int) Math.round(balanceValue));
                    int presents = Math.abs((int) Math.round(giftSumValue) / 4000);
                    int reachToPresent = 4000 - Math.abs((int) Math.round(giftSumValue) % 4000);


                    StringBuilder sb = new StringBuilder();
                    sb.append("Доступно бонусных рублей: ").append(balance).append("р.\n");
                    sb.append("Накоплено подарков: ").append(presents).append("\n");
                    sb.append("До следующего подарка осталось: ").append(reachToPresent).append("р.\n");

                    return sb.toString();

                } catch (IOException e) {
                    log.error("-=РЕСТ СЕРВИС -ГЕТ БАЛАНС- НЕ ОТВЕЧАЕТ=-: IOException during connection for user " + clientId, e);
                    return null; // Обработка ошибки чтения ответа
                } catch (NumberFormatException e) {
                    log.error("Error parsing numbers from the response for clientId: " + clientId, e);
                    return "Ошибка обработки числовых данных."; // Или другое сообщение по умолчанию
                }
            } else {
                String errorMessage = "GET request failed. Response code: " + responseCode + ", Message: " + con.getResponseMessage();
                log.error("HTTP request failed for clientId: " + clientId + ", " + errorMessage);
                return null;
            }

        } catch (IOException e) {
            log.error("IOException during connection for user " + clientId, e);
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    //Этот метод для получения статуса заказа
    public String getOrderStatus(String phone) {
        HttpURLConnection con = null;
        String url = "http://78.29.24.26:54321/sushi2/hs/PC/GetStatusByPhone/" + phone.replace("+", "") + "/";

        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            // Базовая аутентификация
            String authString = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8)); // Указана кодировка
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    Gson gson = new Gson();
                    Map<String, Object> responseMap = gson.fromJson(in, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    StringBuilder sb = new StringBuilder();
                    Object statusObject = responseMap.get("Status"); // Получаем статус как Object

                    if (statusObject == null) {
                        log.warn("Status is null in the response for phone: {}", phone);
                        return "Статус заказа не найден.";
                    }

                    String statusNumber = statusObject.toString();

                    switch (statusNumber) {
                        case "8.0":
                            sb.append("Заказ принят и оплачен\n");
                            break;
                        case "0.0":
                            sb.append("Заказ не найден\n");
                            break;
                        case "1.0":
                            sb.append("Заказ принят\n");
                            break;
                        case "2.0":
                            sb.append("Заказ принят и одобрен\n");
                            break;
                        case "3.0":
                            sb.append("Заказ изготавливается ...\n");
                            break;
                        case "4.0":
                            sb.append("Изготовлен и ожидает доставки\n");
                            break;
                        case "5.0":
                            sb.append("Ваш заказ в доставке\n");
                            if (responseMap.containsKey("Courier") && responseMap.containsKey("CourierPhone")) {
                                sb.append("Курьер: ").append(responseMap.get("Courier")).append("\n");
                                sb.append("Телефон: ").append(responseMap.get("CourierPhone")).append("\n");
                            }
                            break;
                        case "6.0":
                        case "7.0":
                            sb.append("Выполнен\n");
                            break;
                        default:
                            sb.append("Заказы не найдены.\n(Информация о заказах становится доступна в день доставки...)");
                    }

                    return sb.toString();

                } catch (IOException e) {
                    log.error("IOException during server response for phone " + phone, e);
                    return null;
                }
            } else {
                String errorMessage = "GET request failed. Response code: " + responseCode + ", Message: " + con.getResponseMessage();
                log.error("HTTP request failed for phone " + phone + ", " + errorMessage);
                return null;
            }

        } catch (IOException e) {
            log.error("-=РЕСТ СЕРВИС -ГЕТ СТАТУС- НЕ ОТВЕЧАЕТ=-: IOException during connection for phone " + phone, e);
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
