package com.example.curity.authentication;

import se.curity.identityserver.sdk.service.OriginalQueryExtractor;
import se.curity.identityserver.sdk.web.Request;

class AuthenticationRequestModel {
    private final String _nonce;

    AuthenticationRequestModel(Request request, OriginalQueryExtractor originalQueryExtractor) {

        if (request.isGetRequest()) {
            _nonce = originalQueryExtractor.getQueryParameterValue("login_hint_token");
        } else {
            throw new RuntimeException("HTTP method not supported");
        }
    }

    public String getNonce() {
        return _nonce;
    }
}