package com.jmoore.bevfacey;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    static Document docHome;//JSoup Document storing the main webpage
    static String[]itemTitles;//Array that will store Title values for the Adapter
    static String[]itemDescs;//Descriptions for Adapter
    static String[]itemPicURLS;//URLs of images for Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetThePage gtp=new GetThePage();//Create a new object to get the webpage in the background
        gtp.execute();//Execute the background task of object
    }

    public static String[] soup2string(Elements elms){ //This method converts JSoup Elements into String arrays
        String[]strArray=new String[elms.size()];
        Object[]objArray=elms.toArray();
        for(int i=0;i<strArray.length;i++){
            strArray[i]=objArray[i].toString();
        }return strArray;
    }

    public static void CreateArrays(){ //This method creates arrays from the content in the JSoup Document
        String[]schoolNotice;//If there is no school notice then this is empty...
        try{//... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = docHome.select("div.school notice");
            schoolNotice=soup2string(schoolNoticeElements);
        }catch(Exception noSchoolNoticeException){schoolNotice=new String[0];schoolNotice[0]="nonotice";}

        Elements mainContentsElements=docHome.select("article.content-article");//Elements from the Articles on the main page
        String[]mainContentS=soup2string(mainContentsElements);//Convert the Elements into a String array

        //noinspection StatementWithEmptyBody
        if(schoolNotice[0].equals("nonotice")){//If there is no notice, then ignore it...
            //Empty
        }else{//... or convert the schoolNotice array into the correct display format
            prepareForDisplay(schoolNotice);
        }

    }

    public static void prepareForDisplay(String[]prep){//Prepare the raw data arrays into usable formats

    }
/*
    protected void showContentMainPage(){
        CustomListAdapter adapter=new CustomListAdapter(this, itemname,itemdesc,imgid);
        ListView list=(ListView)findViewById(R.id.mainlist);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Selecteditem= itemname[+position];
                Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();

            }
        });
    }*/
}

class GetThePage extends AsyncTask<String,Integer,String>{

    @Override
    protected String doInBackground(String[]params){
        URL urlHome=null;
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