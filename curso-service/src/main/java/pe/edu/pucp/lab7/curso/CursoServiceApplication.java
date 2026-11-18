package pe.edu.pucp.lab7.curso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Con spring-cloud-starter-netflix-eureka-client en el classpath
 * el registro en Eureka es automático (no hace falta @EnableDiscoveryClient).
 */
@SpringBootApplication
public class CursoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CursoServiceApplication.class, args);
    }
}
