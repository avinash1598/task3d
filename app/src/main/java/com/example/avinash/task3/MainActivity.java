package com.example.avinash.task3;

//************************ long press on the list item to edit the information ********************************

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    public database obj;
    TextView tv1, tv2;
    EditText et1, et2;TextView et3;
    static String status="null";
    public static int length = 5;
    public static int idpos=0;
    public static String lnam, lnum;
    static byte[] limg;
    public static boolean update=false;

    static  Boolean flag=true;
    public static Object lock = new Object();
    public static Object lock2 = new Object();
    public static Uri imageuri;

    public Handler h;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "data";

    public static int start = 0;
    public static String name[] = {"Rupam", "Avinash", "Balinda", "Home", "Papa"};
    public static String number[] = {"9176255830", "7845727410", "8148274821", "8527123530", "9936433657"};
    public static int img[] = {(R.mipmap.ic_launcher3), (R.mipmap.ic_launcher3),(R.mipmap.ic_launcher3),(R.mipmap.ic_launcher3),(R.mipmap.ic_launcher3)};
    public static ArrayList<String> naml=new ArrayList<String>(),numl=new ArrayList<String>();

    public static contactlist cl;
    public static Cursor c;

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    databaseinfo dbin;
    ArrayList<databaseinfo> list=new ArrayList<databaseinfo>();
    ArrayList<byte[]> imglist=new ArrayList<byte[]>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        //Toast.makeText(getApplicationContext(), " " + start, Toast.LENGTH_SHORT).show();
        length = 5;
        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);
        et3=(TextView)findViewById(R.id.textView5);
        final EditText sv=(EditText)findViewById(R.id.editText3);
        sv.setHint("SEARCH BY NAME/NUMBER");

//*************************************** edittext to search contact ******************************************
        sv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String txt=sv.getText().toString().toLowerCase(Locale.ENGLISH);
                cl.Filter(txt);
            }
        });
//**************************************************************************************************************
        obj = new database(this);
        final opr op = new opr();
        Toast.makeText(this, "" + obj.row(), Toast.LENGTH_SHORT).show();
   //************************************ Adding to arraylist ******************************************************************
        for(int k=0;k<5;k++)
        naml.add(k,name[k]);

        for(int k=0;k<5;k++)
            numl.add(k,number[k]);

        for(int k=0;k<5;k++){
            Uri uri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(img[k]) + '/' + getResources().getResourceTypeName(img[k]) + '/' + getResources().getResourceEntryName(img[k]));
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
           // Bitmap b= BitmapFactory.decodeFile(uri.getPath());
           // b.compress(Bitmap.CompressFormat.PNG,10,stream);
            //Log.d("SIIIIIIIIIIZE", "" + b.getByteCount());*/
            Bitmap b=BitmapFactory.decodeResource(getResources(),img[k]);
            b.compress(Bitmap.CompressFormat.PNG,10,stream);
            Log.d("SIIIIIIIIIIZE", "" + b.getByteCount());
            byte[] by=stream.toByteArray();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imglist.add(k, by);
        }
//*************************************to save the start and list length value *******************************************************************
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//*********************** to handle event when database is updated ***********************************************************************************************

         h=new Handler(){
            public void handleMessage(android.os.Message m){
                if(update){
                    Toast.makeText(MainActivity.this, "handledatabase" + "" + length, Toast.LENGTH_SHORT).show();
                    int id = 1;
                    naml.clear();
                    numl.clear();
                    imglist.clear();
                   for (id = 1; id <=length; id++) {
                        Log.d("pos", "" + id);
                        c = obj.getinfo(id);
                        c.moveToFirst();

                       naml.add(id - 1, c.getString(c.getColumnIndex(database.pname)));
                       numl.add(id-1,c.getString(c.getColumnIndex(database.pnum)));
                       imglist.add(id-1, (c.getBlob(c.getColumnIndex(database.pimg))));
                    }

                    list.clear();
                    for(int i=0;i<length;i++)

                    {dbin=new databaseinfo(naml.get(i),numl.get(i),imglist.get(i));
                        list.add(dbin);}
                    cl = new contactlist(MainActivity.this, list);
                    lv.setAdapter(cl);


                }
            }
         };
//************************************long press listview to change entries **********************************************************************************
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                idpos = position + 1;
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                final View v = li.inflate(R.layout.edit_details, null, false);
                updateimage();    // function to load image from gallary
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final EditText et = (EditText) v.findViewById(R.id.editText);
                final EditText num = (EditText) v.findViewById(R.id.editText2);
                alertDialogBuilder.setView(v);

                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        lnam = et.getText().toString();
                        Toast.makeText(getApplicationContext(), lnam, Toast.LENGTH_SHORT).show();
                        lnum = num.getText().toString();
                        Toast.makeText(getApplicationContext(), lnum, Toast.LENGTH_SHORT).show();
                        status = "update";
                        Thread.State s = op.getState();
                        Toast.makeText(MainActivity.this, "ThreadState" + ":" + s.toString(), Toast.LENGTH_SHORT).show();

                        if (s.toString().matches("NEW")) op.start();
                        else op.play();
                    }

                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;

            }
        });
