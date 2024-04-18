package nl.boonsboos.simeco.controllers.responses.error;

import java.time.LocalDateTime;

public final class UserUnauthorizedResponse {

    private final LocalDateTime timestamp;
    private final int status = 401;
    private final String error = "You do not have permission to view this resource.";
    private final String path;

    public UserUnauthorizedResponse(String path) {
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    public String getMessage() {
        return error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }
}