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
import android.widget.TextView;
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


/** Bev Facey High School official app
 *
 * @author Joshua Moore (https://github.com/supamonkey2000)
 * https://github.com/FaceyComputers/BFH-Android-New
 */


public class MainActivity extends AppCompatActivity{

    public static Document docHome; //JSoup Document storing the main webpage
    public static Document docETeachers; //JSoup Document storing the ETeacher links
    public static Document docSub; //JSoup Document for other classes to write to (declaring here allows for it to be reused)
    public static ArrayList<String>itemTitles=new ArrayList<>(); //Array that will store Title values for the Adapter
    public static ArrayList<String>itemDescs=new ArrayList<>(); //Descriptions for Adapter
    public static ArrayList<String>itemPicURLS=new ArrayList<>(); //URLs of images for Adapter
    public static String globalURL="http://www.bevfacey.ca"; //URL prefix for images
    public static boolean loaded=false; //Is the app finished loading the Main page and ETeacher page
    public static String[]menuItemTitles={"About","ETeachers","Programs","Parents","Students","Athletics","Guidance","Sustainability"}; //Items to use in the Nav menu
    public static Typeface typeface; //Default typeface
    public static Typeface typefaceBody; //Typeface for body sections
    public static Typeface typefaceMenuItems; //Typeface for menu items

    //Length of how many sections are in each page (grabbed from website)
    public static int subAboutLength;
    public static int subProgramsLength;
    public static int subParentsLength;
    public static int subStudentsLength;
    public static int subAthleticsLength;
    public static int subGuidanceLength;
    public static int subETeachersLength;

    //These hold the information for Titles and Links of submenu items
    public static List<String>subAboutTitles=new ArrayList<>();
    public static List<String>subAboutText=new ArrayList<>();
    public static List<String>subProgramsTitles=new ArrayList<>();
    public static List<String>subProgramsText=new ArrayList<>();
    public static List<String>subParentsTitles=new ArrayList<>();
    public static List<String>subParentsText=new ArrayList<>();
    public static List<String>subStudentsTitles=new ArrayList<>();
    public static List<String>subStudentsText=new ArrayList<>();
    public static List<String>subAthleticsTitles=new ArrayList<>();
    public static List<String>subAthleticsText=new ArrayList<>();
    public static List<String>subGuidanceTitles=new ArrayList<>();
    public static List<String>subGuidanceText=new ArrayList<>();
    public static List<String>subETeachersTitles=new ArrayList<>();
    public static List<String>subETeachersText=new ArrayList<>();

