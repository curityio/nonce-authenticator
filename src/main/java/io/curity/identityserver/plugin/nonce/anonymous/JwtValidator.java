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
import org.jose4j.http.SimpleGet;
import org.jose4j.http.SimpleResponse;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.plugin.ManagedObject;
import se.curity.identityserver.sdk.service.HttpClient;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * JwtValidator is a ManagedObject to be able to be the validation keys stored, avoiding downloading them for every
 * request. Keys will be updated if a key is not found
 */
public final class JwtValidator extends ManagedObject<NonceAuthenticatorPluginConfig>
{
    private final JwtConsumer _consumer;

    public JwtValidator(NonceAuthenticatorPluginConfig config)
    {
        super(config);
        var httpsJwks = new HttpsJwks(config.getJKWSEndpoint());
        httpsJwks.setSimpleHttpGet(new CustomSimpleGet(config.getHttpClient()));

        _consumer = new JwtConsumerBuilder()
                .setRequireSubject()
                .setExpectedAudience(config.getAudience())
                .setVerificationKeyResolver(new HttpsJwksVerificationKeyResolver(httpsJwks))
                .setExpectedIssuer(config.getIssuer())
                .build();
    }

    public JwtContext validate(String token) throws InvalidJwtException
    {
        return _consumer.process(token);
    }

    private static class CustomSimpleGet implements SimpleGet
    {
        private final HttpClient _httpClient;

        public CustomSimpleGet(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        @Override
        public SimpleResponse get(String url)
        {
            var response = _httpClient.request(URI.create(url)).get().response();
            var body = response.body(HttpResponse.asString());
            var headers = response.headers().map();

            return new SimpleResponse()
            {
                @Override public int getStatusCode() { return response.statusCode(); }
                @Override public String getStatusMessage() { return null; }
                @Override public List<String> getHeaderValues(String name) { return headers.getOrDefault(name, Collections.emptyList()); }
                @Override public String getBody() { return body; }
                @Override public Collection<String> getHeaderNames() { return headers.keySet(); }
            };
        }
    }
}
