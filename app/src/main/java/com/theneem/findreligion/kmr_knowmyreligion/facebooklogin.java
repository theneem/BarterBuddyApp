package com.theneem.barterbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class facebooklogin extends Activity {


    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_facebooklogin);

        callbackManager = CallbackManager.Factory.create();

        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        }
        catch (Exception ex) {

            ex.printStackTrace();
        }



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if(GetAppString("Lastpage") == "saint")
                {
                //lets redirect the  user to next screen
                Intent intentSubmit = new Intent(getApplicationContext(),SubmitSaintActivity.class);

                startActivity(intentSubmit);
                }
                else if (GetAppString("Lastpage") == "saintreview")
                {
                    //lets redirect the  user to next screen
                    Intent intentSubmit = new Intent(getApplicationContext(),MapSaintActivity.class);

                    startActivity(intentSubmit);

                }
                else if (GetAppString("Lastpage") == "templereview")
                {
                    //lets redirect the  user to next screen
                    Intent intentSubmit = new Intent(getApplicationContext(),MapActivity.class);

                    startActivity(intentSubmit);

                }

                else
                {
                    //lets redirect the  user to next screen
                    Intent intentSubmit = new Intent(getApplicationContext(),SubmitTempleActivity.class);

                    startActivity(intentSubmit);
                }

            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");

                    e.printStackTrace();

            }
        });


    }




    public String GetAppString(String strAppKey) {

        SharedPreferences MyPref = getApplicationContext().getSharedPreferences(getString(R.string.ApplicationPreference), MODE_PRIVATE);
        return MyPref.getString(strAppKey, null);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



}