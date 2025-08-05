package pro.masterfood.utils;

import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class FileTypeDetector {

    public static String detectFileType(byte[] data) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            byte[] header = new byte[12]; // Увеличиваем размер header, т.к. для HEIC нужно больше байт
            int bytesRead = bis.read(header, 0, 12);

            if (bytesRead < 2) {
                return "unknown"; // Слишком мало данных
            }

            // JPEG
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
                return "jpg";
            }

            // PNG
            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E &&
                    header[3] == (byte) 0x47 && header[4] == (byte) 0x0D && header[5] == (byte) 0x0A &&
                    header[6] == (byte) 0x1A && header[7] == (byte) 0x0A) {
                return "png";
            }

            // GIF
            if (header[0] == (byte) 0x47 && header[1] == (byte) 0x49 && header[2] == (byte) 0x46 &&
                    (header[3] == (byte) 0x38 && (header[4] == (byte) 0x37 || header[4] == (byte) 0x39) &&
                            header[5] == (byte) 0x61)) {
                return "gif";
            }

            // PDF
            if (header[0] == (byte) 0x25 && header[1] == (byte) 0x50 && header[2] == (byte) 0x44 && header[3] == (byte) 0x46) {
                return "pdf";
            }

            // HEIC (проверка по box type 'ftyp' и major brand)
            if (bytesRead >= 12 &&
                    header[4] == (byte) 0x66 && header[5] == (byte) 0x74 && header[6] == (byte) 0x79 &&
                    header[7] == (byte) 0x70) { // ftyp
                //  && header[8] == (byte) 0x6D && header[9] == (byte) 0x69 && header[10] == (byte) 0x66 &&  header[11] == (byte) 0x31) { //mif1
                String majorBrand = new String(header, 8, 4, "UTF-8");
                if (majorBrand.equals("mif1") || majorBrand.equals("msf1") || majorBrand.equals("heic") || majorBrand.equals("heix") || majorBrand.equals("heim") || majorBrand.equals("hevm") || majorBrand.equals("miaf")) {
                    return "heic";
                }
            }
            return "Неизвестный тип фотографии"; // Неизвестный тип
        }
    }
}
