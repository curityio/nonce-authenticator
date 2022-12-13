# Nonce Authenticator Plugin Example

[![Quality](https://img.shields.io/badge/quality-test-yellow)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

## SSO Navigation

In some setups, environmental limitations prevent OpenID Connect Single Sign On (SSO) from working.\
In these cases, the user will first authenticate in a source application.\
When navigating to a target application, re-authentication will be required, with a suboptimal user experience.\
The nonce authenticator pattern provides a solution to the double login problem.

## Mobile Use Case

A common use case for this pattern is when navigating from a mobile app to a web app.\
Different cookie jars may be used for SSO cookies, resulting in a double login by default.

## Nonce Authenticator

This plugin uses the [Nonce Token Issuer](https://curity.io/docs/idsvr-java-plugin-sdk/latest/se/curity/identityserver/sdk/service/NonceTokenIssuer.html) from the Java SDK.\
This enables the source application to issue a nonce and use it to bootstrap SSO in the target application.\
The target application uses the nonce, with is validated in a silent authentication flow.

## Security Flow

The source OAuth client must first extend its audience to include the nonce issuing endpoint.\
The endpoint format is `[BASE URL]` + `[Anonymous Authentication Endpoint]` + `[Authenticator Name]`.
The client can post its ID token to this endpoint, to create a nonce:

```
curl -X POST 'https://idsvr.example.com/authentication/anonymous/nonce1?token=eyJraWQi...' \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'token=eyJraWQi...'
```

Next, the target OAuth client can be navigated to, using a nonce as a URL query parameter.\
This value can only be used once, is very short lived, and is used immediately by the target application:

```text
https://www.example.com?nonce=OFiicYQJYY2phWnD5nFMflid5Du82ycW
```

The target application then performs its own OpenID Connect redirect, which is guaranteed to use SSO:

```text
http://idsvr.example.com/oauth/v2/oauth-authorize
    ?client_id=web-client
    &redirect_uri=http%3A%2F%2Fwww.example.com%2F
    &response_type=code
    &code_challenge=l9QIPE4TFgW2y7STZDSWQ4Y4CQpO8W6VtELopzYHdNg
    &code_challenge_method=S256
    &state=NlAoISfdL1DxPdNGFBljlVuB1GDjgGARmqDcxtHhV8iKNYu6ECS2KOavDHpI3eLN
    &scope=openid%20profile
    &acr_values=urn:se:curity:authentication:nonce:nonce1
    &login_hint=OFiicYQJYY2phWnD5nFMflid5Du82ycW
    &prompt=login
```

The following additional OpenID Connect parameters are used in this redirect.\
It can be issued on a hidden iframe so that the end user experience is seamless.

| Parameter | Description |
| --------- | ----------- |
| acr_values | Forces the nonce authenticator to be used
| login_hint | Supplies the nonce for validation |
| prompt | Prevents nonce authentication being bypassed via SSO cookies |

The target OAuth client then authenticates silently, with no user prompts.\
The target OAuth client then receives its own set of tokens, with its own scopes and claims.

## Building the plugin

The plugin is built by issuing this command:

```bash
mvn package
```

This will produce JAR files in the `target` directory:

```text
nonce-authneticator-*.jar
jose4j-*.jar
```

Deploy these JAR files to your instances of the Curity Identity Server, in a plugins subfolder.\
The plugin group `authenticators.nonce` can be replaced with any other arbitrary name of your choice:

```text
$IDSVR_HOME/usr/share/plugins/authenticators.nonce/*.jar
```

## Configuration

The plugin requires the following settings:

| Property | Example Value |
| -------- | ------------- |
| Audience | https://idsvr.example.com/authentication/anonymous/nonce1
| Issuer | https://idsvr.example.com/oauth/v2/oauth-anonymous
| JWKS Endpoint | https://idsvr.example.com/oauth/v2/oauth-anonymous/jwks
| Nonce Validity Seconds | 120 |

## Website Documentation

See the following resources on the Curity website.\
The code example provides an end-to-end solution that can be run on a standalone computer:

- [The Nonce Authenticator Pattern](https://curity.io/resources/learn/nonce-authenticator-pattern)
- [Mobile Web SSO Code Example](https://curity.io/resources/learn/mobile-web-integration-example/)

## More Information

Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.
