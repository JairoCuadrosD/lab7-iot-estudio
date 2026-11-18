package pe.edu.pucp.lab7.avance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @EnableFeignClients hace que Spring busque interfaces con @FeignClient
 * y les genere una implementación (un proxy que hace las llamadas HTTP).
 */
@SpringBootApplication
@EnableFeignClients
public class AvanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AvanceServiceApplication.class, args);
    }
}
