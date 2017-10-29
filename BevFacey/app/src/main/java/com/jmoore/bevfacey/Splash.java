package com.jmoore.bevfacey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String version = "";
        int code = -1;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            code = pInfo.versionCode;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView versionTV = findViewById(R.id.versionTextView);
        String v = "v" + version + "  c" + code;
        versionTV.setText(v);
        new WaitForLoaded().execute();
    }

    private class WaitForLoaded extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String[] params) {
            //noinspection StatementWithEmptyBody
            while(MainActivity.loadedStatus == 0){}
            return "done";
        }
        protected void onPostExecute(String result) {
            if("done".equals(result) && MainActivity.loadedStatus == 1) {
                Splash.this.finish();
            } else {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getApplicationContext());
                dlgAlert.setMessage("There was an error during loading. Try again later.");
                dlgAlert.setTitle("Oops! Sorry about that...");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    }
}