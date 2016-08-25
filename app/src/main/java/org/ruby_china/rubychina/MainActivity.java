package org.ruby_china.rubychina;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;

import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        TurbolinksSession.getDefault(this).setDebugLoggingEnabled(true);

        WebSettings webSettings = TurbolinksSession.getDefault(this).getWebView().getSettings();
        webSettings.setUserAgentString("turbolinks-app, ruby-china, official, android");

        location = getString(R.string.root_url) + "/topics";

        setAsLogined(false);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    public void newTopic(View view) {
        visitProposedToLocationWithAction(getString(R.string.root_url) + "/topics/new", "advance");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_home:
                return true;
            case R.id.nav_settings:
                visitProposedToLocationWithAction(getString(R.string.root_url) + "/account/edit", "advance");
                return true;
            default:
                return true;
        }
    }

    class VisitCompletedCallback implements  ValueCallback<String> {
        MainActivity mActivity;

        public VisitCompletedCallback(MainActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onReceiveValue(String value) {
            try {
                JSONObject appData = new JSONObject(value);
                boolean logined = appData.has("current_user_id");
                mActivity.setAsLogined(logined);
            } catch (JSONException e) {
            }
        }
    }

    @Override
    public void visitCompleted() {
        TurbolinksSession.getDefault(this).getWebView().evaluateJavascript(
                "App;",
                new VisitCompletedCallback(this)
        );

        super.visitCompleted();
    }

    public void setAsLogined(boolean logined) {
        mNavigationView.getMenu().setGroupVisible(R.id.group_login, !logined);
        mNavigationView.getMenu().setGroupVisible(R.id.group_logined, logined);
    }
}
