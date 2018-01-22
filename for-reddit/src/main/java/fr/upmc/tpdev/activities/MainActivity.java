package fr.upmc.tpdev.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.dean.jraw.auth.AuthenticationManager;
import net.dean.jraw.auth.AuthenticationState;
import net.dean.jraw.auth.NoSuchTokenException;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthException;

import java.util.Locale;

import fr.upmc.tpdev.App;
import fr.upmc.tpdev.R;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Force app to use eng, if not by default **
        Configuration config = new Configuration();
        config.locale = Locale.ENGLISH;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        // language **
    }

    public void login(View view) { startActivity(new Intent(this, LoginActivity.class)); }
    public void userInfo(View view) { startActivity(new Intent(this, UserInfoActivity.class)); }
    /*public void drawer(View view) { startActivity(new Intent(this, DrawerActivity.class)); }
    public void tab(View view) { startActivity(new Intent(this, TabbedActivity.class)); }
    public void details(View view) { startActivity(new Intent(this, PostActivity.class)); }*/

    @Override
    protected void onResume() {
        super.onResume();
        AuthenticationState state = AuthenticationManager.get().checkAuthState();
        Log.d(TAG, "AuthenticationState for onResume(): " + state);

        switch (state) {
            case READY:
                //Toast.makeText(MainActivity.this, "READY", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "READY");
                break;
            case NONE:
                //Toast.makeText(MainActivity.this, "Log in first", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Log in first");
                break;
            case NEED_REFRESH:
                //Toast.makeText(MainActivity.this, "REF", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "REF ");
                refreshAccessTokenAsync();
                break;
        }

        boolean isConnected = getSharedPreferences(this).getBoolean(LoginActivity.IS_LOGIN, false);

        if (isConnected) {
            startActivity(new Intent(this, DrawerActivity.class));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void refreshAccessTokenAsync() {
        new AsyncTask<Credentials, Void, Void>() {
            @Override
            protected Void doInBackground(Credentials... params) {
                try {
                    AuthenticationManager.get().refreshAccessToken(App.getCredentials());
                } catch (NoSuchTokenException | OAuthException e) {
                    Log.e(TAG, "Could not refresh access token", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Log.d(TAG, "Reauthenticated");
            }
        }.execute();
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}
