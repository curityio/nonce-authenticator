/*
 *  Copyright 2025 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.curity.identityserver.plugin.nonce.anonymous;

import io.curity.identityserver.plugin.nonce.config.NonceAuthenticatorPluginConfig;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.authentication.AnonymousRequestHandler;
import se.curity.identityserver.sdk.data.tokens.TokenAttributes;
import se.curity.identityserver.sdk.data.tokens.TokenIssuerException;
import se.curity.identityserver.sdk.http.HttpStatus;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.NonceTokenIssuer;
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider;
import se.curity.identityserver.sdk.web.Produces;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Produces(Produces.ContentType.JSON)
public final class NonceAuthenticatorAnonymousRequestHandler implements AnonymousRequestHandler<AnonymousRequestModel>
{

    private static final Logger _logger = LoggerFactory.getLogger(NonceAuthenticatorAnonymousRequestHandler.class);
    private final NonceAuthenticatorPluginConfig _config;
    private final NonceTokenIssuer _nti;
    private JwtValidator _validator;

    public NonceAuthenticatorAnonymousRequestHandler(JwtValidator validator,
                                                     NonceAuthenticatorPluginConfig config,
                                                     ExceptionFactory exceptionFactory,
                                                     AuthenticatorInformationProvider authInfoProvider)
    {
        _config = config;
        _nti = config.getNonceIssuer();
        _validator = validator;
    }

    @Override
    public AnonymousRequestModel preProcess(Request request, Response response)
    {
        return new AnonymousRequestModel(request);
    }

    @Override
    public Void get(AnonymousRequestModel anonymousRequestModel, Response response)
    {
        addNonce(anonymousRequestModel, response);
        return null;
    }

    @Override
    public Void post(AnonymousRequestModel anonymousRequestModel, Response response)
    {
        addNonce(anonymousRequestModel, response);
        return null;
    }

    private String createNonce(AnonymousRequestModel anonymousRequestModel)
    {
        String token = anonymousRequestModel.getToken();
        String nonce = null;
        try
        {
            JwtContext context = _validator.validate(token);
            Map claimsMap = context.getJwtClaims().getClaimsMap();
            Instant now = Instant.now();
            Instant expires = now.plus(Duration.ofSeconds(_config.getNonceValidity()));
            TokenAttributes attributes = new TokenAttributes(expires, now, Attributes.fromMap(claimsMap));
            nonce = _nti.issue(attributes);
        }
        catch (InvalidJwtException e)
        {
            _logger.warn("Incoming JWT is not valid. {}", e.getMessage());
            e.printStackTrace();
        }
        catch (TokenIssuerException e)
        {
            _logger.warn("Unable to issue token. {}", e.getMessage());
            e.printStackTrace();
        }
        return nonce;
    }

    private void addNonce(AnonymousRequestModel anonymousRequestModel, Response response)
    {
        String nonce = createNonce(anonymousRequestModel);
        if (nonce != null)
        {
            response.putViewData("nonce", nonce, HttpStatus.OK);
        }
        else
        {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
        }
    }
}
