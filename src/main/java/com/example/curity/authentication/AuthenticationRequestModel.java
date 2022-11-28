package com.example.curity.authentication;

import se.curity.identityserver.sdk.service.UserPreferenceManager;
import se.curity.identityserver.sdk.web.Request;

class AuthenticationRequestModel {
    private final String _username;

    AuthenticationRequestModel(Request request, UserPreferenceManager userPreferenceManager) {
        if (request.isGetRequest()) {
            _username = userPreferenceManager.getUsername();
        } else {
            throw new RuntimeException("HTTP method not supported");
        }

    }

    public String getUsername() {
        return _username;
    }
}