//*******************************************************************************************************************
        try {
            start = sharedpreferences.getInt("start", 0);
            String s = sharedpreferences.getString("leng", null);
            length = Integer.parseInt(s);
            Log.d("status", "inside");
        } catch (Exception e) {
            Toast.makeText(this, "CURRENTLY NO SAVED VALUE", Toast.LENGTH_SHORT).show();
        }
//****************************** to display listview when app is opened *******************************************
        if (start == 0) {

            list.clear();
            for(int i=0;i<length;i++)
            {dbin=new databaseinfo(naml.get(i),numl.get(i),imglist.get(i));
            list.add(dbin);}
            cl = new contactlist(this, list);
        }
        else
        {
            Toast.makeText(this, "database" + "" + length, Toast.LENGTH_SHORT).show();
            int id = 1;
            naml.clear();
            numl.clear();
            imglist.clear();

            for (id = 1; id <= length; id++) {
                Log.d("pos", "" + id);
                c = obj.getinfo(id);
                c.moveToFirst();

                naml.add(id - 1, c.getString(c.getColumnIndex(database.pname)));
                numl.add(id - 1, c.getString(c.getColumnIndex(database.pnum)));
                imglist.add(id-1,c.getBlob(c.getColumnIndex(database.pimg)));
            }

            cl=null;
            list.clear();
            for(int i=0;i<length;i++)
            {dbin=new databaseinfo(naml.get(i),numl.get(i),imglist.get(i));
                list.add(dbin);}
            cl = new contactlist(this, list);

        }


        lv.setAdapter(cl);
        // obj.drop();
        if (start == 0) op.start();
        Toast.makeText(this, "start" + start, Toast.LENGTH_SHORT).show();

    }


    public class opr extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                if (start == 0) {
                    try {
                        for (int i = 0; i < 5; i++) {
                            Log.d("pos", String.valueOf(i));
                            if (obj.insertContcts(name[i], number[i], imglist.get(i))) {
                                Log.d("Result", "Inserted");
                                Thread.sleep(2000);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("ërror", e.toString());

                    }
                    Log.d("row", "" + obj.row());
                    start = 1;
                    try {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("start", start);
                        editor.putString("leng", "" + length);
                        editor.apply();
                    } catch (Exception e) {
                        Log.d("error", e.toString());
                    }
                }

               else  if(status.matches("update")){
                    if(obj.updateContact(idpos,lnam,lnum,limg)){
                        update=true;
                        Log.d("update", "true");
                        Bundle b = new Bundle();
                        b.putInt("index", 1);
                        Message m = (h).obtainMessage();
                        m.setData(b);
                        h.sendMessage(m);


                        status="null";
                    }
                }

            }

        }
        public void check() {
            synchronized (lock2) {
                while (!flag) {
                    try {lock2.wait();
                    } catch (Exception e) {}
                }
            }
        }

        public void pause() {
            flag = false;
        }

        public void play() {
            flag=true;
            synchronized(lock2) {
                lock2.notify();
            }
        }
    }
//******************************to add new entry ************************************
    public void add(View v){
        contactlist c=(contactlist)lv.getAdapter();
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
         v = li.inflate(R.layout.edit_details, null,false);
        updateimage();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final EditText et = (EditText) v.findViewById(R.id.editText);
        final EditText num = (EditText) v.findViewById(R.id.editText2);
        //final EditText img1 = (EditText) v.findViewById(R.id.editText4);

        alertDialogBuilder.setView(v);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                lnam = et.getText().toString();
                Toast.makeText(getApplicationContext(), lnam, Toast.LENGTH_SHORT).show();
                lnum = num.getText().toString();
                Toast.makeText(getApplicationContext(), lnum, Toast.LENGTH_SHORT).show();
                naml.add(length, lnam);
                numl.add(length,lnum);
                imglist.add(length,limg);
                cl=null;
                Log.d("status", "inside");
                length++;
                list.clear();
                for(int i=0;i<length;i++)
                {dbin=new databaseinfo(naml.get(i),numl.get(i),imglist.get(i));
                    list.add(dbin);}
                cl = new contactlist(MainActivity.this, list);

                lv.setAdapter(cl);
                if(obj.insertContcts(lnam, lnum, limg)){
                    Toast.makeText(MainActivity.this,"inserted",Toast.LENGTH_LONG).show();
                }

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("leng", "" + length);
                editor.apply();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
//********************************load image **********************************************************
    public void updateimage(){

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    String path = getPathFromURI(selectedImageUri);

                  Log.i(TAG, selectedImageUri.getPath());

                    Bitmap b;
                    try {ByteArrayOutputStream stream=new ByteArrayOutputStream();
                        Matrix m=new Matrix();
                        b = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        m.postRotate(90);
                        Bitmap bs2=Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true );
                        Bitmap bs=Bitmap.createScaledBitmap(bs2,150,200,false);
                        BitmapFactory.Options o=new BitmapFactory.Options();
                        bs.compress(Bitmap.CompressFormat.JPEG,10,stream);
                        Log.d("SIIIIIIIIIIZE", "" + bs.getByteCount());
                        byte[] by=stream.toByteArray();
                        limg=by;
                    } catch (IOException e) {
                        Log.d("errrrrrrrrrrrrrrrrrrr", e.toString());

                    }
                    imageuri=selectedImageUri;
                }
            }
        }
    }


    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    }
