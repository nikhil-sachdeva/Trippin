package com.example.nikhil.roadsafety;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class AccidentDatabaseHelper extends SQLiteOpenHelper {

    static final private String DB_NAME = "Accident";
    static final public String DB_TABLE = "AccidentProne";
    static final private int DB_VER = 1;


    Context ctx;
    SQLiteDatabase myDB;

    public AccidentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+DB_TABLE+" (_id integer primary key autoincrement,name text,lat real,lang real,simple_acc integer,fatal_acc integer,danger_pts real,zone text,danger_idx real,distance real);");
        Log.i("Database", "Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+DB_TABLE);
        onCreate(db);
    }

    public void insertData(String name, double lat, double lang, int simple, int fatal, double danger_pts, String zone, double danger_idx,double distance){
        myDB = getWritableDatabase();

        myDB.execSQL("insert into "+DB_TABLE+" (name,lat,lang,simple_acc,fatal_acc,danger_pts,zone,danger_idx,distance) values('"+name+"','"+lat+"','"+lang+"','"+simple+"','"+fatal+"','"+danger_pts+"','"+zone+"','"+danger_idx+"','"+distance+"');");

    }

    public Cursor getAll(){
        myDB = getReadableDatabase();
        Cursor cr = myDB.rawQuery("select* from "+DB_TABLE+" order by distance", null);


        return cr;
    }

    public double getZoneIndex(String zone){
        Log.i("Messages", "getZoneIndex() called");
        myDB = getReadableDatabase();
        Cursor cr = myDB.rawQuery("select zone,avg(danger_idx),count(name) from "+DB_TABLE+" group by zone", null);
        while (cr.moveToNext()){
            if (cr.getString(cr.getColumnIndex("zone")).equals(zone)){
                Log.i("Messages", "Count: "+cr.getString(0)+" "+cr.getInt(2));
                return cr.getDouble(1)*(cr.getDouble(2)/(145-cr.getDouble(2)))*10;
            }
        }

        return 0;
    }

    public void showAll(){
        myDB = getReadableDatabase();
        Cursor cr = myDB.rawQuery("select* from "+DB_TABLE, null);
        StringBuilder str = new StringBuilder();

        while(cr.moveToNext()){
            String s1 = cr.getString(1);
            String s2 = cr.getString(2);
            String s3 = cr.getString(3);
            String s4 = cr.getString(4);
            String s5 = cr.getString(5);

            str.append(s1+" "+s2+" "+s3+" "+s4+" "+s5+"\n");
        }
        Toast.makeText(ctx, str.toString(), Toast.LENGTH_LONG).show();

    }

    public void dropTable(){
        myDB.execSQL("drop table if exists "+DB_TABLE);
        onCreate(myDB);
    }

    public int getZonewiseCount(String zone){
        myDB = getReadableDatabase();
        Cursor cr = myDB.rawQuery("select zone,count(name) from "+DB_TABLE+" group by zone", null);
        while (cr.moveToNext()){
            if (cr.getString(cr.getColumnIndex("zone")).equals(zone)){
                return cr.getInt(1);
            }
        }

        return 0;
    }


}
