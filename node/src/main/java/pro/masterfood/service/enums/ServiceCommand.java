package pro.masterfood.service.enums;

public enum ServiceCommand {
    EXIT("/quit_accept"),
    QUIT("/quit"),
    HELP("/help"),
    REGISTRATION("/registration"),
    GET_USER_INFO("/present"),
    STATUS("/status"),
    REPORT("/report"),
    CANCEL("/cancel"),
    START("/start");
    private final String value;
    ServiceCommand(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    public static ServiceCommand fromValue(String v){
        for (ServiceCommand c : ServiceCommand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
