package pro.masterfood.dto;

import java.util.List;

public class ProcessingResponseToOneS_Rest {
    private String result;  // Соответствует messageFromNode (описание обработки)
    private List<String> userList;  // Соответствует processedUsers (список ID пользователей)

    // Конструкторы, геттеры, сеттеры
    public ProcessingResponseToOneS_Rest(String result, List<String> userList) {
        this.result = result;
        this.userList = userList;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
