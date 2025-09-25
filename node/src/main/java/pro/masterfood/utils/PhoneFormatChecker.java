package pro.masterfood.utils;

import org.springframework.stereotype.Component;

@Component
public class PhoneFormatChecker {
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "notIsPhone";
        }

        // 1. Удаляем все нецифровые символы
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");

        // 2. Проверяем длину и первую цифру
        if (digitsOnly.length() == 11) {
            if (digitsOnly.startsWith("8")) {
                digitsOnly = "7" + digitsOnly.substring(1); // Заменяем 8 на 7
            } else if (!digitsOnly.startsWith("7")) {
                return "phoneIsNotCorrect"; // Если 11 цифр и не начинается с 7, значит неверный формат
            }
        } else if (digitsOnly.length() == 10) {
            digitsOnly = "7" + digitsOnly; // Добавляем 7 в начале, если 10 цифр
        } else {
            return "phoneIsNotCorrect"; // Некорректная длина
        }

        if (digitsOnly.length() != 11) {
            return "phoneIsNotCorrect"; //после форматирования, если длина не 11, значит ошибка
        }

        return digitsOnly;
    }
}
