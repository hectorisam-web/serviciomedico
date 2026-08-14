package mintur.serviciomedico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
// Quitamos el ComponentScan anterior para evitar redundancia
public class ServiciomedicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiciomedicoApplication.class, args);
    }
    

  }