package com.arieldiax.codelab.fireblood.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.arieldiax.codelab.fireblood.R;
import com.arieldiax.codelab.fireblood.utils.ViewUtils;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    /**
     * Views of the activity.
     */
    BottomNavigationView mMainBottomNavigationView;

    /**
     * Instance of the FirebaseAuth class.
     */
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        init();
        updateUi();
    }

    /**
     * Initializes the user interface view bindings.
     */
    void initUi() {
        mMainBottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation_view);
    }

    /**
     * Initializes the back end logic bindings.
     */
    void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Updates the user interface view bindings.
     */
    void updateUi() {
        mMainBottomNavigationView.setSelectedItemId(R.id.search_navigation_item);
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
        moveTaskToBack(true);
    }

    /**
     * Signs out the user.
     */
    void signOutUser() {
        mFirebaseAuth.signOut();
        ViewUtils.startCustomActivity(this, WelcomeActivity.class, null, true);
    }
}
