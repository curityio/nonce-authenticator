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

import se.curity.identityserver.sdk.web.Request;

import java.util.Collection;

public final class AnonymousRequestModel
{
    static final String TOKEN_PARAM = "token";
    private final String _token;

    AnonymousRequestModel(Request request)
    {
        if (request.isGetRequest())
        {
            Collection<String> tokenValues = request.getQueryParameterValues(TOKEN_PARAM);
            _token = tokenValues.iterator().hasNext() ? tokenValues.iterator().next() : "";
        }
        else if (request.isPostRequest())
        {
            _token = request.getFormParameterValueOrError(TOKEN_PARAM);
        }
        else
        {
            throw new RuntimeException("Invalid HTTP request");
        }
    }

    String getToken()
    {
        return _token;
    }
}
