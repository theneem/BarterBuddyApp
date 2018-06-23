package com.theneem.barterbuddy;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by deval on 3/2/2017.
 */

public class ReligionFragment extends Fragment implements View.OnClickListener {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {

            myView = inflater.inflate(R.layout.religionhome_layout, container, false);

            Button btnT = (Button) myView.findViewById(R.id.btnTemple);
            btnT.setOnClickListener(this);

            Button btnS = (Button) myView.findViewById(R.id.btnSaint);
            btnS.setOnClickListener(this);

            TextView txtH = (TextView) myView.findViewById( R.id.txtHeader);
            txtH.setText(GetAppString("RelegionName"));




            switch (GetAppString("ReligionId") )
            {
                case "3":
                    btnT.setBackgroundResource(R.drawable.btn_hinduism);
                    btnS.setBackgroundResource(R.drawable.btn_hinduism);

                    break;
                case "4":
                    btnT.setBackgroundResource(R.drawable.btn_buddhism);
                    btnS.setBackgroundResource(R.drawable.btn_buddhism);

                    break;
                case "1":
                    btnT.setBackgroundResource(R.drawable.btn_kids);
                    btnS.setBackgroundResource(R.drawable.btn_school);

                    break;
                case "2":
                    btnT.setBackgroundResource(R.drawable.btn_swaminarayanism);
                    btnS.setBackgroundResource(R.drawable.btn_swaminarayanism);

                    break;
                case "5":
                    btnT.setBackgroundResource(R.drawable.btn_christianism);
                    btnS.setBackgroundResource(R.drawable.btn_christianism);

                    break;

            }


            // SharedPreferences MyPref = getActivity().getSharedPreferences("com.theneem.findrligion.AppSettings", MODE_PRIVATE);

            //TextView tView = (TextView) myView.findViewById(R.id.ReligionTitle);
            //TextView tView = (TextView) myView.findViewById(R.id.ReligionTitle);
            //tView.setText(MyPref.getString("RelegionName", null));

            return myView;

        } catch (Exception e) {
            //Toast toast = Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            //toast.show();
            return myView;
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnTemple:
                SetAppString("SectionName", "Temple");
                intent = new Intent(getActivity(), MapActivity.class);
                break;
            case R.id.btnSaint:
                SetAppString("SectionName", "Saint");
                intent = new Intent(getActivity(), MapSaintActivity.class);
                break;
        }

        startActivity(intent);

    }



    public String GetAppString(String strAppKey) {

        SharedPreferences MyPref = getContext().getSharedPreferences(getString(R.string.ApplicationPreference), MODE_PRIVATE);
        return MyPref.getString(strAppKey, null);

    }



    public void SetAppString(String strAppKey, String strAppValue) {
        SharedPreferences MyPref = getContext().getSharedPreferences("com.theneem.findrligion.AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = MyPref.edit();
        editor.putString(strAppKey, strAppValue);
        editor.commit();
    }

}

