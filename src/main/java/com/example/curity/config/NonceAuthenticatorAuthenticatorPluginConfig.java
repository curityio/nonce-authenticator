package com.example.curity.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultLong;
import se.curity.identityserver.sdk.config.annotation.DefaultString;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.config.annotation.Name;
import se.curity.identityserver.sdk.service.NonceTokenIssuer;
import se.curity.identityserver.sdk.service.UserPreferenceManager;

@SuppressWarnings("InterfaceNeverImplemented")
public interface NonceAuthenticatorAuthenticatorPluginConfig extends Configuration
{
     NonceTokenIssuer getNonceIssuer();
     UserPreferenceManager getUserPreferenceManager();
     @Name("JWKS-Endpoint")
     @Description("Url to the JWKS endpoint. Used to fetch keys to validate the ID Token")
     @DefaultString("")
     String getJKWSEndpoint();
     @Description("Validity time for the nonce (in seconds)")
     @DefaultLong(120)
     long getNonceValidity();
     @Name("Audience")
     @Description("The audience of the ID Token")
     @DefaultString("")
     String getAudience();
    @Name("Issuer")
    @Description("The issuer of the ID Token")
    @DefaultString("")
    String getIssuer();
}
