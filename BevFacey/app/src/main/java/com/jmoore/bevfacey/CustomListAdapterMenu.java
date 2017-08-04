package com.jmoore.bevfacey;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

//      0                   1                       2                   3               4
//R.drawable.about  R.drawable.eteachers  R.drawable.programs  R.drawable.parents  R.drawable.students
//          5                   6                       7
// R.drawable.athletics  R.drawable.guidance  R.drawable.sustainability

class CustomListAdapterMenu extends ArrayAdapter<String>{//This class is the list of Information
    private final Activity context;
    private final String[]menuText;

    CustomListAdapterMenu(Activity context,String[]menuText,String[]menuText2){
        super(context,R.layout.mylistmenu,menuText2);
        this.context=context;
        this.menuText=menuText;
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

    @NonNull
    public View getView(int position,View view,@NonNull ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.mylistmenu,null,true);

        TextView tv=rowView.findViewById(R.id.menuText);
        tv.setTypeface(MainActivity.typefaceMenuItems);
        tv.setText(menuText[position]);
        tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TextView tv2=(TextView)view;
                String title=tv2.getText().toString();
                if(title.equals(MainActivity.menuItemTitles[0])){
                    ListView lv=rowView.findViewById(R.id.subMenuItem);
                    if(lv.getVisibility()==View.GONE) {
                        int height = tv2.getHeight();
                        CustomListAdapterSubMenu clasm = new CustomListAdapterSubMenu(context, getNormalArrays(MainActivity.subAboutTitles), getNormalArrays(MainActivity.subAboutText));
                        lv.setAdapter(clasm);
                        lv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height * MainActivity.subAboutLength));
                        lv.setVisibility(View.VISIBLE);
                    }else{
                        lv.setVisibility(View.GONE);
                    }
                }else if(title.equals(MainActivity.menuItemTitles[1])){

                }else if(title.equals(MainActivity.menuItemTitles[2])){

                }else if(title.equals(MainActivity.menuItemTitles[3])){
                    //Parents
                }else if(title.equals(MainActivity.menuItemTitles[4])){
                    //Students
                }else if(title.equals(MainActivity.menuItemTitles[5])){
                    //Athletics
                }else if(title.equals(MainActivity.menuItemTitles[6])){
                    //Guidance
                }else if(title.equals(MainActivity.menuItemTitles[7])){
                    //Sustainability
                }
            }
        });
        return rowView;
    }
}