package com.jmoore.bevfacey;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.*;

public class MainActivity extends AppCompatActivity{

    public static Document docHome; //JSoup Document storing the main webpage
    public static ArrayList<String>itemTitles=new ArrayList<>(); //Array that will store Title values for the Adapter
    public static ArrayList<String>itemDescs=new ArrayList<>(); //Descriptions for Adapter
    public static ArrayList<String>itemPicURLS=new ArrayList<>(); //URLs of images for Adapter
    public String imgurl="http://www.bevfacey.ca"; //URL prefix for images
    public static boolean loaded=false;
    public static int[]imageids={R.drawable.about,R.drawable.eteachers,R.drawable.programs,R.drawable.parents,R.drawable.students,R.drawable.athletics,R.drawable.guidance,R.drawable.sustainability};
    public static Typeface typeface;
    public static Typeface typefaceBody;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager am=getApplicationContext().getAssets();
        typeface=Typeface.createFromAsset(am,String.format(Locale.CANADA,"fonts/%s","goodtimegrotesk.ttf"));
        typefaceBody=Typeface.createFromAsset(am,String.format(Locale.CANADA,"fonts/%s","latolight.ttf"));
        new GetThePage().execute(); //Execute the task to retrieve from the website
    }

    public String[]soup2string(Elements elms){ //This method converts JSoup Elements into String arrays
        Object[]objArray=elms.toArray(); //First, convert the Elements into an Object array
        String[]strArray=new String[objArray.length]; //This will hold the String values of the Elements
        for(int i=0;i<strArray.length;i++){ //For Loop to convert each Object/Element into a String
            strArray[i]=objArray[i].toString(); //Convert the Object to a String
        }return strArray; //Return the complete String array
    }

    public void CreateArrays(){ //This method creates arrays from the content in the JSoup Document
        String[]schoolNotice; //If there is no school notice then this is empty...
        try{ //... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = MainActivity.docHome.select("div.school.notice"); //Search the doc for a notice
            schoolNotice=soup2string(schoolNoticeElements); //Convert the Elements into an array
        }catch(Exception noSchoolNoticeException){schoolNotice=new String[1];schoolNotice[0]="noNotice";} //No notice was found

        Elements mainContentsElements=MainActivity.docHome.select("article.content-article"); //Elements from the Articles on the main page
        String[]mainContentS=soup2string(mainContentsElements); //Convert the Elements into a String array

        //noinspection StatementWithEmptyBody
        if(schoolNotice[0].equals("noNotice")){ //If there is no notice, then ignore it...
            System.out.println("FAILED: No Notice");
        }else{ //... or convert the schoolNotice array into the correct display format
            prepareForDisplay(0,schoolNotice); //Send the array to the prepare method
            //ID codes for prepareForDisplay: 0=schoolNotice 1=Articles
        }
        prepareForDisplay(1,mainContentS); //Convert the Articles into the correct display format
        showContentMainPage(); //Lastly, display the list on the UI
    }

    public String getFromPattern(String[]patterns,String theText){ //This extracts Strings in between two other Strings
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=(m.group(1));
        }return whatToReturn;
    }

    public void prepareForDisplay(int id,String[]prep){ //Prepare the raw data arrays into usable formats
        if(id==0){ //ID 0 is schoolNotice
            String[]patternsTitle={"<h3 class=\"notice-subtitle\">","</h3>"}; //Pattern to use for finding the Title
            String[]patternsDesc={"<p>","</p>"}; //Pattern for finding the content of the notice
            String noticeTitle=getFromPattern(patternsTitle,prep[0]); //Use the pattern above to find the Title
            String noticeText=getFromPattern(patternsDesc,prep[0]); //Same thing as above but for content
            noticeText=Jsoup.parse(noticeText).text(); //Remove any HTML tags from the content
            MainActivity.itemTitles.add(":SCHOOLNOTICE:"+noticeTitle); //Add the Title as the first item in the ArrayList
            MainActivity.itemDescs.add(noticeText); //Add the Content as the first item the ArrayList
            MainActivity.itemPicURLS.add(""); //There is no image so this is just blank
        }else{ //If the ID is not 0, it's the articles
            String[]patternsTitle={"<h2 class=\"article-title\">","</h2>"}; //Pattern for finding Titles of text-based articles
            String[]patternsImgURLs={"<img alt=\"\" src=\"","\">"}; //Pattern for finding image URLs
            for(String aPrep:prep){ //Loop through each Article
                String articleImgURLs=getFromPattern(patternsImgURLs,aPrep); //First, get the image URL if there is one (we remove this URL later on)
                String articleTitle=getFromPattern(patternsTitle,aPrep); //Attempt to find a title
                Elements Elmlinks=Jsoup.parse(aPrep).select("a"); //Elements list to store all the Links in the Article
                List<String>absLinks=new ArrayList<>(); //This stores the "absolute" link, i.e. "https://www.example.com/website.htm"
                for(Element link:Elmlinks){ //This loop handles links
                    aPrep=aPrep.replace(link.toString(),link.attr("abs:href")); //Replace the raw HTML code links (<a href="example></a>) with the absolute link
                    absLinks.add(link.attr("abs:href")); //Add the absolute link to the ArrayList for later use
                }
                for(int i=0;i<absLinks.size();i++){ //Sort through the links to find duplicates and remove them
                    if(StringUtils.countMatches(aPrep,absLinks.get(i))>1){ //Checks to see if there is more than one of each link
                        aPrep=aPrep.replaceFirst(absLinks.get(i),""); //Replace the duplicate (we want to remove the first one) with a blank
                    }
                }
                if(articleTitle.isEmpty()){ //Check if the article has a Title. If not, then it is an Image article
                    articleTitle=""; //Just set the title blank. This is further dealt with in CustomListAdapter
                }
                //",,," is the placeholder for New Lines, which are dealt with in CustomListAdapter
                aPrep=aPrep.replaceAll("<br>",",,,"); //Replace breaks with a New Line placeholder
                String pretty=Jsoup.clean(aPrep,"",Whitelist.none().addTags("br","p"),new Document.OutputSettings().prettyPrint(true)); //Use JSoup to clean up the Article
                String articleDescs=Jsoup.clean(pretty,"",Whitelist.none(),new Document.OutputSettings().prettyPrint(false)); //More cleaning
                articleDescs=articleDescs.replaceAll("(\\r|\\n|\\r\\n)+",",,,"); //Replace any New Lines with the New Line placeholder
                articleDescs=articleDescs.replaceAll("<br>",",,,"); //I don't think I need this but I'm too lazy to check
                articleDescs=Jsoup.parse(articleDescs).text(); //Convert the article into the final usable format
                articleImgURLs=imgurl+articleImgURLs; //Add any image URLs to the ArrayList
                MainActivity.itemTitles.add(articleTitle); //Add the Title of the article to the ArrayList
                MainActivity.itemDescs.add(articleDescs); //Add the content of the article to the ArrayList
                MainActivity.itemPicURLS.add(articleImgURLs); //Add the Image URLs of the article to the ArrayList
            }
        }
    }

    public void showContentMainPage(){ //This finalizes the information to display and displays it
        String[]itemTitlesArray=MainActivity.itemTitles.toArray(new String[0]); //Convert the Title ArrayList into a regular String array
        String[]itemDescsArray=MainActivity.itemDescs.toArray(new String[0]); //Convert the content ArrayList into a regular String array
        String[]itemPicURLSarray=MainActivity.itemPicURLS.toArray(new String[0]); //Convert the Image URLs ArrayList into a regular String array
        CustomListAdapter adapter=new CustomListAdapter(this,itemTitlesArray,itemDescsArray,itemPicURLSarray); //Add the arrays to a custom adapter
        ListView list=(ListView)findViewById(R.id.mainlist); //Get the ID of our ListView on the main Activity
        ImageView bannerIV=(ImageView)findViewById(R.id.bannerImage);
        ImageView navBIV=(ImageView)findViewById(R.id.navButton);
        int margin=bannerIV.getHeight()+navBIV.getHeight();
        list.setPadding(0,0,0,margin);
        list.setAdapter(adapter); //Set the ListView adapter to our custom adapter, which holds the information
    }

    public void expandMenu(View view){
        ListView navView=(ListView)findViewById(R.id.navList);
        if(view.getId()==(findViewById(R.id.navButton)).getId()){
            int[]imgs={R.drawable.about,R.drawable.eteachers,R.drawable.programs,R.drawable.parents,R.drawable.students,R.drawable.athletics,R.drawable.guidance,R.drawable.sustainability};
            if(navView.getVisibility()==View.GONE){
                String[]imgstrings=new String[imgs.length];
                for(int i=0;i<imgs.length;i++){
                    imgstrings[i]=Integer.toString(imgs[i]);
                }
                CustomListAdapterMenu adapter=new CustomListAdapterMenu(this,imgs,imgstrings);
                ImageView bannerIV=(ImageView)findViewById(R.id.bannerImage);
                ImageView navBIV=(ImageView)findViewById(R.id.navButton);
                int margin=bannerIV.getHeight()+navBIV.getHeight();
                navView.setPadding(0,0,0,margin);
                navView.setAdapter(adapter);
                navView.setVisibility(View.VISIBLE);
            }else{
                navView.setVisibility(View.GONE);
            }
        }
    }

    private class GetThePage extends AsyncTask<String,Integer,String>{ //This class downloads the main webpage using JSoup
        Intent i;
        protected void onPreExecute(){
            i=new Intent(getApplicationContext(),Splash.class);
            startActivity(i);
        }
        @Override //Required for every networking AsyncTask
        protected String doInBackground(String[]params){ //Do the task in the background so we don't freeze the UI thread
            URL urlHome; //Initialize the URL variable
            try{urlHome=new URL("http://www.bevfacey.ca/");}catch(MalformedURLException ex){return"bad";} //Convert the String URL into an actual URL
            try{MainActivity.docHome=Jsoup.parse(urlHome,1500);}catch(IOException ex){return"bad";} //Try to download the URL (this only fails if the download is corrupted)
            return"good"; //Tell the post execution task that it worked
        }
        protected void onPostExecute(String result){ //This runs after doInBackground has finished
            if(result.equals("good")){ //If it was successful, then continue
                loaded=true;
                CreateArrays(); //Start the manipulation of the website data
            }
            //// TODO: 8/1/2017 Make an else statement that displays a dialog box
        }
    }
}