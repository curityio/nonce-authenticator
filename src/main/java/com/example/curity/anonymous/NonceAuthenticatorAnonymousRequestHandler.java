package com.example.curity.anonymous;

import com.example.curity.config.NonceAuthenticatorAuthenticatorPluginConfig;
import org.jose4j.jwt.MalformedClaimException;
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

@Produces(Produces.ContentType.JSON)
public class NonceAuthenticatorAnonymousRequestHandler implements AnonymousRequestHandler<AnonymousRequestModel> {

    private static final Logger _logger = LoggerFactory.getLogger(NonceAuthenticatorAnonymousRequestHandler.class);
    private final NonceAuthenticatorAuthenticatorPluginConfig _config;
    private final NonceTokenIssuer _nti;
    private JwtValidator _validator;

    public NonceAuthenticatorAnonymousRequestHandler(JwtValidator validator,
                                                     NonceAuthenticatorAuthenticatorPluginConfig config,
                                                     ExceptionFactory exceptionFactory,
                                                     AuthenticatorInformationProvider authInfoProvider) {
        _config = config;
        _nti = config.getNonceIssuer();
        _validator = validator;
    }

    @Override
    public AnonymousRequestModel preProcess(Request request, Response response) {
        return new AnonymousRequestModel(request);
    }

    @Override
    public Void get(AnonymousRequestModel anonymousRequestModel, Response response) {
        addNonce(anonymousRequestModel, response);
        return null;
    }

    @Override
    public Void post(AnonymousRequestModel anonymousRequestModel, Response response) {
        addNonce(anonymousRequestModel, response);
        return null;
    }

    private String createNonce(AnonymousRequestModel anonymousRequestModel) {
        String token = anonymousRequestModel.getToken();
        String nonce = null;
        try {
            String subject = getSubject(token);
            Instant now = Instant.now();
            Instant expires = now.plus(Duration.ofSeconds(_config.getNonceValidity()));
            TokenAttributes attributes = new TokenAttributes(expires, now, Attributes.of("subject", subject));
            nonce = _nti.issue(attributes);

        } catch (MalformedClaimException | InvalidJwtException e) {
            _logger.warn("Incoming JWT is not valid. {}", e.getMessage());
            e.printStackTrace();
        } catch (TokenIssuerException e) {
            _logger.warn("Unable to issue token. {}", e.getMessage());
            e.printStackTrace();
        }
        return nonce;
    }

    private String getSubject(String token) throws InvalidJwtException, MalformedClaimException {
        JwtContext context = _validator.validate(token);
        return context.getJwtClaims().getSubject();
    }


    private void addNonce(AnonymousRequestModel anonymousRequestModel, Response response) {
        String nonce = createNonce(anonymousRequestModel);
        if (nonce != null) {
            response.putViewData("nonce", nonce, HttpStatus.OK);
        } else {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
        }
    }

}
