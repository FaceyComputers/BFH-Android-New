package com.jmoore.bevfacey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/** Bev Facey High School official app
 *
 * @author Joshua Moore (https://github.com/supamonkey2000)
 * https://github.com/FaceyComputers/BFH-Android-New
 */


public class MainActivity extends AppCompatActivity {

    public static Document docHome; //JSoup Document storing the main webpage
    public static Document docETeachers; //JSoup Document storing the ETeacher links
    public static Document docSub; //JSoup Document for other classes to write to (declaring here allows for it to be reused)
    public static ArrayList<String> articleContent = new ArrayList<>();

    public static final String globalURL = "http://www.bevfacey.ca"; //URL prefix for images
    public static int loadedStatus = 0; //Is the app finished loading the Main page and ETeacher page? 0=no, 1=success, -1=failed
    public static String[] menuItemTitles = {"Quicklinks", "About", "ETeachers", "Programs", "Parents",
            "Students", "Athletics", "Guidance", "Sustainability"}; //Items to use in the Nav menu
    public static Typeface typeface; //Default typeface
    public static Typeface typefaceBody; //Typeface for body sections
    public static Typeface typefaceMenuItems; //Typeface for menu items
    public CustomListAdapter adapter;

    //Length of how many sections are in each page (grabbed from website)
    public static int subQuicklinksLength = 5; //Hardcoded links
    public static int subAboutLength;
    public static int subProgramsLength;
    public static int subParentsLength;
    public static int subStudentsLength;
    public static int subAthleticsLength;
    public static int subGuidanceLength;
    public static int subETeachersLength;

    //These hold the information for Titles and Links of submenu items
    public static List<String> subQuicklinksTitles = new ArrayList<>();
    public static List<String> subQuicklinksText = new ArrayList<>();
    public static List<String> subAboutTitles = new ArrayList<>();
    public static List<String> subAboutText = new ArrayList<>();
    public static List<String> subProgramsTitles = new ArrayList<>();
    public static List<String> subProgramsText = new ArrayList<>();
    public static List<String> subParentsTitles = new ArrayList<>();
    public static List<String> subParentsText = new ArrayList<>();
    public static List<String> subStudentsTitles = new ArrayList<>();
    public static List<String> subStudentsText = new ArrayList<>();
    public static List<String> subAthleticsTitles = new ArrayList<>();
    public static List<String> subAthleticsText = new ArrayList<>();
    public static List<String> subGuidanceTitles = new ArrayList<>();
    public static List<String> subGuidanceText = new ArrayList<>();
    public static List<String> subETeachersTitles = new ArrayList<>();
    public static List<String> subETeachersText = new ArrayList<>();

    //These hold the info for every sub menu. For example, when a sub item is pressed it gets
    //the text value and finds the index of that value in this list. Then it gets the link for
    //the item at that same index.
    public static List<String> globalSubTitles = new ArrayList<>();
    public static List<String> globalSubText = new ArrayList<>();

