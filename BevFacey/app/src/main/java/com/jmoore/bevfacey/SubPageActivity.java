package com.jmoore.bevfacey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SubPageActivity extends AppCompatActivity {

    Document doc=MainActivity.docSub;
    Intent intent;
    Context context;
    GridLayout grid;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            list.setDivider(list.getBackground());
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
        System.out.println(url);
        if(url.toLowerCase().contains("bell-times")){
            bellTimes();
        }else if(url.toLowerCase().contains("calendar")){
            calendar();
        }
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
            String str=elm.toString();
            str=str.replace("\n","");
            String date=MainActivity.getFromPatternStatic(patternDate,str);
            String event=MainActivity.getFromPatternStatic(patternEvent,str);
            dates.add(date);
            events.add(event);
        }
        CustomListAdapterSubPage_TitleText clasm=new CustomListAdapterSubPage_TitleText(this,getNormalArrays(dates),getNormalArrays(events));
        list.setAdapter(clasm);
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