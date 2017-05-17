package com.arieldiax.codelab.fireblood.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.arieldiax.codelab.fireblood.R;
import com.arieldiax.codelab.fireblood.utils.NavigationUtils;
import com.arieldiax.codelab.fireblood.utils.ViewUtils;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /**
     * Views of the activity.
     */
    FrameLayout mMainFrameLayout;
    BottomNavigationView mMainBottomNavigationView;

    /**
     * Instance of the FirebaseAuth class.
     */
    FirebaseAuth mFirebaseAuth;

    /**
     * Canonical name of the class.
     */
    String mClassCanonicalName;

    /**
     * Animations of the activity.
     */
    Animation mFadeInAnimation;
    Animation mFadeOutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Initializes the user interface view bindings.
     */
    protected void initUi() {
        mMainFrameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);
        mMainBottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation_view);
    }

    /**
     * Initializes the back end logic bindings.
     */
    protected void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mClassCanonicalName = "";
        mFadeInAnimation = ViewUtils.getFadeInAnimation();
        mFadeOutAnimation = ViewUtils.getFadeOutAnimation();
    }

    /**
     * Initializes the event listener view bindings.
     */
    protected void initListeners() {
        mMainBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem navigationItem) {
                Class activityClass = null;
                switch (navigationItem.getItemId()) {
                    case R.id.notifications_navigation_item:
                        activityClass = NotificationsActivity.class;
                        break;
                    case R.id.search_navigation_item:
                        activityClass = SearchActivity.class;
                        break;
                    default:
                        activityClass = ProfileActivity.class;
                        break;
                }
                if (mClassCanonicalName.equals(activityClass.getCanonicalName())) {
                    return true;
                }
                NavigationUtils.stackCustomActivity(MainActivity.this, activityClass, true);
                return false;
            }
        });
        mMainBottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {

            @Override
            public void onNavigationItemReselected(@NonNull MenuItem navigationItem) {
                onNavigationItemReselectedListener();
            }
        });
    }

    /**
     * Updates the user interface view bindings.
     */
    protected void updateUi() {
    }

    /**
     * Trigger for on navigation item reselected listener.
     */
    protected void onNavigationItemReselectedListener() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainFrameLayout.startAnimation(mFadeInAnimation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainFrameLayout.startAnimation(mFadeOutAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.sign_out_menu_item:
                signOutUser();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (!NavigationUtils.isTaskRoot()) {
            NavigationUtils.unstackCustomActivity(this);
        } else {
            moveTaskToBack(true);
        }
    }

    /**
     * Signs out the user.
     */
    void signOutUser() {
        mFirebaseAuth.signOut();
        Intent activityIntent = new Intent(this, WelcomeActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        NavigationUtils.clearClassesStack();
    }
}
