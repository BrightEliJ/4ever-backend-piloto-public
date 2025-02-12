package tim.field.application.util;

import java.util.TimeZone;

public class TimeZoneCheck {
    public static void main(String[] args) {
        TimeZone timeZone = TimeZone.getDefault();
        System.out.println("Fuso horário da aplicação: " + timeZone.getID());
        
    }
}