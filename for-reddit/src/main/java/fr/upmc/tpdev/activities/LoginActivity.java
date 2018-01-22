package fr.upmc.tpdev.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;
import fr.upmc.tpdev.App;
import fr.upmc.tpdev.R;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    public static final String IS_LOGIN = "fr.upmc.tpdev.isLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create our RedditClient
        final OAuthHelper helper = AuthenticationManager.get().getRedditClient().getOAuthHelper();

        // OAuth2 scopes to request. See https://www.reddit.com/dev/api/oauth for a full list
        String[] scopes = {"identity", "read"};

        final URL authorizationUrl = helper.getAuthorizationUrl(App.getCredentials(), true, true, scopes);
        final WebView webView = findViewById(R.id.webview);
        // Load the authorization URL into the browser
        webView.loadUrl(authorizationUrl.toExternalForm());
        webView.setWebViewClient(new WebViewClient() {
            private boolean found;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Prevent successive calls to OAuthHelper.onUserChallenge
                if (found) return;

                if (url.contains("code=")) {
                    // We've detected the redirect URL
                    this.found = true;
                    new OnUserChallengeTask().execute(url);
                    finish();

                    getSharedPreferences(getApplicationContext())
                            .edit()
                            .putBoolean(IS_LOGIN, true)
                            .apply();

                    // start new Drawer Activity
                    startActivity(new Intent(LoginActivity.this, DrawerActivity.class));

                } else if (url.contains("error=")) {
                    // Something went wrong (the user denied our app access to their account, we specified a scope that
                    // doesn't exist, etc.) See here for the full list of error reasons:
                    // https://github.com/reddit/reddit/wiki/OAuth2#allowing-the-user-to-authorize-your-application
                    Toast.makeText(LoginActivity.this, "You must press 'allow' to log in with this account", Toast.LENGTH_SHORT).show();
                    webView.loadUrl(authorizationUrl.toExternalForm());
                }
            }
        });
    }

    private static final class OnUserChallengeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Get the current RedditClient's OAuthHelper
                OAuthHelper helper = AuthenticationManager.get().getRedditClient().getOAuthHelper();

                // Tell the helper that the user has authorized our application
                OAuthData data = helper.onUserChallenge(params[0], App.getCredentials());

                // Give the OAuthData to our RedditClient to use
                RedditClient reddit = AuthenticationManager.get().getRedditClient();
                reddit.authenticate(data);

                return reddit.getAuthenticatedUser();

            } catch (NetworkException | OAuthException e) {
                Log.e(MainActivity.TAG, "Could not log in", e);

                return null;
            }
        }
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}
