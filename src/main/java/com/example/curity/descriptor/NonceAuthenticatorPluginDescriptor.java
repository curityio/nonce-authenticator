package com.example.curity.descriptor;

import com.example.curity.anonymous.JwtValidator;
import com.example.curity.anonymous.NonceAuthenticatorAnonymousRequestHandler;
import com.example.curity.authentication.NonceAuthenticatorAuthenticationRequestHandler;
import com.example.curity.config.NonceAuthenticatorPluginConfig;
import se.curity.identityserver.sdk.authentication.AnonymousRequestHandler;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.plugin.ManagedObject;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticatorPluginDescriptor;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;

public final class NonceAuthenticatorPluginDescriptor implements AuthenticatorPluginDescriptor<NonceAuthenticatorPluginConfig>
{
    @Override
    public String getPluginImplementationType()
    {
        return "nonce";
    }

    @Override
    public Class<? extends NonceAuthenticatorPluginConfig> getConfigurationType()
    {
        return NonceAuthenticatorPluginConfig.class;
    }

    @Override
    public Map<String, Class<? extends AuthenticatorRequestHandler<?>>> getAuthenticationRequestHandlerTypes()
    {
        return unmodifiableMap(singletonMap("index", NonceAuthenticatorAuthenticationRequestHandler.class));
    }

    @Override
    public Map<String, Class<? extends AnonymousRequestHandler<?>>> getAnonymousRequestHandlerTypes() {
        return unmodifiableMap(singletonMap("index", NonceAuthenticatorAnonymousRequestHandler.class));

    }

    @Override
    public Optional<? extends ManagedObject<NonceAuthenticatorPluginConfig>> createManagedObject(NonceAuthenticatorPluginConfig configuration)
    {
        return Optional.of(new JwtValidator(configuration));
    }
}
