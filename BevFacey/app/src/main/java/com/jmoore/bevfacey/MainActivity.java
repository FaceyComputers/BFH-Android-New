package com.jmoore.bevfacey;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{

    public static Document docHome;//JSoup Document storing the main webpage
    public static ArrayList<String>itemTitles=new ArrayList<>();//Array that will store Title values for the Adapter
    public static ArrayList<String>itemDescs=new ArrayList<>();//Descriptions for Adapter
    public static ArrayList<String>itemPicURLS=new ArrayList<>();//URLs of images for Adapter
    public String imgurl="http://www.bevfacey.ca";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetThePage().execute();
    }

    public String[]soup2string(Elements elms){ //This method converts JSoup Elements into String arrays
        Object[]objArray=elms.toArray();
        String[]strArray=new String[objArray.length];
        for(int i=0;i<strArray.length;i++){
            strArray[i]=objArray[i].toString();
        }return strArray;
    }

    public void CreateArrays(){ //This method creates arrays from the content in the JSoup Document
        String[]schoolNotice;//If there is no school notice then this is empty...
        try{//... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = MainActivity.docHome.select("div.school.notice");//Search the doc for a notice
            schoolNotice=soup2string(schoolNoticeElements);//Convert the Elements into an array
        }catch(Exception noSchoolNoticeException){schoolNotice=new String[1];schoolNotice[0]="nonotice";}//No notice was found

        Elements mainContentsElements=MainActivity.docHome.select("article.content-article");//Elements from the Articles on the main page
        String[]mainContentS=soup2string(mainContentsElements);//Convert the Elements into a String array

        //noinspection StatementWithEmptyBody
        if(schoolNotice[0].equals("nonotice")){//If there is no notice, then ignore it...
            System.out.println("FAILED: Nonotice");
        }else{//... or convert the schoolNotice array into the correct display format
            prepareForDisplay(0,schoolNotice);//Send the array to the prepare method
            //ID codes for prepareForDisplay: 0=schoolNotice 1=Articles
        }
        prepareForDisplay(1,mainContentS);
        showContentMainPage();
    }

    public String getFromPattern(String[]patterns,String theText){
        System.out.println(theText);
        System.out.println("");System.out.println("");
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=(m.group(1));
        }return whatToReturn;
    }

    public void prepareForDisplay(int id,String[]prep){//Prepare the raw data arrays into usable formats
        if(id==0){
            String[]patternsTitle={"<h3 class=\"notice-subtitle\">","</h3>"};
            String[]patternsDesc={"<p>","</p>"};
            String noticeTitleArray=getFromPattern(patternsTitle,prep[0]);
            String noticeTextArray=getFromPattern(patternsDesc,prep[0]);
            String noticeTitle=noticeTitleArray;
            String noticeText=noticeTextArray;
            //for(String aNoticeTextArray:noticeTextArray){
            //    noticeText=noticeText.concat(". "+aNoticeTextArray);
            //}
            noticeText=Jsoup.parse(noticeText).text();
            MainActivity.itemTitles.add(noticeTitle);
            MainActivity.itemDescs.add(noticeText);
            MainActivity.itemPicURLS.add("");
        }else{
            String[]patternsTitle={"<h2 class=\"article-title\">","</h2>"};
            String[]patternsDesc={"<p>","</p>"};
            String[]patternsImgURLs={"<img alt=\"\" src=\"","\">"};
            for(String aPrep:prep){
                String articleTitle=getFromPattern(patternsTitle,aPrep);//Attempt to find a title
                if(articleTitle.isEmpty()){//There is no article title
                    articleTitle="Image";
                }
                String articleDescsArray=getFromPattern(patternsDesc,aPrep);//Get the article text
                String articleImgURLsArray=getFromPattern(patternsImgURLs,aPrep);
                //String articleDescs=articleDescsArray;
                String articleDescs=Jsoup.parse(aPrep).text();
                String articleImgURLs=articleImgURLsArray;
                /*for(String sectionOfADA:articleDescsArray){
                    articleDescs=articleDescs.concat(". "+sectionOfADA);
                }
                for(String sectionOfAIUA:articleImgURLsArray){
                    articleImgURLs=articleImgURLs.concat("$"+sectionOfAIUA);
                }*/
                articleDescs=Jsoup.parse(articleDescs).text();
                articleImgURLs=imgurl+articleImgURLs;
                MainActivity.itemTitles.add(articleTitle);
                MainActivity.itemDescs.add(articleDescs);
                MainActivity.itemPicURLS.add(articleImgURLs);
            }
        }
    }

    public void showContentMainPage(){
        String[]itemTitlesArray=MainActivity.itemTitles.toArray(new String[0]);
        String[]itemDescsArray=MainActivity.itemDescs.toArray(new String[0]);
        String[]itemPicURLSarray=MainActivity.itemPicURLS.toArray(new String[0]);
        CustomListAdapter adapter=new CustomListAdapter(this,itemTitlesArray,itemDescsArray,itemPicURLSarray);
        ListView list=(ListView)findViewById(R.id.mainlist);
        list.setAdapter(adapter);

    }

    private class GetThePage extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String[]params){
            URL urlHome;
            try{urlHome=new URL("http://www.bevfacey.ca/");}catch(MalformedURLException ex){return"bad";}
            try{MainActivity.docHome=Jsoup.parse(urlHome,1500);}catch(IOException ex){return"bad";}
            return"good";
        }
        protected void onPostExecute(String result){
            if(result.equals("good")){
                CreateArrays();
            }
        }
    }
}