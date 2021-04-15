package com.hometurf.testandroidteam;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hometurf.android.interfaces.HomeTurfBaseAuth0Service;
import com.hometurf.android.services.HomeTurfJavascriptService;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.jwt.JWT;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

public class TeamHomeTurfAuth0Service implements HomeTurfBaseAuth0Service {

    private boolean isAuthorizing = false;
    private boolean isLoggingOut = false;
    private final String audience;
    private final String scheme;
    private final Auth0 auth0Account;
    private HomeTurfJavascriptService javascriptService;
    private Activity webViewActivity;

    public TeamHomeTurfAuth0Service(String audience, String clientId, String domain, String scheme) {
        this.audience = audience;
        this.scheme = scheme;
        auth0Account = new Auth0(clientId, domain);
        AuthenticationAPIClient auth0Client = new AuthenticationAPIClient(auth0Account);
    }

    public void setJavascriptService(HomeTurfJavascriptService javascriptService) {
        this.javascriptService = javascriptService;
    }

    public void setWebViewActivity(Activity activity) {
        this.webViewActivity = activity;
    }

    public void login() {
        if (isAuthorizing) {
            javascriptService.executeJavaScriptActionInWebView("LOGIN_AUTH0_ALREADY_IN_PROGRESS");
            return;
        }
        isAuthorizing = true;
        WebAuthProvider.login(auth0Account)
                .withScheme(scheme)
                .withAudience(audience)
                .start(webViewActivity, new Callback<Credentials, AuthenticationException>() {

                    @Override
                    public void onFailure(@NonNull final AuthenticationException e) {
                        Log.e("Authentication error", e.getDescription());
                        javascriptService.executeJavaScriptActionAndStringDataInWebView("LOGIN_AUTH0_ERROR", e.getDescription());
                        isAuthorizing = false;
                    }

                    @Override
                    public void onSuccess(@Nullable final Credentials credentials) {
                        if (credentials == null) {
                            String errorMessage = "No credentials";
                            Log.e("Authentication error", errorMessage);
                            javascriptService.executeJavaScriptActionAndStringDataInWebView("LOGIN_AUTH0_ERROR",
                                    errorMessage);
                            isAuthorizing = false;
                            return;
                        }
                        JWT jwt = new JWT(credentials.getIdToken());
                        String sub = jwt.getClaim("sub").asString();
                        Log.e("Authentication", "ID Token decoded (for sub)");
                        javascriptService.executeJavaScriptActionAndRawDataInWebView("LOGIN_AUTH0_SUCCESS", String.format("{accessToken: '%s', sub: '%s'}",
                                credentials.getAccessToken(), sub));
                        isAuthorizing = false;
                    }
                });
    }

    public void logout() {
        if (isLoggingOut) {
            javascriptService.executeJavaScriptActionInWebView("LOGOUT_AUTH0_ALREADY_IN_PROGRESS");
            return;
        }
        isLoggingOut = true;
        WebAuthProvider.logout(auth0Account)
                .withScheme(scheme)
                .start(webViewActivity, new Callback<Void, AuthenticationException>() {
                    @Override
                    public void onFailure(@NonNull AuthenticationException e) {
                        javascriptService.executeJavaScriptActionAndStringDataInWebView("LOGOUT_AUTH0_ERROR",
                                e.getDescription());
                        isLoggingOut = false;
                    }
                    @Override
                    public void onSuccess(Void aVoid) {
                        javascriptService.executeJavaScriptActionInWebView("LOGOUT_AUTH0_SUCCESS");
                        isLoggingOut = false;
                    }
                });
    }
}
