package tim.field.application.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TokenUtils {

    public static String decodeToken(String token) {
        try {
            return URLDecoder.decode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao decodificar o token", e);
        }
    }
}
