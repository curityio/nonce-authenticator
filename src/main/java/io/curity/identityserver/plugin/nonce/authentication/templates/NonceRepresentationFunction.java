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

package io.curity.identityserver.plugin.nonce.authentication.templates;

import se.curity.identityserver.sdk.haapi.HaapiContract;
import se.curity.identityserver.sdk.haapi.Message;
import se.curity.identityserver.sdk.haapi.RepresentationFactory;
import se.curity.identityserver.sdk.haapi.RepresentationFunction;
import se.curity.identityserver.sdk.haapi.RepresentationModel;
import se.curity.identityserver.sdk.http.HttpMethod;
import se.curity.identityserver.sdk.http.MediaType;
import se.curity.identityserver.sdk.web.Representation;

import java.net.URI;

public final class NonceRepresentationFunction implements RepresentationFunction {
    private static final Message MSG_TITLE = Message.ofKey("view.title");
    private static final Message MSG_ACTION = Message.ofKey("view.submit");
    private static final Message MSG_TOKEN = Message.ofKey("view.token");

    @Override
    public Representation apply(RepresentationModel representationModel, RepresentationFactory factory) {
        String postbackUrl = representationModel.getString("_authUrl");
        return factory.newAuthenticationStep(step -> {
            step.addFormAction(HaapiContract.Actions.Kinds.LOGIN, URI.create(postbackUrl),
                    HttpMethod.POST, MediaType.X_WWW_FORM_URLENCODED,
                    MSG_TITLE,
                    MSG_ACTION,
                    fields -> {
                        fields.addTextField("login_hint_token", MSG_TOKEN);
                    });

        });

    }
}
