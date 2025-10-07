package pro.masterfood.service;

public interface RedirectService {
    boolean validateInitData(String initData, String botToken);

    String generateToken(String initData);
}
