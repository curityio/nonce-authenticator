# Nonce Authenticator Plugin Example

[![Quality](https://img.shields.io/badge/quality-test-yellow)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

Mobile and web apps should be implemented as different clients, with different client IDs.
These operate very differently, since mobile apps use opaque tokens whereas web apps use secure cookies.
The challenge is to enable a credential for the web view, and to do so securely.

It is not possible to start the web view with SSO cookies to allow seamless integration. Instead, the user will have to authenticate in the web view as well. This behavior is not intuitive nor appreciated from a UX perspective.

## Description ##
The Nonce Authenticator allows for the mobile app the get a temporary nonce which the app uses when opening the web view. The web app would use that nonce to authenticate the user without any interaction. 

The mobile app would POST its ID Token to the anonymous endpoint of the Nonce Authenticator. The endpoint is constructed from `[BASE URL]` + `[Anonymous Authentication Endpoint]` + `[Authenticator Name]`. To try it, you can use cURL
```
curl -X POST 'https://idsv.example.com/authentication/anonymous/nonce?token=' -H 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'token=eyJraWQi...'
```

The web view is opened with the nonce appended to the URL
```
https://portal.example.com?nonce=abcd1234
```

The web app would start an authentication flow with the `login_hint` parameter set to the nonce. If the web app is set up with only the Nonce Authenticator or an ACR value is set for the Nonce Authenticator, the end-user doesn't have to do anything and will experience SSO. An SSO cookie will also be set allowing the client to re-authenticate without the need for a new nonce.

***NOTE*** Any authenticator can be used to authenticate the mobile app.

## Deploying the Plugin

Follow the below steps to run this plugin in deploy the plugin into Curity Identity Server. The plugin has to be installed on all instances.

### Building the plugin

The plugin built by issuing the command

```
mvn package
```

This will produce a JAR file in the `target` directory, which can be installed.

### Deploy the JAR Files

Gather the following files from the `target` folder:

```text
nonce-authneticator-*.jar
jose4j-*.jar
```

Deploy JAR files to the instances of the Curity Identity Server, in a plugins subfolder:

```text
$IDSVR_HOME/usr/share/plugins/authenticators.nonce/*.jar
```
The plugin group `authenticators.nonce` can be replaced with any other arbitrary name of your choice.

### Configuration

The settings, except for the Nonce Validity, are to validate the incoming token. Below are some example values

```text
Audience: mobileapp
Issuer: https://idsvr.example.com/oauth/v2/oauth-anonymous
JWKS Endpoint: https://idsvr.example.com/oauth/v2/oauth-anonymous/jwks
Nonce Validity: 10
```

## Security Considerations

The security involves using a proven ID token from the mobile app to bootstrap tokens for the web view application.
A nonce is created by the mobile app. Since this can only be used once and is short lived, it is safe to pass it in a query string to the web view.

## Cookie Usage

The nonce authenticator ensures that Single Sign On is achieved when a web view is invoked from a mobile app:

- The first time a web view is invoked, there will be a redirect in the web view
- The next time a web view is invoked, it will use a saved cookie, and there is no need for a redirect

For this to work, you need to invoke web views in the system browser: a Chrome Custom Tab on Android, or a Safari View Controller on iOS. 
Other web views will run a private browser session, requiring a redirect whenever the web view is invoked.

## Conclusion
With this technique, you can let a mobile app transfer a session to a web view. The end-user will experience SSO moving between the native app and web apps.

More Information
~~~~~~~~~~~~~~~~

Please visit [curity.io](https://curity.io) for more information about the Curity Identity Server.

[curity.io/plugins:](https://curity.io/docs/idsvr/latest/developer-guide/plugins/index.html#plugin-installation)