package com.example.curity.descriptor;

import com.example.curity.anonymous.JwtValidator;
import com.example.curity.anonymous.NonceAuthenticatorAnonymousRequestHandler;
import com.example.curity.authentication.NonceAuthenticatorAuthenticatorRequestHandler;
import com.example.curity.config.NonceAuthenticatorAuthenticatorPluginConfig;
import se.curity.identityserver.sdk.authentication.AnonymousRequestHandler;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.plugin.ManagedObject;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticatorPluginDescriptor;
import se.curity.identityserver.sdk.web.RequestHandlerSet;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;

public final class NonceAuthenticatorAuthenticatorPluginDescriptor implements AuthenticatorPluginDescriptor<NonceAuthenticatorAuthenticatorPluginConfig>
{
    @Override
    public String getPluginImplementationType()
    {
        return "nonce";
    }

    @Override
    public Class<? extends NonceAuthenticatorAuthenticatorPluginConfig> getConfigurationType()
    {
        return NonceAuthenticatorAuthenticatorPluginConfig.class;
    }

    @Override
    public Map<String, Class<? extends AuthenticatorRequestHandler<?>>> getAuthenticationRequestHandlerTypes()
    {
        return unmodifiableMap(singletonMap("index", NonceAuthenticatorAuthenticatorRequestHandler.class));
    }

    @Override
    public Map<String, Class<? extends AnonymousRequestHandler<?>>> getAnonymousRequestHandlerTypes() {
        return unmodifiableMap(singletonMap("index", NonceAuthenticatorAnonymousRequestHandler.class));

    }

    @Override
    public Optional<? extends ManagedObject<NonceAuthenticatorAuthenticatorPluginConfig>> createManagedObject(NonceAuthenticatorAuthenticatorPluginConfig configuration)
    {
        return Optional.of(new JwtValidator(configuration));
    }

    @Override
    public RequestHandlerSet allowedHandlersForCrossSiteNonSafeRequests()
    {
        return RequestHandlerSet.of(
                NonceAuthenticatorAuthenticatorRequestHandler.class);
    }
}
