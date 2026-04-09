package kg.alatoo.smarthousebackendsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SmartHouseBackendSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHouseBackendSystemApplication.class, args);
    }

}
