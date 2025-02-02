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

package io.curity.identityserver.plugin.nonce.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultLong;
import se.curity.identityserver.sdk.config.annotation.DefaultService;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.config.annotation.Name;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.NonceTokenIssuer;
import se.curity.identityserver.sdk.service.OriginalQueryExtractor;

@SuppressWarnings("InterfaceNeverImplemented")
public interface NonceAuthenticatorPluginConfig extends Configuration
{
    @Name("JWKS-Endpoint")
    @Description("Url to the JWKS endpoint. Used to fetch keys to validate the ID Token")
    String getJKWSEndpoint();

    @Description("Validity time for the nonce (in seconds)")
    @DefaultLong(120)
    long getNonceValidity();

    @Name("Audience")
    @Description("The audience of the ID Token")
    String getAudience();

    @Name("Issuer")
    @Description("The issuer of the ID Token")
    String getIssuer();

    NonceTokenIssuer getNonceIssuer();

    OriginalQueryExtractor getOriginalQueryExtractor();

    @DefaultService
    HttpClient getHttpClient();
}
