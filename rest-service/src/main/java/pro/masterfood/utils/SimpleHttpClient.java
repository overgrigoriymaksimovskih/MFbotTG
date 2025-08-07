package pro.masterfood.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.text.html.parser.Entity;

@RequiredArgsConstructor
@Component
public class SimpleHttpClient {

    public static String getBalance(String clientId) {

        String username = "ws";
        String password = "R1xHoHG";

        HttpURLConnection con = null;
//        String url = "http://78.29.24.26:54321/sushi2/hs/PC//GetBalance/" + clientId + "/";
        String url = "http://192.168.127.36:54321/sushi2/hs/PC//GetBalance/" + clientId + "/";
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // Устанавливаем метод запроса (GET по умолчанию)
            con.setRequestMethod("GET");

            // Базовая аутентификация
            String authString = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // Получаем код ответа
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                // Читаем ответ из потока ввода
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    // Преобразуем JSON в Map<String, Object> с использованием Gson
                    Gson gson = new Gson();
                    // Используем TypeToken для правильной типизации Map
                    Map<String, Object> responseMap = gson.fromJson(in, new TypeToken<Map<String, Object>>() {}.getType());

                    int balance = Math.abs((int)Double.parseDouble(responseMap.get("balance").toString()));
                    int presents = Math.abs((int)Double.parseDouble(responseMap.get("giftSum").toString()) / 4000);
                    int reachToPresent = 4000 - Math.abs((int)Double.parseDouble(responseMap.get("giftSum").toString()) % 4000);
                    int bonusPoints = 0;

                    StringBuilder sb = new StringBuilder();
//                    sb.append("На вашем счету: " + balance + "р." + "\n");
                    sb.append("Доступно бонусных рублей: " + balance + "р." + "\n");
                    sb.append("Накоплено подарков: " + presents + "\n");
//                    sb.append("Доступно бонусных рублей: " + bonusPoints + "р." + "\n");
                    sb.append("До следующего подарка осталось: " + reachToPresent + "р." + "\n");

                    return sb.toString(); // Возвращаем Map


                } catch (IOException e) {
                    System.err.println("Error reading response: " + e.getMessage());
                    return null; // Обработка ошибки чтения ответа
                }
            } else {
                String errorMessage = "GET request failed. Response code: " + responseCode + ", Message: " + con.getResponseMessage();
                System.err.println(errorMessage);
                return null; // Обработка ошибки ответа сервера (например, 401 Unauthorized)
            }

        } catch (IOException e) {
            System.err.println("IOException during connection: " + e.getMessage());
            return null; // Обработка общей ошибки соединения
        } finally {
            if (con != null) {
                con.disconnect(); // Закрываем соединение в блоке finally
            }
        }
    }

    public static String getOrderStatus(String phone) {

        String username = "ws";
        String password = "R1xHoHG";

        HttpURLConnection con = null;
//        String url = "http://78.29.24.26:54321/sushi2/hs/PC//GetStatus/" + "z727784" + "/";
//        String url = "http://192.168.127.36:54321/sushi2/hs/PC//GetStatus/" + "z727784" + "/";
        String url = "http://192.168.127.36:54321/sushi2/hs/PC/GetStatusByPhone/" + phone.replace("+", "") + "/";
//        String url = "http://192.168.127.36:54321/sushi2/hs/PC/GetStatusByPhone/79517790515/";
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            // Устанавливаем метод запроса (GET по умолчанию)
            con.setRequestMethod("GET");

            // Базовая аутентификация
            String authString = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            // Получаем код ответа
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                // Читаем ответ из потока ввода
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    // Преобразуем JSON в Map<String, Object> с использованием Gson
                    Gson gson = new Gson();
                    // Используем TypeToken для правильной типизации Map
                    Map<String, Object> responseMap = gson.fromJson(in, new TypeToken<Map<String, Object>>() {}.getType());


                    StringBuilder sb = new StringBuilder();
                    String statusNumber = responseMap.get("Status").toString();

                    if(statusNumber.equals("8.0")){
                        sb.append("Заказ принят и оплачен" + "\n");
                    } else if (statusNumber.equals("0.0")) {
                        sb.append("Заказ не найден" + "\n");
                    }else if (statusNumber.equals("1.0")) {
                        sb.append("Заказ принят" + "\n");
                    }else if (statusNumber.equals("2.0")) {
                        sb.append("Заказ принят и одобрен" + "\n");
                    } else if (statusNumber.equals("3.0")) {
                        sb.append("Заказ изготавливается ..." + "\n");
                    } else if (statusNumber.equals("4.0")) {
                        sb.append("Изготовлен и ожидает доставки" + "\n");
                    } else if (statusNumber.equals("5.0")) {
                        sb.append("Ваш заказ в доставке" + "\n");
                        sb.append("Курьер: " + responseMap.get("Courier").toString() + "\n");
                        sb.append("Телефон: " + responseMap.get("CourierPhone").toString());
                    } else if (statusNumber.equals("6.0") || statusNumber.equals("7.0")) {
                        sb.append("Выполнен" + "\n");
                    }else{
                        sb.append("Заказы не найдены. " + "\n"
                                + "(Информация о заказах становится доступна в день доставки...)");
                    }

                    return sb.toString(); // Возвращаем Map


                } catch (IOException e) {
                    System.err.println("Error reading response: " + e.getMessage());
                    return null; // Обработка ошибки чтения ответа
                }
            } else {
                String errorMessage = "GET request failed. Response code: " + responseCode + ", Message: " + con.getResponseMessage();
                System.err.println(errorMessage);
                return null; // Обработка ошибки ответа сервера (например, 401 Unauthorized)
            }

        } catch (IOException e) {
            System.err.println("IOException during connection: " + e.getMessage());
            return null; // Обработка общей ошибки соединения
        } finally {
            if (con != null) {
                con.disconnect(); // Закрываем соединение в блоке finally
            }
        }
    }
}
