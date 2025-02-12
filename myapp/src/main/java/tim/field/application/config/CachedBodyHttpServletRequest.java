package tim.field.application.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(CachedBodyHttpServletRequest.class);
    private final String body;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        body = stringBuilder.toString().trim(); // Trim para remover newline final
        // Log the body for debugging purposes
        logger.debug("Request body cached: {}", body);
    }

    public String getBody() {
        return body;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(body));
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }
}