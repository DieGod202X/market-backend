package com.tecnm.merida.market_backend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthenticationRequest {

    @Schema(description = "Client ID or email (optional if email or clientId is provided)", example = "4546221")
    private String username;

    @Schema(description = "Client email (optional if username is provided)", example = "lionel@messi.com")
    private String email;

    @Schema(description = "Client ID (optional if username is provided)", example = "4546221")
    private String clientId;

    @Schema(description = "Plain text password", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String resolveIdentifier() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        if (email != null && !email.isBlank()) {
            return email;
        }
        if (clientId != null && !clientId.isBlank()) {
            return clientId;
        }
        return null;
    }
}
