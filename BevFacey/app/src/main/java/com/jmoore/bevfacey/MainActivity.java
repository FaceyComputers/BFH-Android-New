package com.jmoore.bevfacey;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{

    public static Document docHome;//JSoup Document storing the main webpage
    public static ArrayList<String>itemTitles=new ArrayList<>();//Array that will store Title values for the Adapter
    public static ArrayList<String>itemDescs=new ArrayList<>();//Descriptions for Adapter
    public static ArrayList<String>itemPicURLS=new ArrayList<>();//URLs of images for Adapter
    public static boolean loaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetThePage gtp=new GetThePage();//Create a new object to get the webpage in the background
        gtp.execute();//Execute the background task of object

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateArrays();
            }
        });
    }

    public String[] soup2string(Elements elms){ //This method converts JSoup Elements into String arrays
        Object[]objArray=elms.toArray();
        String[]strArray=new String[objArray.length];
        for(int i=0;i<strArray.length;i++){
            strArray[i]=objArray[i].toString();
        }System.out.println(strArray[0]);return strArray;
    }

    public void CreateArrays(){ //This method creates arrays from the content in the JSoup Document
        String[]schoolNotice;//If there is no school notice then this is empty...
        try{//... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = MainActivity.docHome.select("div.school.notice");
            schoolNotice=soup2string(schoolNoticeElements);
        }catch(Exception noSchoolNoticeException){schoolNotice=new String[1];schoolNotice[0]="nonotice";}

        Elements mainContentsElements=MainActivity.docHome.select("article.content-article");//Elements from the Articles on the main page
        String[]mainContentS=soup2string(mainContentsElements);//Convert the Elements into a String array

        //noinspection StatementWithEmptyBody
        if(schoolNotice[0].equals("nonotice")){//If there is no notice, then ignore it...
            System.out.println("FAILED: Nonotice");
        }else{//... or convert the schoolNotice array into the correct display format
            prepareForDisplay(0,schoolNotice);//Send the array to the prepare method
            //ID codes for prepareForDisplay: 0=schoolNotice 1=Articles
        }
        //prepareForDisplay(1,mainContentS);
        showContentMainPage();

    }

    public String getFromPattern(String[]patterns,String theText){
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=m.group(1);
        }
        return whatToReturn;
    }

    public void prepareForDisplay(int id,String[]prep){//Prepare the raw data arrays into usable formats
        if(id==0){
            String[]patternsTitle={"<h3 class=\"notice-subtitle\">","</h3>"};
            String[]patternsDesc={"<p>","</p>"};
            String noticeTitle=getFromPattern(patternsTitle,prep[0]);
            String noticeText=getFromPattern(patternsDesc,prep[0]);
            noticeText=Jsoup.parse(noticeText).text();
            System.out.println(noticeTitle);
            MainActivity.itemTitles.add(noticeTitle);
            MainActivity.itemDescs.add(noticeText);
            MainActivity.itemPicURLS.add("");
        }else{

        }
    }

    public void showContentMainPage(){
        String[]itemTitlesArray=MainActivity.itemTitles.toArray(new String[0]);
        String[]itemDescsArray=MainActivity.itemDescs.toArray(new String[0]);
        String[]itemPicURLSarray=MainActivity.itemPicURLS.toArray(new String[0]);

        Intent intent = new Intent(this,ShowHomepage.class);
        intent.putExtra("titles",itemTitlesArray);
        intent.putExtra("descs",itemDescsArray);
        intent.putExtra("urls",itemPicURLSarray);
        this.startActivity(intent);

        /*
        String[]itemTitlesArray=MainActivity.itemTitles.toArray(new String[0]);
        String[]itemDescsArray=MainActivity.itemDescs.toArray(new String[0]);
        String[]itemPicURLSarray=MainActivity.itemPicURLS.toArray(new String[0]);
        CustomListAdapter adapter=new CustomListAdapter(this,itemTitlesArray,itemDescsArray,itemPicURLSarray);
        ListView list=(ListView)findViewById(R.id.mainlist);
        list.setAdapter(adapter);
        */
    }
}

class GetThePage extends AsyncTask<String,Integer,String>{

    @Override
    protected String doInBackground(String[]params){
        URL urlHome;
        try{urlHome=new URL("http://www.bevfacey.ca/");}catch(MalformedURLException ex){return"bad";}
        try{MainActivity.docHome=Jsoup.parse(urlHome,1500);}catch(IOException ex){return"bad";}
        return"good";
    }

    protected void onPostExecute(String result){
        if(result.equals("good")){
            MainActivity.loaded=true;
            //MainActivity man = new MainActivity();
            //man.CreateArrays();
            System.out.println("Good");
        }
        else{
            System.out.println("Failed");
        }
    }
}