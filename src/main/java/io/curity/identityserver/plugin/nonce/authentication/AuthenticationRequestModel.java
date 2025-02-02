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

import se.curity.identityserver.sdk.service.OriginalQueryExtractor;
import se.curity.identityserver.sdk.web.Request;

public final class AuthenticationRequestModel
{
    private static final String LOGIN_HINT_TOKEN = "login_hint_token";
    private String _nonce;

    AuthenticationRequestModel(Request request, OriginalQueryExtractor originalQueryExtractor)
    {

        if (request.isGetRequest())
        {
            _nonce = originalQueryExtractor.getQueryParameterValue(LOGIN_HINT_TOKEN);
        }
        if (request.isPostRequest())
        {
            _nonce = request.getFormParameterValueOrError(LOGIN_HINT_TOKEN);
        }
    }

    public String getNonce()
    {
        return _nonce;
    }
}
