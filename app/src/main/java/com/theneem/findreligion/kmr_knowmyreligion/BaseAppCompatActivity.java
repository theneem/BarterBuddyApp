package com.theneem.barterbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

/**
 * Created by deval on 11/5/2017.
 */

public class BaseAppCompatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private LoginButton loginButton;
    private CallbackManager callbackManager;




    protected void BaseonCreate() {

        //super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        NavigationView nv  = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton)nv.getHeaderView(0).findViewById(R.id.login_button);


        //setContentView(R.layout.activity_facebooklogin);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                SetWelcome();

            }

            @Override
            public void onCancel() {
                //info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                //info.setText("Login attempt failed.");

                e.printStackTrace();

            }
        });


        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    SetWelcome("");
                }
            }
        };


        //face book set welcome message
        if(AccessToken.getCurrentAccessToken() != null)
        {

            try
            {
                SetWelcome();
            }

            catch(Exception ex)
            {

                ex.printStackTrace();
            }

        }
        else
        {
            SetWelcome("");
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();






    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {


            int id = item.getItemId();
            boolean isRelegionScreen = true;


            FragmentManager fragmentManager = getSupportFragmentManager();

            // TODO: THIS NEEDS TO BE A GLOBAL FUNCATION WHICH CAN BE CALL FROM ANY WHERE
            if (id == R.id.nav_Browser) {
                SetAppString("RelegionName", "Hinduism");
            } else if (id == R.id.nav_Buddhism) {
                SetAppString("RelegionName", "Buddhism");
            } else if (id == R.id.nav_Jainism) {
                SetAppString("RelegionName", "Jainism");
            } else if (id == R.id.nav_Swaminarayan) {
                SetAppString("RelegionName", "Swaminarayan");
            } else if (id == R.id.nav_aboutapp) {
                SetAppString("RelegionName", "AboutApp");
                isRelegionScreen =false;
            } else if (id == R.id.nav_aboutus) {
                SetAppString("RelegionName", "AboutUs");
                isRelegionScreen = false;
            }


            if(isRelegionScreen == true) {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main,
                                new com.theneem.barterbuddy.ReligionFragment())
                        .commit();
            }   
            else {
                if(id == R.id.nav_aboutapp)
                {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_main,
                                    new com.theneem.barterbuddy.AboutAppFragment())
                            .commit();
                }
                else if (id == R.id.nav_aboutus)
                {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_main,
                                    new com.theneem.barterbuddy.AboutTheneemFragment())
                            .commit();
                }

            }


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

        } catch (Exception e) {

            //Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            //toast.show();
            return false;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void SetAppString(String strAppKey, String strAppValue) {

        SharedPreferences MyPref = getApplicationContext().getSharedPreferences("com.theneem.findrligion.AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPref.edit();
        editor.putString(strAppKey, strAppValue);
        editor.commit();
    }

    public String GetAppString(String strAppKey) {

        SharedPreferences MyPref = getApplicationContext().getSharedPreferences(getString(R.string.ApplicationPreference), MODE_PRIVATE);
        return MyPref.getString(strAppKey, null);

    }


    public void SetWelcome(String welcomeMessage)
    {


        NavigationView nv  = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);

        TextView txtwelcome = (TextView) nv.getHeaderView(0).findViewById(R.id.welcomemessage);

        txtwelcome.setText(welcomeMessage);
    }

    public void SetWelcome()
    {

        Profile profile = Profile.getCurrentProfile();


        //txtwelcome.setVisibility(View.VISIBLE);

        SetWelcome("Welcome " + profile.getFirstName()
                + " " + profile.getLastName());

        SetAppString("profileid", profile.getId());
       // profile.getProfilePictureUri()

    }


    // end of class
}
