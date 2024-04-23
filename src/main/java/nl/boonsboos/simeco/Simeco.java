package nl.boonsboos.simeco;

import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.simulation.ServerTickThread;
import nl.boonsboos.simeco.util.SimecoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SpringBootConfiguration
public class Simeco {

    public static final SimecoConfiguration CONFIG = new SimecoConfiguration();
    public static final ServerTickThread TICK_THREAD = new ServerTickThread();

    public static void main(String[] args) {

        DatabasePool.initialize();
        ClientStore.intialize();
        TICK_THREAD.start();

        SpringApplication.run(Simeco.class);
    }
}