    //These hold the info for every sub menu. For example, when a sub item is pressed it gets
    //the text value and finds the index of that value in this list. Then it gets the link for
    //the item at that same index.
    public static List<String>globalSubTitles=new ArrayList<>();
    public static List<String>globalSubText=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){ //Android default method
        super.onCreate(savedInstanceState); //Android default statement
        setContentView(R.layout.activity_main); //Android default statement
        AssetManager am=getApplicationContext().getAssets(); //Allows for creating Typefaces using Assets
        typeface=Typeface.createFromAsset(am,String.format(Locale.CANADA,"fonts/%s","goodtimegrotesk.ttf")); //Default Typeface
        typefaceBody=Typeface.createFromAsset(am,String.format(Locale.CANADA,"fonts/%s","latoregular.ttf")); //Typeface for body items
        typefaceMenuItems=Typeface.createFromAsset(am,String.format(Locale.CANADA,"fonts/%s","goodtimegrotesk.ttf")); //Typeface for menu items

        new GetThePage().execute(); //Execute the AsyncTask used to retrieve info from the website
    }

    public String[]soup2string(Elements elms){ //Converts JSoup Elements into String arrays
        Object[]objArray=elms.toArray(); //First, convert the Elements into an Object array
        String[]strArray=new String[objArray.length]; //This will hold the String values of the Elements
        for(int i=0;i<strArray.length;i++){ //For Loop to convert each Object/Element into a String
            strArray[i]=objArray[i].toString(); //Convert the Object to a String
        }return strArray; //Return the complete String array
    }

    public void createSubArrays(){ //Creates the sub menus
        Elements navElms=MainActivity.docHome.select("nav.primary-navigation"); //Find the website navigation bar
        Elements liElms=navElms.select("li.children"); //Store all the website expandable menus
        for(Element elm:liElms){
            String elmString=elm.toString(); //Convert the current Element into a String for easier handling
            if(elmString.toLowerCase().contains("about")){ //All of these just add the items to each submenu
                aboutSub(elm);
            }else if(elmString.toLowerCase().contains("programs")){
                programsSub(elm);
            }else if(elmString.toLowerCase().contains("parents")){
                parentsSub(elm);
            }else if(elmString.toLowerCase().contains("students")){
                studentsSub(elm);
            }else if(elmString.toLowerCase().contains("athletics")){
                athleticsSub(elm);
            }else if(elmString.toLowerCase().contains("guidance")){
                guidanceSub(elm);
            }
        }
        eTeachersSub(); //This adds all the teachers to the ETeacher sub menu
        addGlobal(); //Add all the items to the global arrays
    }

    public void aboutSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subAboutTitles.add(title);
            subAboutText.add(link);
        }
        subAboutTitles.remove(0);
        subAboutText.remove(0);
        subAboutLength=subAboutTitles.size();
    }

    public void programsSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subProgramsTitles.add(Jsoup.parse(title).text());
            subProgramsText.add(link);
        }
        subProgramsTitles.remove(0);
        subProgramsText.remove(0);
        subProgramsLength=subProgramsTitles.size();
    }

    public void parentsSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subParentsTitles.add(Jsoup.parse(title).text());
            subParentsText.add(link);
        }
        subParentsTitles.remove(0);
        subParentsText.remove(0);
        subParentsLength=subParentsTitles.size();
    }

    public void studentsSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subStudentsTitles.add(Jsoup.parse(title).text());
            subStudentsText.add(link);
        }
        subStudentsTitles.remove(0);
        subStudentsText.remove(0);
        subStudentsLength=subStudentsTitles.size();
    }

    public void athleticsSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subAthleticsTitles.add(Jsoup.parse(title).text());
            subAthleticsText.add(link);
        }
        subAthleticsTitles.remove(0);
        subAthleticsText.remove(0);
        subAthleticsLength=subAthleticsTitles.size();
    }

    public void guidanceSub(Element elm){
        Elements aboutClasses=elm.select("li");
        String[]patternLink={"<a href=\"","\">"};
        String[]patternTitle={"<b>","</b>"};
        for(Element el:aboutClasses){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternLink,elString);
            String title=MainActivity.getFromPatternStatic(patternTitle,elString);
            subGuidanceTitles.add(Jsoup.parse(title).text());
            subGuidanceText.add(link);
        }
        subGuidanceTitles.remove(0);
        subGuidanceText.remove(0);
        subGuidanceLength=subGuidanceTitles.size();
    }

    public void eTeachersSub(){
        Elements pickElms=MainActivity.docETeachers.select("div.main-content");
        Elements teachersElms=pickElms.select("option");
        String[]patternETlink={"value=\"","\">"};
        String[]patternETtitle={"\">","</option>"};
        for(Element el:teachersElms){
            String elString=el.toString();
            String link=MainActivity.getFromPatternStatic(patternETlink,elString);
            String title=MainActivity.getFromPatternStatic(patternETtitle,elString);
            subETeachersTitles.add(title);
            subETeachersText.add(link);
        }
        subETeachersTitles.remove(0);
        subETeachersText.remove(0);
        subETeachersLength=subETeachersTitles.size();
    }

    public void addGlobal(){ //Populate the Global arrays with the other arrays
        globalSubTitles.addAll(subAboutTitles);
        globalSubTitles.addAll(subETeachersTitles);
        globalSubTitles.addAll(subProgramsTitles);
        globalSubTitles.addAll(subParentsTitles);
        globalSubTitles.addAll(subStudentsTitles);
        globalSubTitles.addAll(subAthleticsTitles);
        globalSubTitles.addAll(subGuidanceTitles);
        //Text (links in this case)
        globalSubText.addAll(subAboutText);
        globalSubText.addAll(subETeachersText);
        globalSubText.addAll(subProgramsText);
        globalSubText.addAll(subParentsText);
        globalSubText.addAll(subStudentsText);
        globalSubText.addAll(subAthleticsText);
        globalSubText.addAll(subGuidanceText);
    }

    public void createArrays(){ //This method creates arrays from the content in the JSoup Document
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

    public static String getFromPatternStatic(String[]patterns,String theText){ //This extracts Strings in between two other Strings
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=(m.group(1));
        }
        if(!whatToReturn.isEmpty()){
            if(patterns[0].contains("p")){
                return"DONOTREPLACE";
            }else{
                return whatToReturn;
            }
        }else{
            return whatToReturn;
        }
    }
    public static String getFromPatternStaticArray(String[]patterns,String theText){ //This extracts Strings in between two other Strings
        Pattern pattern=Pattern.compile(Pattern.quote(patterns[0])+"(.*?)"+Pattern.quote(patterns[1]));
        Matcher m=pattern.matcher(theText);
        String whatToReturn="";
        while(m.find()){
            whatToReturn=whatToReturn+",.,"+(m.group(1));
        }
        whatToReturn=whatToReturn.replaceFirst(",.,","");
        return whatToReturn;
    }

    public void prepareForDisplay(int id,String[]prep){ //Prepare the raw data arrays into usable formats
        if(id==0){ //ID 0 is schoolNotice
            String[]patternsTitle={"<h3 class=\"notice-subtitle\">","</h3>"}; //Pattern to use for finding the Title
            //DEPRECATED//String[]patternsDesc={"<p>","</p>"}; //Pattern for finding the content of the notice
            for(String aPrep:prep){
                String noticeTitle=getFromPattern(patternsTitle,aPrep); //Use the pattern above to find the Title
                //DEPRECATED//String noticeText=getFromPattern(patternsDesc,aPrep); //Same thing as above but for content
                String noticeText=aPrep.replaceAll("(\\r|\\n|\\r\\n)+",",,,"); //Replace any New Lines with the New Line placeholder
                noticeText=noticeText.replaceFirst(",,,","");
                noticeText=noticeText.replaceFirst(",,,","");
                noticeText=Jsoup.parse(noticeText).text(); //Remove any HTML tags from the content
                noticeText=noticeText.replace(noticeTitle,""); //Remove the title from the paragraph
                noticeText=noticeText.replace("District Notice",""); //Remove District Notice from the paragraph
                noticeText=noticeText.replace("School Notice",""); //Remove School Notice from the paragraph
                MainActivity.itemTitles.add(Jsoup.parse(":SCHOOLNOTICE:" + noticeTitle).text()); //Add the Title as the first item in the ArrayList
                MainActivity.itemDescs.add(noticeText); //Add the Content as the first item the ArrayList
                MainActivity.itemPicURLS.add(""); //There is no image so this is just blank
            }
        }else{ //If the ID is not 0, it's the articles
            String[]patternsTitle={"<h2 class=\"article-title\">","</h2>"}; //Pattern for finding Titles of text-based articles
            String[]patternsImgURLs={"<img alt=\"\" src=\"","\">"}; //Pattern for finding image URLs
            for(String aPrep:prep){ //Loop through each Article
                String articleImgURLs=getFromPattern(patternsImgURLs,aPrep); //First, get the image URL if there is one (we remove this URL later on)
                String articleTitle=getFromPattern(patternsTitle,aPrep); //Attempt to find a title
                Elements Elmlinks=Jsoup.parse(aPrep).select("a"); //Elements list to store all the Links in the Article
                List<String>absLinks=new ArrayList<>(); //This stores the "absolute" link, i.e. "https://www.example.com/website.htm"
                for(Element link:Elmlinks){ //This loop handles links
                    String tmpLink=link.attr("abs:href");
                    if(tmpLink.isEmpty()){
                        tmpLink=globalURL + link.attr("href");
                    }
                    aPrep=aPrep.replace(link.toString(),tmpLink); //Replace the raw HTML code links (<a href="example></a>) with the absolute link
                    absLinks.add(tmpLink); //Add the absolute link to the ArrayList for later use
                }
                if(absLinks.size()>1) {
                    for (int i = 0; i < absLinks.size(); i++) { //Sort through the links to find duplicates and remove them
                        if (StringUtils.countMatches(aPrep, absLinks.get(i)) > 1) { //Checks to see if there is more than one of each link
                            aPrep = aPrep.replaceFirst(absLinks.get(i), ""); //Replace the duplicate (we want to remove the first one) with a blank
                        }
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
                articleTitle=Jsoup.parse(articleTitle).text(); //Remove any HTML tags from the title
                /*if(absLinks.size()>1){
                    for(int i = 0; i < absLinks.size(); i++) {
                        articleDescs = articleDescs.replace(absLinks.get(i),"<a href=\""+absLinks.get(i)+"\">link</a>");
                    }
                }*/
                articleImgURLs=globalURL+articleImgURLs; //Add any image URLs to the ArrayList
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
        ImageView bannerIV=(ImageView)findViewById(R.id.bannerImage); //Get the ID of our Banner image
        TextView navBIV=(TextView)findViewById(R.id.navButton); //Get the ID of the navigation button (which is ironically not a button but a textview)
        navBIV.setTypeface(MainActivity.typefaceMenuItems); //Set the navigation button typeface to the menu typeface
        navBIV.setText(R.string.navigation); //I dearly hope this doesn't need explaining
        //We don't want our ListView height to be more than the screen since that causes for items to be cut off.
        //To counter this, we get the height of the banner and the nav button and set the ListView padding to that value.
        int margin=bannerIV.getHeight()+navBIV.getHeight();
        list.setPadding(0,0,0,margin);
        list.setAdapter(adapter); //Set the ListView adapter to our custom adapter, which holds the information
    }

    public void expandMenu(View view){ //This method expands the navigation menu
        ListView navView=(ListView)findViewById(R.id.navList); //Access the ListView so we can add a CustomListAdapter
        if(view.getId()==(findViewById(R.id.navButton)).getId()){ //Check if the view is the Nav button
            if(navView.getVisibility()==View.GONE){ //Is the menu visible?
                CustomListAdapterMenu adapter=new CustomListAdapterMenu(this,menuItemTitles,menuItemTitles); //Create the items to add to the menu
                ImageView bannerIV=(ImageView)findViewById(R.id.bannerImage); //This is meant for getting the height of the banner so we can make the menu fit on screen
                TextView navBIV=(TextView)findViewById(R.id.navButton); //Same as above but for the navigation button
                int margin=bannerIV.getHeight()+navBIV.getHeight(); //The margin is used to make sure the menu isn't cut off at the bottom of the screen
                navView.setPadding(0,0,0,margin); //Apply the margin to the menu
                navView.setAdapter(adapter); //Add all the items from the adapter to the menu
                navView.setVisibility(View.VISIBLE); //Make it visible
            }else{ //The menu is already visible...
                navView.setVisibility(View.GONE); //... so make it invisible
            }
        }
    }

    private class GetThePage extends AsyncTask<String,Integer,String>{ //This class downloads the main webpage using JSoup
        private Intent splashIntent=null; //The Intent for the splash page
        protected void onPreExecute(){ //This is done before the background task executes
            splashIntent=new Intent(getApplicationContext(),Splash.class); //First create the Intent for the splash page...
            startActivity(splashIntent); //... Then display it
        }
        @Override //Required for every networking AsyncTask
        protected String doInBackground(String[]params){ //Do the task in the background so we don't freeze the UI thread
            URL urlHome; //Initialize the URL variable
            URL urlETeachers; //Initialize the ETeachers URL
            try{urlHome=new URL("http://www.bevfacey.ca/");}catch(MalformedURLException ex){return"bad";} //Convert the String URL into an actual URL
            try{urlETeachers=new URL("http://www.bevfacey.ca/eteachers");}catch(MalformedURLException ex){return"bad";} //Convert the String URL into an actual URL
            try{MainActivity.docHome=Jsoup.parse(urlHome,3000);}catch(IOException ex){return"bad";} //Try to download the URL (this only fails if the download is corrupted)
            try{MainActivity.docETeachers=Jsoup.parse(urlETeachers,3000);}catch(IOException ex){return"bad";} //Try to download the URL (this only fails if the download is corrupted)
            return"good"; //Tell the post execution task that it worked
        }
        protected void onPostExecute(String result){ //This runs after doInBackground has finished
            if("good".equals(result)){ //If it was successful, then continue
                loaded=true;
                createArrays(); //Start the manipulation of the website data
                createSubArrays();
            }
        }
    }
}