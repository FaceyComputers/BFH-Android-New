package com.jmoore.bevfacey;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{

    static Document docHome;//JSoup Document storing the main webpage
    static ArrayList<String> itemTitles=new ArrayList<>();//Array that will store Title values for the Adapter
    static ArrayList<String>itemDescs=new ArrayList<>();//Descriptions for Adapter
    static ArrayList<String>itemPicURLS=new ArrayList<>();//URLs of images for Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetThePage gtp=new GetThePage();//Create a new object to get the webpage in the background
        gtp.execute();//Execute the background task of object
    }

    public static String[] soup2string(Elements elms){ //This method converts JSoup Elements into String arrays
        Object[]objArray=elms.toArray();
        String[]strArray=new String[objArray.length];
        for(int i=0;i<strArray.length;i++){
            strArray[i]=objArray[i].toString();
        }return strArray;
    }

    public static void CreateArrays(){ //This method creates arrays from the content in the JSoup Document
        String[]schoolNotice;//If there is no school notice then this is empty...
        try{//... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = docHome.select("div.school.notice");
            schoolNotice=soup2string(schoolNoticeElements);
        }catch(Exception noSchoolNoticeException){schoolNotice=new String[1];schoolNotice[0]="nonotice";}

        Elements mainContentsElements=docHome.select("article.content-article");//Elements from the Articles on the main page
        String[]mainContentS=soup2string(mainContentsElements);//Convert the Elements into a String array

        //noinspection StatementWithEmptyBody
        if(schoolNotice[0].equals("nonotice")){//If there is no notice, then ignore it...
            System.out.println("FAILED: Nonotice");
        }else{//... or convert the schoolNotice array into the correct display format
            prepareForDisplay(0,schoolNotice);//Send the array to the prepare method
            //ID codes for prepareForDisplay: 0=schoolNotice 1=Articles
        }
        //prepareForDisplay(1,mainContentS);
        //new MainActivity().showContentMainPage();

    }

    public static String getFromPattern(String[]patterns,String theText){
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=m.group(1);
        }
        return whatToReturn;
    }

    public static void prepareForDisplay(int id,String[]prep){//Prepare the raw data arrays into usable formats
        if(id==0){
            String[]patternsTitle={"<h3 class=\"notice-subtitle\">","</h3>"};
            String[]patternsDesc={"<p>","</p>"};
            String noticeTitle=getFromPattern(patternsTitle,prep[0]);
            String noticeText=getFromPattern(patternsDesc,prep[0]);

            //noticeText=noticeText.replaceAll("<.*?>","");
            noticeText=Jsoup.parse(noticeText).text();
            System.out.println(noticeTitle);
            System.out.println(noticeText);
        }else{

        }
    }

    public void showContentMainPage(){
        String[]itemTitlesArray=itemTitles.toArray(new String[0]);
        String[]itemDescsArray=itemDescs.toArray(new String[0]);
        String[]itemPicURLSarray=itemPicURLS.toArray(new String[0]);
        CustomListAdapter adapter=new CustomListAdapter(this,itemTitlesArray,itemDescsArray,itemPicURLSarray);
        ListView list=(ListView)findViewById(R.id.mainlist);
        list.setAdapter(adapter);

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Selecteditem= itemTitlesArray[+position];
                Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();

            }
        });*/
    }
}

class GetThePage extends AsyncTask<String,Integer,String>{

    @Override
    protected String doInBackground(String[]params){
        URL urlHome;
        try{urlHome=new URL("http://www.bevfacey.ca/");}catch(MalformedURLException ex){return"bad";}
        try{MainActivity.docHome=Jsoup.parse(urlHome,5000);}catch(IOException ex){return"bad";}
        return"good";
    }

    protected void onPostExecute(String result){
        if(result.equals("good")){
            MainActivity.CreateArrays();
        }
        else{
            System.out.println("Failed");
        }
    }
}