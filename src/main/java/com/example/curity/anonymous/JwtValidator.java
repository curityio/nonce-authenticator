package com.example.curity.anonymous;

import com.example.curity.config.NonceAuthenticatorPluginConfig;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import se.curity.identityserver.sdk.plugin.ManagedObject;

/**
 * JwtValidator is a ManagedObject to be able to be the validation keys stored, avoiding to download them for every
 * request. Keys will be updated if a key is not found
 */
public class JwtValidator extends ManagedObject<NonceAuthenticatorPluginConfig> {
    NonceAuthenticatorPluginConfig _config;
    JwtConsumer _consumer;

    public JwtValidator(NonceAuthenticatorPluginConfig configuration) {
        super(configuration);
        _config = configuration;
        HttpsJwks httpsJwks = new HttpsJwks(_config.getJKWSEndpoint());
        HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJwks);
        _consumer = new JwtConsumerBuilder()
                .setRequireSubject().setExpectedAudience(_config.getAudience())
                .setVerificationKeyResolver(httpsJwksKeyResolver)
                .setExpectedIssuer(_config.getIssuer())
                .build();
    }

    public JwtContext validate(String token) throws InvalidJwtException {
        return _consumer.process(token);
    }
}
