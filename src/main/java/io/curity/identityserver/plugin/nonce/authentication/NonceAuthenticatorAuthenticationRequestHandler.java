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

package io.curity.identityserver.plugin.nonce.authentication;

import io.curity.identityserver.plugin.nonce.config.NonceAuthenticatorPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.attribute.MapAttributeValue;
import se.curity.identityserver.sdk.attribute.SubjectAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.data.tokens.TokenAttributes;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.NonceTokenIssuer;
import se.curity.identityserver.sdk.service.OriginalQueryExtractor;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;
import se.curity.identityserver.sdk.web.ResponseModel;

import java.util.Optional;

import static java.util.Collections.emptyMap;

public final class NonceAuthenticatorAuthenticationRequestHandler implements AuthenticatorRequestHandler<AuthenticationRequestModel>
{
    private static final Logger _logger = LoggerFactory.getLogger(NonceAuthenticatorAuthenticationRequestHandler.class);

    private final NonceTokenIssuer _nti;
    private final OriginalQueryExtractor _originalQueryExtractor;
    private final ExceptionFactory _exceptionFactory;

    public NonceAuthenticatorAuthenticationRequestHandler(NonceAuthenticatorPluginConfig config, ExceptionFactory exceptionFactory)
    {
        _nti = config.getNonceIssuer();
        _originalQueryExtractor = config.getOriginalQueryExtractor();
        _exceptionFactory = exceptionFactory;
    }

    @Override
    public Optional<AuthenticationResult> get(AuthenticationRequestModel authenticationRequestModel, Response response)
    {
        _logger.debug("Received GET authentication request");
        return handleAuthentication(authenticationRequestModel);
    }

    @Override
    public Optional<AuthenticationResult> post(AuthenticationRequestModel authenticationRequestModel, Response response)
    {
        _logger.debug("Received POST authentication request");
        return handleAuthentication(authenticationRequestModel);
    }

    private Optional<AuthenticationResult> handleAuthentication(AuthenticationRequestModel authenticationRequestModel)
    {
        String nonce = authenticationRequestModel.getNonce();
        if (nonce == null)
        {
            return Optional.empty();
        }

        TokenAttributes idTokenAttributes = _nti.introspect(nonce).orElseThrow(() -> {
            _logger.debug("The provided nonce is invalid or has expired");
            return _exceptionFactory.forbiddenException(ErrorCode.AUTHENTICATION_FAILED, "unknown.nonce");
        });

        MapAttributeValue mav = MapAttributeValue.of(idTokenAttributes);
        String subject = mav.get("sub").getValue().toString();
        SubjectAttributes subjectAttributes = SubjectAttributes.of(subject, Attributes.of(Attribute.of("id_token", mav)));

        return Optional.of(new AuthenticationResult(AuthenticationAttributes.of(subjectAttributes, ContextAttributes.empty())));
    }

    @Override
    public AuthenticationRequestModel preProcess(Request request, Response response)
    {
        response.setResponseModel(ResponseModel.templateResponseModel(emptyMap(), "authenticate/get"), Response.ResponseModelScope.ANY);
        return new AuthenticationRequestModel(request, _originalQueryExtractor);
    }
}

