package obu.ckt.cricket;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import obu.ckt.cricket.comon.RegularTextView;
import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;
import obu.ckt.cricket.fragments.DashboardFragment;
import obu.ckt.cricket.fragments.QuickFragment;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DataLayer dataLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dataLayer = DataLayer.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        RegularTextView name = (RegularTextView) ((LinearLayout) navigationView.getHeaderView(0)).getChildAt(1);
        RegularTextView mail = (RegularTextView) ((LinearLayout) navigationView.getHeaderView(0)).getChildAt(2);
        name.setText(dataLayer.getUser(SharePref.getInstance(this)).name);
        mail.setText(dataLayer.getUser(SharePref.getInstance(this)).email);
        addFragment(new DashboardFragment());

        final Menu navMenu = navigationView.getMenu();
        navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ArrayList<View> menuItems = new ArrayList<>(); // save Views in this array
                navigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this); // remove the global layout listener
                for (int i = 0; i < navMenu.size(); i++) {// loops over menu items  to get the text view from each menu item
                    final MenuItem item = navMenu.getItem(i);
                    navigationView.findViewsWithText(menuItems, item.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                }
                for (final View menuItem : menuItems) {// loops over the saved views and sets the font
                    ((TextView) menuItem).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Aleo-Regular.otf"));
                }
            }
        });
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.add(R.id.container_home, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_home);
            if (f instanceof DashboardFragment) {
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                finish();
            } else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Utils.singleAlertDialogCallBack(HomeActivity.this, "Do you want to logout", new Utils.Logout() {
                @Override
                public void click() {
                    SharePref.getInstance(HomeActivity.this).clearAll();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    finish();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_home) {
            // Handle the camera action
            addFragment(new DashboardFragment());
        } else if (id == R.id.nav_history) {
            // Handle the camera action
            addFragment(QuickFragment.newInstance("Completed"));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
