package com.jmoore.bevfacey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SubPageActivity extends AppCompatActivity{

    public Document doc=MainActivity.docSub;
    public Intent intent;
    public Context context;
    public GridLayout grid;
    public ListView list;
    public static List<String>h3titles=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_page);
        context=getApplicationContext();
        intent=getIntent();
        grid=(GridLayout)findViewById(R.id.subLayout);
        list=(ListView)findViewById(R.id.subContent);
        analyze();
    }

    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            ImageView bannerIV=(ImageView)findViewById(R.id.bannerImage2);
            int margin=bannerIV.getHeight();
            list.setPadding(0,0,0,margin);
        }
    }

    private String[]getNormalArrays(List<String>list){
        String[]normalArray;
        Object[]linksObjArray=list.toArray(); //First, convert the Elements into an Object array
        String[]linksStrArray=new String[linksObjArray.length]; //This will hold the String values of the Elements
        for(int i=0;i<linksStrArray.length;i++){ //For Loop to convert each Object/Element into a String
            linksStrArray[i]=linksObjArray[i].toString(); //Convert the Object to a String
        }
        normalArray=linksStrArray;
        return normalArray;
    }

    public void analyze(){//Determine what type of page it is
        String url=intent.getStringExtra("url");
        if(url.toLowerCase().contains("bell-times")){
            bellTimes();
        }else if(url.toLowerCase().contains("calendar")){
            calendar();
        }

        else{
            other();
        }
    }

    public void other(){
        Elements sectionsElms=doc.select("article.content-article");
        List<String>secTitles=new ArrayList<>();
        List<String>secItems=new ArrayList<>();
        String[]patternH2title={"<h2 class=\"article-title\">","</h2>"};
        String[]patternH3title={"<h3>","</h3>"};
        String[]patternNewLine={"</p>","<p>"};
        for(Element elm:sectionsElms){
            String str=elm.toString().replace("\n","");
            //String newLinesToReplace=MainActivity.getFromPatternStaticArray(patternNewLine,str);

            String subTitle=MainActivity.getFromPatternStaticArray(patternH3title,str);
            String[]subTitleArray=subTitle.split(",.,");
            for(String s:subTitleArray){
                if(!s.equals("")){
                    str=str.replaceFirst(s,".,.");
                    h3titles.add(s);
                }
            }

            str=str.replaceAll(Pattern.quote(MainActivity.getFromPatternStatic(patternNewLine,str)),"");
            str=str.replaceAll("</p><p>",",,,");
            str=str.replaceAll("<br>",",,,");
            String title=MainActivity.getFromPatternStatic(patternH2title,str);
            secTitles.add(Jsoup.parse(title).text());
            String items=Jsoup.parse(str).text();
            secItems.add(items);
        }
        String[]titles=getNormalArrays(secTitles);
        String[]items=getNormalArrays(secItems);
        list.setAdapter(new CustomListAdapterSubPage_TitleText(this,titles,items));
    }

    public void bellTimes(){

    }

    public void calendar(){
        Elements eventsElms=(doc.select("div.event_details"));
        String[]patternDate={"<div class=\"dates\">","</div>"};
        String[]patternEvent={"<div class=\"name\">","</div>"};
        List<String>dates=new ArrayList<>();
        List<String>events=new ArrayList<>();
        for(Element elm:eventsElms){
            String str=elm.toString().replace("\n","");
            dates.add(MainActivity.getFromPatternStatic(patternDate,str));
            events.add(MainActivity.getFromPatternStatic(patternEvent,str));
        }
        list.setAdapter(new CustomListAdapterSubPage_TitleText(this,getNormalArrays(dates),getNormalArrays(events)));
    }
}
/*
 GridLayout gv=(GridLayout)findViewById(R.id.subLayout);
 TextView tv=new TextView(context);
 tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
 tv.setText(doc.text());
 GridLayout.LayoutParams params=new GridLayout.LayoutParams(GridLayout.spec(1),GridLayout.spec(0));
 tv.setLayoutParams(params);
 gv.addView(tv);
 */