    //Patterns
    public static final String[] patternLink = {"<a href=\"", "\">"};

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Android default method
        super.onCreate(savedInstanceState); //Android default statement
        setContentView(R.layout.activity_main); //Android default statement
        AssetManager am = getApplicationContext().getAssets(); //Allows for creating Typefaces using Assets
        typeface = Typeface.createFromAsset(am, String.format(Locale.CANADA, "fonts/%s", "goodtimegrotesk.ttf")); //Default Typeface
        typefaceBody = Typeface.createFromAsset(am, String.format(Locale.CANADA, "fonts/%s", "latoregular.ttf")); //Typeface for body items
        typefaceMenuItems = Typeface.createFromAsset(am, String.format(Locale.CANADA, "fonts/%s", "goodtimegrotesk.ttf")); //Typeface for menu items
        addQuicklinks();
        new GetThePage().execute(); //Execute the AsyncTask used to retrieve info from the website
    }

    private void addQuicklinks() {
        subQuicklinksTitles.add("Powerschool");
        subQuicklinksTitles.add("Google Classroom");
        subQuicklinksTitles.add("MyPass");
        subQuicklinksTitles.add("Call Office");
        subQuicklinksTitles.add("Daily Bulletin");
        subQuicklinksLength = subQuicklinksTitles.size();
        subQuicklinksText.add("https://powerschool.eips.ca/public/");
        subQuicklinksText.add("https://classroom.google.com");
        subQuicklinksText.add("https://public.education.alberta.ca/PASI/myPass/Welcome/Index");
        subQuicklinksText.add("phone://780-467-0044");
        subQuicklinksText.add("http://bevfacey.ca/parents/daily-bulletin");
    }

    public String[] soup2string(Elements elms) { //Converts JSoup Elements into String arrays
        Object[] objArray = elms.toArray(); //First, convert the Elements into an Object array
        String[] strArray = new String[objArray.length]; //This will hold the String values of the Elements
        for(int i = 0; i < strArray.length; i++) { //For Loop to convert each Object/Element into a String
            strArray[i] = objArray[i].toString(); //Convert the Object to a String
        }
        return strArray; //Return the complete String array
    }

    public void createSubArrays() { //Creates the sub menus
        Elements navElms = MainActivity.docHome.select("nav.primary-navigation"); //Find the website navigation bar
        Elements liElms = navElms.select("li.children"); //Store all the website expandable menus
        for(Element elm : liElms) {
            String elmString = elm.toString(); //Convert the current Element into a String for easier handling
            if(elmString.toLowerCase().contains("about")) { //All of these just add the items to each submenu
                aboutSub(elm);
            } else if(elmString.toLowerCase().contains("programs")) {
                programsSub(elm);
            } else if(elmString.toLowerCase().contains("parents")) {
                parentsSub(elm);
            } else if(elmString.toLowerCase().contains("students")) {
                studentsSub(elm);
            } else if(elmString.toLowerCase().contains("athletics")) {
                athleticsSub(elm);
            } else if(elmString.toLowerCase().contains("guidance")) {
                guidanceSub(elm);
            }
        }
        eTeachersSub(); //This adds all the teachers to the ETeacher sub menu
        addGlobal(); //Add all the items to the global arrays
    }

    public void aboutSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subAboutTitles.add(title);
            subAboutText.add(link);
        }
        subAboutTitles.remove(0);
        subAboutText.remove(0);
        subAboutLength = subAboutTitles.size();
    }

    public void programsSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subProgramsTitles.add(Jsoup.parse(title).text());
            subProgramsText.add(link);
        }
        subProgramsTitles.remove(0);
        subProgramsText.remove(0);
        subProgramsLength = subProgramsTitles.size();
    }

    public void parentsSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subParentsTitles.add(Jsoup.parse(title).text());
            subParentsText.add(link);
        }
        subParentsTitles.remove(0);
        subParentsText.remove(0);
        subParentsLength = subParentsTitles.size();
    }

    public void studentsSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subStudentsTitles.add(Jsoup.parse(title).text());
            subStudentsText.add(link);
        }
        subStudentsTitles.remove(0);
        subStudentsText.remove(0);
        subStudentsLength = subStudentsTitles.size();
    }

    public void athleticsSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subAthleticsTitles.add(Jsoup.parse(title).text());
            subAthleticsText.add(link);
        }
        subAthleticsTitles.remove(0);
        subAthleticsText.remove(0);
        subAthleticsLength = subAthleticsTitles.size();
    }

    public void guidanceSub(Element elm) {
        Elements aboutClasses = elm.select("li");
        String[] patternTitle = {"<b>", "</b>"};
        for(Element el : aboutClasses) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternLink, elString);
            String title = MainActivity.getFromPatternStatic(patternTitle, elString);
            subGuidanceTitles.add(Jsoup.parse(title).text());
            subGuidanceText.add(link);
        }
        subGuidanceTitles.remove(0);
        subGuidanceText.remove(0);
        subGuidanceLength = subGuidanceTitles.size();
    }

    public void eTeachersSub() {
        Elements pickElms = MainActivity.docETeachers.select("div.main-content");
        Elements teachersElms = pickElms.select("option");
        String[] patternETlink = {"value=\"", "\">"};
        String[] patternETtitle = {"\">", "</option>"};
        for(Element el : teachersElms) {
            String elString = el.toString();
            String link = MainActivity.getFromPatternStatic(patternETlink, elString);
            String title = MainActivity.getFromPatternStatic(patternETtitle, elString);
            subETeachersTitles.add(title);
            subETeachersText.add(link);
        }
        subETeachersTitles.remove(0);
        subETeachersText.remove(0);
        subETeachersLength = subETeachersTitles.size();
    }

    public void addGlobal() { //Populate the Global arrays with the other arrays
        globalSubTitles.addAll(subQuicklinksTitles);
        globalSubTitles.addAll(subAboutTitles);
        globalSubTitles.addAll(subETeachersTitles);
        globalSubTitles.addAll(subProgramsTitles);
        globalSubTitles.addAll(subParentsTitles);
        globalSubTitles.addAll(subStudentsTitles);
        globalSubTitles.addAll(subAthleticsTitles);
        globalSubTitles.addAll(subGuidanceTitles);
        //Text (links in this case)
        globalSubText.addAll(subQuicklinksText);
        globalSubText.addAll(subAboutText);
        globalSubText.addAll(subETeachersText);
        globalSubText.addAll(subProgramsText);
        globalSubText.addAll(subParentsText);
        globalSubText.addAll(subStudentsText);
        globalSubText.addAll(subAthleticsText);
        globalSubText.addAll(subGuidanceText);
    }

    public void createArrays() { //This method creates arrays from the content in the JSoup Document
        String[] schoolNotice; //If there is no school notice then this is empty...
        try { //... and we need a try/catch to see if there is a notice
            Elements schoolNoticeElements = MainActivity.docHome.select("div.school.notice"); //Search the doc for a notice
            schoolNotice = soup2string(schoolNoticeElements); //Convert the Elements into an array
            if(schoolNotice.length == 0) {
                throw new Exception();
            }
        } catch(Exception noSchoolNoticeException) {
            Log.w("NoSchoolNotice", "No School or District notices found");
            schoolNotice = new String[1];
            schoolNotice[0] = "noNotice";
        } //No notice was found

        Elements mainContentsElements = MainActivity.docHome.select("article.content-article"); //Elements from the Articles on the main page
        String[] mainContentS = soup2string(mainContentsElements); //Convert the Elements into a String array

        if(!schoolNotice[0].equals("noNotice")) { //If there is no notice, then ignore it...
            prepareForDisplay(0, schoolNotice); //Send the array to the prepare method
            //ID codes for prepareForDisplay: 0=schoolNotice 1=Articles
        }
        prepareForDisplay(1, mainContentS); //Convert the Articles into the correct display format
        showContentMainPage(); //Lastly, display the list on the UI
    }

    /*public String getFromPattern(String[] patterns, String theText) { //This extracts Strings in between two other Strings
        Pattern pattern = Pattern.compile(Pattern.quote(patterns[0]) + "(.*?)" + Pattern.quote(patterns[1]));
        Matcher m = pattern.matcher(theText);
        String whatToReturn = "";
        while(m.find()) {
            whatToReturn = (m.group(1));
        }
        return whatToReturn;
    }*/

    public static String getFromPatternStatic(String[] patterns, String theText) { //This extracts Strings in between two other Strings
        Pattern pattern = Pattern.compile(Pattern.quote(patterns[0]) + "(.*?)" + Pattern.quote(patterns[1]));
        Matcher m = pattern.matcher(theText);
        String whatToReturn = "";
        while(m.find()) {
            whatToReturn = (m.group(1));
        }
        if(!whatToReturn.isEmpty()) {
            if(patterns[0].contains("p")) {
                return "DONOTREPLACE";
            } else {
                return whatToReturn;
            }
        } else {
            return whatToReturn;
        }
    }

    public static String getFromPatternStaticArray(String[] patterns, String theText) { //This extracts Strings in between two other Strings
        Pattern pattern = Pattern.compile(Pattern.quote(patterns[0]) + "(.*?)" + Pattern.quote(patterns[1]));
        Matcher m = pattern.matcher(theText);
        StringBuilder whatToReturnBuilder = new StringBuilder();
        while(m.find()) {
            whatToReturnBuilder.append(",.,").append(m.group(1));
        }
        String whatToReturn = whatToReturnBuilder.toString();
        whatToReturn = whatToReturn.replaceFirst(",.,", "");
        return whatToReturn;
    }

    public void prepareForDisplay(int id, String[] prep) { //Prepare the raw data arrays into usable formats
        if(id == 0) { //ID 0 is schoolNotice
            for(String aPrep : prep) {
                MainActivity.articleContent.add(":SCHOOL_NOTICE:" + aPrep);
            }
        } else { //If the ID is not 0, it's the articles
            MainActivity.articleContent.addAll(Arrays.asList(prep));
        }
    }

    public void showContentMainPage() { //This finalizes the information to display and displays it
        String[] articleContentArray = MainActivity.articleContent.toArray(new String[0]);
        adapter = new CustomListAdapter(this, articleContentArray); //Add the arrays to a custom adapter

        RecyclerView list = findViewById(R.id.mainlist); //Get the ID of our ListView on the main Activity
        ImageView bannerIV = findViewById(R.id.bannerImage); //Get the ID of our Banner image
        TextView navBIV = findViewById(R.id.navButton); //Get the ID of the navigation button (which is ironically not a button but a textview)
        navBIV.setTypeface(MainActivity.typefaceMenuItems); //Set the navigation button typeface to the menu typeface
        navBIV.setText(R.string.navigation); //I dearly hope this doesn't need explaining
        //We don't want our ListView height to be more than the screen since that causes for items to be cut off.
        //To counter this, we get the height of the banner and the nav button and set the ListView padding to that value.
        int margin = bannerIV.getHeight() + navBIV.getHeight();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        int verticalSpacing = 5;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(this, R.drawable.drop_shadow);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(itemDecorator);
        list.addItemDecoration(shadowItemDecorator);
        list.setHasFixedSize(false);
        list.setPadding(0, 0, 0, margin);
        list.setItemViewCacheSize(0);
        list.setAdapter(adapter); //Set the ListView adapter to our custom adapter, which holds the information
    }

    public void expandMenu(View view) { //This method expands the navigation menu
        ListView navView = findViewById(R.id.navList); //Access the ListView so we can add a CustomListAdapter
        if(view.getId() == (findViewById(R.id.navButton)).getId()) { //Check if the view is the Nav button
            if(navView.getVisibility() == View.GONE) { //Is the menu visible?
                CustomListAdapterMenu adapter = new CustomListAdapterMenu(this, menuItemTitles, menuItemTitles); //Create the items to add to the menu
                ImageView bannerIV = findViewById(R.id.bannerImage); //This is meant for getting the height of the banner so we can make the menu fit on screen
                TextView navBIV = findViewById(R.id.navButton); //Same as above but for the navigation button
                int margin = bannerIV.getHeight() + navBIV.getHeight(); //The margin is used to make sure the menu isn't cut off at the bottom of the screen
                navView.setPadding(0, 0, 0, margin); //Apply the margin to the menu
                navView.setAdapter(adapter); //Add all the items from the adapter to the menu
                navView.setVisibility(View.VISIBLE); //Make it visible
            } else { //The menu is already visible...
                navView.setVisibility(View.GONE); //... so make it invisible
            }
        }
    }

    @SuppressLint("StaticFieldLeak") //Yes. I know. This is terrible. But the darn thing breaks without it
    private class GetThePage extends AsyncTask<String, Integer, String> { //This class downloads the main webpage using JSoup
        private Intent splashIntent = null; //The Intent for the splash page
        protected void onPreExecute() { //This is done before the background task executes
            splashIntent = new Intent(getApplicationContext(), Splash.class); //First create the Intent for the splash page...
            startActivity(splashIntent); //... Then display it
        }
        @Override //Required for every networking AsyncTask
        protected String doInBackground(String[] params) { //Do the task in the background so we don't freeze the UI thread
            URL urlHome; //Initialize the URL variable
            URL urlETeachers; //Initialize the ETeachers URL
            try {
                urlHome = new URL("http://www.bevfacey.ca/"); //Convert the String URL into an actual URL
            } catch(MalformedURLException ex) {
                return "bad";
            }
            try {
                urlETeachers = new URL("http://www.bevfacey.ca/eteachers"); //Convert the String URL into an actual URL
            } catch(MalformedURLException ex) {
                return "bad";
            }
            try {
                MainActivity.docHome = Jsoup.parse(urlHome, 15000); //Try to download the URL
            } catch(IOException ex) {
                return "bad";
            }
            try {
                MainActivity.docETeachers = Jsoup.parse(urlETeachers, 15000); //Try to download the URL
            } catch(IOException ex) {
                return "bad";
            }
            return"good"; //Tell the post execution task that it worked
        }
        protected void onPostExecute(String result) { //This runs after doInBackground has finished
            if("good".equals(result)) { //If it was successful, then continue
                loadedStatus = 1;
                createArrays(); //Start the manipulation of the website data
                createSubArrays();
            } else {
                loadedStatus = -1;
            }
        }
    }
}