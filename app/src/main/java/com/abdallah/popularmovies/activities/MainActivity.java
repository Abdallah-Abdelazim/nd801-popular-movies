package com.abdallah.popularmovies.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.abdallah.popularmovies.R;
import com.abdallah.popularmovies.fragments.BrowseMoviesFragment;
import com.abdallah.popularmovies.fragments.FavoriteMoviesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String STATE_ACTIVITY_TITLE = "STATE_ACTIVITY_TITLE";

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getCharSequence(STATE_ACTIVITY_TITLE));
        }
        else {
            // open the default navigation drawer fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, BrowseMoviesFragment.newInstance())
                    .commit();
            setTitle(R.string.title_browse_movies_fragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(STATE_ACTIVITY_TITLE, getTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_browse_movies) {
            fragment = BrowseMoviesFragment.newInstance();
        } else if (id == R.id.nav_fav_movies) {
            fragment = FavoriteMoviesFragment.newInstance();
        }
        else {
            Log.d(TAG, "Unrecognized navigation item selected!");
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof BrowseMoviesFragment) {
            setTitle(R.string.title_browse_movies_fragment);
            navigationView.getMenu().findItem(R.id.nav_browse_movies).setChecked(true);
        }
        else if (currentFragment instanceof FavoriteMoviesFragment) {
            setTitle(R.string.title_favorite_movies_fragment);
            navigationView.getMenu().findItem(R.id.nav_fav_movies).setChecked(true);
        }
        else {
            Log.d(TAG, "Unrecognized current fragment!");
        }
    }
}
