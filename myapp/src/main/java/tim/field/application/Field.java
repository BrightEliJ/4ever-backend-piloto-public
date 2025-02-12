package tim.field.application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Field {
    public static void main(String[] args) {
        SpringApplication.run(Field.class, args);

        LocalDateTime now = LocalDateTime.now();

        // Data e hora de início da Aplicação
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        System.out.println("Início Aplicação: " + formattedDateTime);
    }
}