package com.example.avinash.task3;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AVINASH on 6/28/2016.
 */
public class contactlist extends BaseAdapter  {
    Context context;
    MainActivity m=new MainActivity();
    private static LayoutInflater inflater=null;

    public ArrayList<databaseinfo> arrlist=new ArrayList<databaseinfo>();
    public ArrayList<databaseinfo> temparrlist=new ArrayList<databaseinfo>();

    public contactlist(MainActivity mainActivity, ArrayList<databaseinfo> list) {
        context=mainActivity;
        this.arrlist=list;
        this.temparrlist.addAll(list);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrlist.size();
    }

    @Override
    public Object getItem(int position) {
        return arrlist.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public ArrayList Filter(String text) {
       String txt=text.toLowerCase(Locale.getDefault());
       arrlist.clear();

        if(txt.length()==0){
            arrlist.addAll(temparrlist);
        }
        else {
            for(databaseinfo dbi:temparrlist){
                if(dbi.getName().toLowerCase(Locale.getDefault()).contains(text)){
                    arrlist.add(dbi);
                    Log.d("size","12");
                }

                if(dbi.getNum().contains(txt)){
                    arrlist.add(dbi);
                }

            }notifyDataSetChanged();
        }
        return arrlist;
    }


    public class Holder{
        TextView numh,naam;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View contactView;
        contactView=inflater.inflate(R.layout.contact,null,false);
        holder.naam=(TextView)contactView.findViewById(R.id.textView);
        holder.numh=(TextView)contactView.findViewById(R.id.textView1);
        holder.img=(ImageView)contactView.findViewById(R.id.imageView1);
        holder.naam.setText(arrlist.get(position).getName().toString());
        holder.numh.setText(arrlist.get(position).getNum().toString());
        Bitmap b=BitmapFactory.decodeByteArray(arrlist.get(position).getImg(),0,arrlist.get(position).getImg().length);
        holder.img.setImageBitmap(b);
        return contactView;
    }




}
