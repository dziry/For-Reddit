package net.dean.jrawandroidexample;

import android.app.Application;
import android.net.wifi.hotspot2.pps.Credential;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.android.AndroidRedditClient;
import net.dean.jraw.android.AndroidTokenStore;
import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.RefreshTokenHandler;
import net.dean.jraw.auth.TokenStore;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.oauth.Credentials;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RedditClient reddit = new AndroidRedditClient(this);
        reddit.setLoggingMode(LoggingMode.ALWAYS);
        AuthenticationManager.get().init(reddit, new RefreshTokenHandler(new AndroidTokenStore(this), reddit));
    }

    public static Credentials getCredentials() {
        return Credentials.installedApp("2WLdJr7h-UwC_A", "https://github.com/AdelLarbi/for-reddit.git");
        //throw new AssertionError("Don't forget to set your Credentials!");
    }
}
