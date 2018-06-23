package com.theneem.barterbuddy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by deval on 3/2/2017.
 */

public class HomeNavigationFragment extends Fragment implements View.OnClickListener {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_homenavigation, container,false);

        Button btnH = (Button)myView.findViewById(R.id.btnHinduism);
        btnH.setOnClickListener(this);


        Button btnB = (Button)myView.findViewById(R.id.btnBuddhism);
        btnB.setOnClickListener(this);

        Button btnJ = (Button)myView.findViewById(R.id.btnJainism);
        btnJ.setOnClickListener(this);

        Button btnS = (Button)myView.findViewById(R.id.btnSwaminarayan);
        btnS.setOnClickListener(this);

        //Button btnC = (Button)myView.findViewById(R.id.btnChristianism);
        //btnC.setOnClickListener(this);

        return myView;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnHinduism:
                SetAppString("RelegionName", "Hinduism");
                SetAppString("ReligionId", "3");
                break;
            case R.id.btnBuddhism:
                SetAppString("RelegionName", "Buddhism");
                SetAppString("ReligionId", "4");

                break;
            case R.id.btnJainism:
                SetAppString("RelegionName", "Education");
                SetAppString("ReligionId", "1");

                break;
            case R.id.btnSwaminarayan:
                SetAppString("RelegionName", "Swaminarayan");
                SetAppString("ReligionId", "2");

                break;
            //case R.id.btnChristianism:
             //   SetAppString("RelegionName", "Christianism");
             //   SetAppString("ReligionId", "5");

              //  break;

        }


        FragmentManager fragmentManager =  getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_main,
                        new ReligionFragment())
                .commit();

    }

    public void  SetAppString(String strAppKey, String strAppValue)
    {

        SharedPreferences MyPref = getContext().getSharedPreferences("com.theneem.findrligion.AppSettings",0);
        SharedPreferences.Editor editor = MyPref.edit();
        editor.putString(strAppKey, strAppValue);
        editor.commit();
    }

}
