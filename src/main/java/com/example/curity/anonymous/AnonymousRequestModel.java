package com.example.curity.anonymous;

import se.curity.identityserver.sdk.web.Request;

import java.util.Collection;

class AnonymousRequestModel
{
	static final String TOKEN_PARAM = "token";
	private final String _token;

	AnonymousRequestModel(Request request)
	{
		if (request.isGetRequest()) {
			Collection<String> tokenValues = request.getQueryParameterValues(TOKEN_PARAM);
			_token = tokenValues.iterator().hasNext() ? tokenValues.iterator().next() : "";
		} else if (request.isPostRequest()) {
			_token = request.getFormParameterValueOrError(TOKEN_PARAM);
		} else {
			throw new RuntimeException("Invalid HTTP request");
		}
	}
	String getToken() {
		return _token;
	}
}