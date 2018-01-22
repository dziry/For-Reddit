package fr.upmc.tpdev.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.auth.AuthenticationManager;

import java.util.ArrayList;

import fr.upmc.tpdev.R;
import fr.upmc.tpdev.adapters.PostCardAdapter;
import fr.upmc.tpdev.beans.Post;
import fr.upmc.tpdev.fragments.PostCardFragment;


public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String USER = "fr.upmc.tpdev.user";
    public static final String KARMA = "fr.upmc.tpdev.karma";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private TextView mUser;
    private TextView mKarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ****** Drawer.
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ****** Drawer.

        // ****** Tab.
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        // ****** Tab.

        // ****** Cards.
        RecyclerView recyclerView = findViewById(R.id.rv_cards_in_fragment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        SparseArray<ArrayList<Post>> postList = new SparseArray<>();
        postList.put(1, new ArrayList<Post>());
        postList.put(2, new ArrayList<Post>());

        PostCardAdapter adapter = new PostCardAdapter(recyclerView, postList, 1);
        recyclerView.setAdapter(adapter);
        // ****** Cards.

        mUser = findViewById(R.id.tv_user);
        mKarma = findViewById(R.id.tv_karma);

        /*String user = getSharedPreferences(this).getString(USER, null);
        int karma = getSharedPreferences(this).getInt(KARMA, -1);

        if (user == null || karma < 0) {
            new LoginTask().execute();

        } else {
            mUser.setText(user);
            String karmaString = karma + " karma";
            mKarma.setText(karmaString);
        }*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add top communities as menu items in nav menu drawer
        addMenuItemInNavMenuDrawer();

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);

        return true;
    }

    final String[] SUBREDDIT_TAB = new String[]{"funny", "AskReddit", "todayilearned", "science", "worldnews", "pics", "IAmA", "gaming", "videos", "movies", "aww", "Music", "blog", "gifs", "news", "explainlikeimfive", "askscience", "EarthPorn", "books", "television", "mildlyinteresting", "LifeProTips"};

    private void addMenuItemInNavMenuDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        MenuItem topCommunitiesItem = menu.getItem(0);
        SubMenu topCommunities = topCommunitiesItem.getSubMenu();

        for (int i = 0; i < 10; i++) {
            topCommunities.add(R.id.gr_top_communities, i, Menu.NONE, "r/" + SUBREDDIT_TAB[i])
                    // TODO set custom icon for each sub
                    .setIcon(R.drawable.ic_menu_share);
        }

        // Only one item in this group can be checked at a time
        topCommunities.setGroupCheckable(R.id.gr_top_communities, true, true);

        navView.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Toast.makeText(getApplicationContext(), "id=" + SUBREDDIT_TAB[id], Toast.LENGTH_SHORT).show();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else {
            String subredditName = SUBREDDIT_TAB[id];

            Intent intent = new Intent(DrawerActivity.this, SubredditActivity.class);
            intent.putExtra("sub", subredditName);

            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PostCardFragment
            return PostCardFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages : Home, Global
            return 2;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RedditClient redditClient = AuthenticationManager.get().getRedditClient();

            String fullName = redditClient.me().getFullName();
            int karma = redditClient.me().getCommentKarma();

            getSharedPreferences(getApplicationContext())
                    .edit()
                    .putString(USER, fullName)
                    .putInt(KARMA, karma)
                    .apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            String user = getSharedPreferences(getApplicationContext()).getString(USER, null);
            int karma = getSharedPreferences(getApplicationContext()).getInt(KARMA, -1);

            mUser.setText(user);
            String karmaString = karma + " karma";
            mKarma.setText(karmaString);
        }
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
}
