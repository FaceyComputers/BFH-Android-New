package com.jmoore.bevfacey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class CustomListAdapterSubMenu extends ArrayAdapter<String> { //This class is the list of Information
    private final Activity context;
    private final String[] subText;
    private GetSubPages gsp;

    CustomListAdapterSubMenu(Activity context, String[] subText) {
        super(context, R.layout.mylistsubmenu, subText);
        this.context = context;
        this.subText = subText;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylistsubmenu, null, true);

        TextView tv = rowView.findViewById(R.id.subMenuText);
        tv.setTypeface(MainActivity.typefaceMenuItems);
        tv.setText(subText[position]);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv2 = (TextView)view;
                String value = tv2.getText().toString();
                int pos = MainActivity.globalSubTitles.indexOf(value);
                String subURL = MainActivity.globalSubText.get(pos);
                getSubPages(subURL);
            }
        });
        return rowView;
    }

    private void getSubPages(String url) {
        gsp = new GetSubPages();
        gsp.execute(url);
    }

    private void stopSubPages() {
        gsp.cancel(true);
        gsp.isCancelled = true;
    }

    @SuppressWarnings("deprecation")
    private class GetSubPages extends AsyncTask<String, Integer, String> {
        private String urlStr;
        private Intent i;
        private ProgressDialog progress = new ProgressDialog(context);
        private boolean isCancelled = false;
        protected void onPreExecute() {
            progress.setIndeterminate(true);
            progress.setTitle("Loading...");
            progress.setMessage("Please wait");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(true);
            progress.setCanceledOnTouchOutside(true);
            progress.setInverseBackgroundForced(true);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    stopSubPages();
                }
            });
            progress.show();
        }
        @Override
        protected String doInBackground(String[] params) {
            if(!params[0].contains("://")) {
                urlStr = MainActivity.globalURL + params[0];
            } else {
                if(params[0].contains("phone")) {
                    urlStr = params[0].replace("phone://", "tel:");
                } else {
                    urlStr = params[0];
                }
            }
            URL url;
            if(!urlStr.contains("tel:")) {
                if(urlStr.contains("bevfacey.ca")) {

                    try {
                        url = new URL(urlStr); //Convert the String URL into an actual URL
                    } catch (MalformedURLException ex) {
                        return "bad";
                    }
                    try {
                        MainActivity.docSub = Jsoup.parse(url, 15000); //Try to download the URL (this only fails if the download is corrupted)
                    } catch (IOException ex) {
                        return "bad";
                    }
                    i = new Intent(context, SubPageActivity.class);
                    i.putExtra("url", urlStr);
                    if (isCancelled) {
                        i = null;
                        return "bad";
                    }
                    context.startActivity(i);
                    return "good"; //Tell the post execution task that it worked
                } else {
                    PackageManager pm = context.getPackageManager();
                    if(urlStr.contains("powerschool")){
                        if(isPackageInstalled("com.powerschool.portal",pm)){
                            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.powerschool.portal");
                            context.startActivity(launchIntent);
                            return "good";
                        } else {
                            i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(urlStr));
                            context.startActivity(i);
                            return "good";
                        }
                    } else if(urlStr.contains("classroom")) {
                        if(isPackageInstalled("com.google.android.apps.classroom",pm)){
                            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.classroom");
                            context.startActivity(launchIntent);
                            return "good";
                        } else {
                            i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(urlStr));
                            context.startActivity(i);
                            return "good";
                        }
                    }
                    else {
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(urlStr));
                        context.startActivity(i);
                        return "good";
                    }
                }
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(urlStr));
                    context.startActivity(intent);
                } catch(Exception ex) {
                    return "bad";
                }
                return "good";
            }
        }
        protected void onPostExecute(String result) {
            if("good".equals(result)) {
                progress.dismiss();
            } else {
                progress.dismiss();
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
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

        private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
            try {
                packageManager.getPackageInfo(packagename, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
    }
}