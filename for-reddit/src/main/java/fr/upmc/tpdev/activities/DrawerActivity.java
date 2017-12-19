package fr.upmc.tpdev.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.upmc.tpdev.R;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    private void addMenuItemInNavMenuDrawer() {
        NavigationView navView = findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        MenuItem topCommunitiesItem = menu.getItem(0);
        SubMenu topCommunities = topCommunitiesItem.getSubMenu();

        final String[] SUBREDDIT_TAB = new String[]{"funny", "AskReddit", "todayilearned", "science", "worldnews", "pics", "IAmA", "gaming", "videos", "movies", "aww", "Music", "blog", "gifs", "news", "explainlikeimfive", "askscience", "EarthPorn", "books", "television", "mildlyinteresting", "LifeProTips"};

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } elseif (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else*/ if (id == 2) {
            Log.i("Drawer", "2");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
