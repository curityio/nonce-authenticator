package com.example.curity.authentication;

import com.example.curity.config.NonceAuthenticatorAuthenticatorPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.attribute.SubjectAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.data.tokens.TokenAttributes;
import se.curity.identityserver.sdk.errors.ErrorCode;
import se.curity.identityserver.sdk.service.ExceptionFactory;
import se.curity.identityserver.sdk.service.NonceTokenIssuer;
import se.curity.identityserver.sdk.service.UserPreferenceManager;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.util.Optional;

public final class NonceAuthenticatorAuthenticatorRequestHandler implements AuthenticatorRequestHandler<AuthenticationRequestModel>
{
    private static final Logger _logger = LoggerFactory.getLogger(NonceAuthenticatorAuthenticatorRequestHandler.class);

    private final NonceTokenIssuer _nti;
    private final UserPreferenceManager _upm;
    private final ExceptionFactory _exceptionFactory;

    public NonceAuthenticatorAuthenticatorRequestHandler(NonceAuthenticatorAuthenticatorPluginConfig config,
                                                         ExceptionFactory exceptionFactory)
    {
        _nti = config.getNonceIssuer();
        _upm = config.getUserPreferenceManager();
        _exceptionFactory = exceptionFactory;
    }

    @Override
    public Optional<AuthenticationResult> get(AuthenticationRequestModel authenticationRequestModel, Response response)
    {
        String username = authenticationRequestModel.getUsername();
        Optional<TokenAttributes> subjectAttributes= _nti.introspect(username);
        if (subjectAttributes.isEmpty()) {
            _logger.debug("Nonce not found.");
            throw _exceptionFactory.forbiddenException(ErrorCode.AUTHENTICATION_FAILED, "Unknown nonce");
        }

        @Nullable String subject = subjectAttributes.get().get("subject").getValue().toString();
        return Optional.of(
                new AuthenticationResult(
                        AuthenticationAttributes.of(
                                SubjectAttributes.of(subject, Attributes.empty()),
                                ContextAttributes.empty()
                        )
                )
        );
    }


    @Override
    public Optional<AuthenticationResult> post(AuthenticationRequestModel authenticationRequestModel, Response response)
    {
        throw _exceptionFactory.methodNotAllowed();
    }

    @Override
    public AuthenticationRequestModel preProcess(Request request, Response response)
    {
        return new AuthenticationRequestModel(request, _upm);
    }
}
