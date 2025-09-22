package pro.masterfood.utils;

import org.springframework.stereotype.Component;

@Component
public class PhoneFormatChecker {
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null; // Или выбросить исключение IllegalArgumentException, если пустая строка недопустима
        }

        // 1. Удаляем все нецифровые символы
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");

        // 2. Проверяем длину и первую цифру
        if (digitsOnly.length() == 11) {
            if (digitsOnly.startsWith("8")) {
                digitsOnly = "7" + digitsOnly.substring(1); // Заменяем 8 на 7
            } else if (!digitsOnly.startsWith("7")) {
                return null;  //Не начинается с 7, некорректный формат
            }
        } else if (digitsOnly.length() == 10) {
            digitsOnly = "7" + digitsOnly; // Добавляем 7 в начале, если 10 цифр
        } else {
            return null; // Некорректная длина
        }


        if (digitsOnly.length() != 11) {
            return null; //после форматирования, если длина не 11, значит ошибка
        }

        return digitsOnly;
    }
}
