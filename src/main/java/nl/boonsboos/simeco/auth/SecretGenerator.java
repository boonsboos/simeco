package nl.boonsboos.simeco.auth;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public class SecretGenerator {

    private static final String allowedCharacters = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";

    /**
     * Generates a random secret.
     * @return a random secret
     */
    public static String generateSecret() {
        RandomGenerator rng = RandomGeneratorFactory.of("SecureRandom").create();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 192; i++) {
            sb.append(
                allowedCharacters.charAt(
                    (int) (Math.abs(rng.nextLong()) % allowedCharacters.length())
                )
            );
        }

        return sb.toString();
    }
}