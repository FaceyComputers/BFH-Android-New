package com.jmoore.bevfacey;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new WaitForLoaded().execute();
    }

    private class WaitForLoaded extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String[]params){
            while(!MainActivity.loaded){
                //Do nothing
            }
            return "done";
        }
        protected void onPostExecute(String result){
            if(result.equals("done")){
                Splash.this.finish();
            }
        }
    }
}