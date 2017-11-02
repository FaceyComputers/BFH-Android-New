package com.bevfacey.bfhapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

//      0                   1                       2                   3               4
//R.drawable.about  R.drawable.eteachers  R.drawable.programs  R.drawable.parents  R.drawable.students
//          5                   6                       7
// R.drawable.athletics  R.drawable.guidance  R.drawable.sustainability

class CustomListAdapterMenu extends ArrayAdapter<String> {//This class is the list of Information
    private final Activity context;
    private final String[] menuText;
    private GetSubPages gsp;

    CustomListAdapterMenu(Activity context, String[] menuText, String[] menuText2) {
        super(context, R.layout.mylistmenu, menuText2);
        this.context = context;
        this.menuText = menuText;
    }

    private String[] getNormalArrays(List<String> list) {
        String[] normalArray;
        Object[] linksObjArray = list.toArray(); //First, convert the Elements into an Object array
        String[] linksStrArray = new String[linksObjArray.length]; //This will hold the String values of the Elements
        for(int i = 0; i < linksStrArray.length; i++) { //For Loop to convert each Object/Element into a String
            linksStrArray[i] = linksObjArray[i].toString(); //Convert the Object to a String
        }
        normalArray = linksStrArray;
        return normalArray;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.mylistmenu, null, true);

        TextView tv = rowView.findViewById(R.id.menuText);
        tv.setTypeface(MainActivity.typefaceMenuItems);
        tv.setText(menuText[position]);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv2 = (TextView) view;
                String title = tv2.getText().toString();
                if(title.equals(MainActivity.menuItemTitles[0])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subQuicklinksTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((height * MainActivity.subQuicklinksLength) - height / 2) + (dpToPx(3) * MainActivity.subQuicklinksLength)));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[1])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subAboutTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subAboutLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[2])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subETeachersTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subETeachersLength) - height * 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[3])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subProgramsTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subProgramsLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[4])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subParentsTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subParentsLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[5])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subStudentsTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subStudentsLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[6])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subAthleticsTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subAthleticsLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[7])) {
                    ListView lv = rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility() == View.GONE) {
                        int height = tv2.getHeight();
                        lv.setAdapter(new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subGuidanceTitles)));
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (height * MainActivity.subGuidanceLength) - height / 2));
                        lv.setVisibility(View.VISIBLE);
                    } else {
                        lv.setVisibility(View.GONE);
                    }
                } else if(title.equals(MainActivity.menuItemTitles[8])) {
                    getSubPages("/facey-sustainability");
                }
            }
        });
        return rowView;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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
        protected void onPreExecute(){
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
                urlStr = params[0];
            }
            URL url;
            try {
                url = new URL(urlStr); //Convert the String URL into an actual URL
            } catch(MalformedURLException ex) {
                return"bad";
            }
            try {
                MainActivity.docSub = Jsoup.parse(url, 15000); //Try to download the URL
            } catch(IOException ex) {
                return"bad";
            }
            i = new Intent(context, SubPageActivity.class);
            i.putExtra("url", urlStr);
            if(isCancelled) {
                i = null;
                return "bad";
            }
            context.startActivity(i);
            return"good"; //Tell the post execution task that it worked
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
    }
}