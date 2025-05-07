package pro.masterfood.utils;

import org.springframework.stereotype.Component;

@Component
public class CommandPatternChecker {
    public boolean isNotACommand (String string){
        if(string.equals("/present")
                || string.equals("/status")
                || string.equals("/report")
                || string.equals("/cancel")
                || string.equals("/quit")
                || string.equals("/registration")
        ){
            return false;
        }else{
            return true;
        }
    }
}
