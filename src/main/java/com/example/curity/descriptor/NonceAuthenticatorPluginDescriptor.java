/*
 * Copyright 2025 Curity AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.curity.descriptor;

import com.example.curity.anonymous.JwtValidator;
import com.example.curity.anonymous.NonceAuthenticatorAnonymousRequestHandler;
import com.example.curity.authentication.NonceAuthenticatorAuthenticationRequestHandler;
import com.example.curity.authentication.templates.NonceRepresentationFunction;
import com.example.curity.config.NonceAuthenticatorPluginConfig;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.authentication.AnonymousRequestHandler;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.haapi.RepresentationFunction;
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
    public Map<String, Class<? extends AnonymousRequestHandler<?>>> getAnonymousRequestHandlerTypes()
    {
        return unmodifiableMap(singletonMap("index", NonceAuthenticatorAnonymousRequestHandler.class));
    }

    @Override
    public Optional<? extends ManagedObject<NonceAuthenticatorPluginConfig>> createManagedObject(NonceAuthenticatorPluginConfig configuration)
    {
        return Optional.of(new JwtValidator(configuration));
    }

    @Override
    public @Nullable Map<String, Class<? extends RepresentationFunction>> getRepresentationFunctions()
    {
        return unmodifiableMap(singletonMap("authenticate/get", NonceRepresentationFunction.class));
    }
}
