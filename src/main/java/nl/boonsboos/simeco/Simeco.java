package nl.boonsboos.simeco;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.util.SimecoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SpringBootConfiguration
public class Simeco {

    public static SimecoConfiguration CONFIG = new SimecoConfiguration();

    public static void main(String[] args) {

        DatabasePool.initialize();

        SpringApplication.run(Simeco.class);
    }
}
