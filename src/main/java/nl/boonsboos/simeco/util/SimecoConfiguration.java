package nl.boonsboos.simeco.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class SimecoConfiguration {

    private static final Logger LOG = Logger.getLogger(SimecoConfiguration.class.getSimpleName());

    private final String databaseURL;
    private final String databaseName;
    private final String databaseUser;
    private final String databasePassword;
    private final String githubID;
    private final String githubToken;

    public SimecoConfiguration() {
        String fileContent = "";

        try {
            fileContent = Files.readString(Path.of("./config.json"));
        } catch (IOException e) {
            LOG.severe("Failed to load configuration file");
            throw new RuntimeException(e.getCause());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(fileContent);
        }catch (JsonProcessingException e) {
            LOG.severe("Failed to read json from configuration file");
            throw new RuntimeException(e.getCause());
        }

        this.databaseURL      = root.get("databaseURL").asText();
        this.databaseName     = root.get("databaseName").asText();
        this.databaseUser     = root.get("databaseUser").asText();
        this.databasePassword = root.get("databasePassword").asText();
        this.githubID         = root.get("githubID").asText();
        this.githubToken      = root.get("githubToken").asText();
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getGithubID() {
        return githubID;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }
}
