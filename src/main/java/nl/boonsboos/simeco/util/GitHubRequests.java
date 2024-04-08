package nl.boonsboos.simeco.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.boonsboos.simeco.Simeco;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class GitHubRequests {

    private static Logger LOG = Logger.getLogger(GitHubRequests.class.getSimpleName());

    public static class GitHubUserName {

        private final String username;

        public GitHubUserName(String authCode) {

            final HttpRequest getTokenRequest;
            getTokenRequest = HttpRequest.newBuilder()
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(
                    "https://github.com/login/oauth/access_token?"+
                    "client_id=" + Simeco.CONFIG.getGithubID() +
                    "&client_secret=" + Simeco.CONFIG.getGithubToken() +
                    "&code=" + authCode
                ))
                .setHeader("Accept", "application/vnd.github+json")
                .setHeader("User-Agent", "boonsboos-genderstockmarket")
                .setHeader("X-Github-API-Version", "2022-11-28")
                .build();

            HttpRequest.Builder getUsernameRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user"))
                .setHeader("Accept", "application/vnd.github+json")
                .setHeader("User-Agent", "boonsboos-genderstockmarket")
                .setHeader("X-Github-API-Version", "2022-11-28");
            // don't build because authorization header needs to be set in the try/catch

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpResponse<String> apiTokenResponse = client.send(getTokenRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode apiTokenRoot = new ObjectMapper().readTree(apiTokenResponse.body());

                String apiToken = apiTokenRoot.get("access_token").asText();

                HttpRequest getUsernameRequest = getUsernameRequestBuilder
                        .setHeader("Authorization", "Bearer "+apiToken)
                        .build();

                HttpResponse<String> userNameResponse = client.send(getUsernameRequest, HttpResponse.BodyHandlers.ofString());
                JsonNode usernameRoot = new ObjectMapper().readTree(userNameResponse.body());

                this.username = usernameRoot.get("login").asText();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public String getUsername() {
            return username;
        }

    }
}
