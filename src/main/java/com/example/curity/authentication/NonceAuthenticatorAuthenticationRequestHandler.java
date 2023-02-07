package com.example.curity.authentication;

import com.example.curity.config.NonceAuthenticatorPluginConfig;
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

import java.util.Optional;

public final class NonceAuthenticatorAuthenticationRequestHandler implements AuthenticatorRequestHandler<AuthenticationRequestModel>
{
    private static final Logger _logger = LoggerFactory.getLogger(NonceAuthenticatorAuthenticationRequestHandler.class);

    private final NonceTokenIssuer _nti;
    private final OriginalQueryExtractor _originalQueryExtractor;
    private final ExceptionFactory _exceptionFactory;

    public NonceAuthenticatorAuthenticationRequestHandler(NonceAuthenticatorPluginConfig config,
                                                          ExceptionFactory exceptionFactory)
    {
        _nti = config.getNonceIssuer();
        _originalQueryExtractor = config.getOriginalQueryExtractor();
        _exceptionFactory = exceptionFactory;
    }

    @Override
    public Optional<AuthenticationResult> get(AuthenticationRequestModel authenticationRequestModel, Response response)
    {
        String nonce = authenticationRequestModel.getNonce();
        Optional<TokenAttributes> idTokenAttributes= _nti.introspect(nonce);
        if (idTokenAttributes.isEmpty()) {
            _logger.debug("Nonce not found.");
            throw _exceptionFactory.forbiddenException(ErrorCode.AUTHENTICATION_FAILED, "Unknown nonce");
        }
        MapAttributeValue mav = MapAttributeValue.of(idTokenAttributes.get());
        String subject = mav.get("sub").getValue().toString();
        SubjectAttributes sa = SubjectAttributes.of(
                subject,Attributes.of(
                        Attribute.of("id_token", mav)));

        return Optional.of(
                new AuthenticationResult(
                        AuthenticationAttributes.of(sa,
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
        return new AuthenticationRequestModel(request, _originalQueryExtractor);
    }
}
