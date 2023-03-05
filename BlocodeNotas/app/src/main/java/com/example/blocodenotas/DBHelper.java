package com.example.blocodenotas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bancodedadosnotas.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;

    public DBHelper (Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS nota " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, descricao TEXT);";
                db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Example","Upgrading database, this will drop table and recreate.");
        db.execSQL("DROP TABLE IF EXISTS nota");
        onCreate(db);
    }

    public void insert(String descricao){
        String sql = "INSERT INTO nota (descricao) VALUES ('"+ descricao +"')";
        db.execSQL(sql);
    }
    public void delete(String descricao){
        String sql = "DELETE FROM nota WHERE (descricao='"+ descricao + "')";
        db.execSQL(sql);

    }
    public List<String> getFromDB(){
        List<String> list = new ArrayList<String>();
        String sql = "SELECT descricao FROM nota";
        try{
            Cursor cursor = this.db.rawQuery(sql,null);
            int nregistros = cursor.getCount();
            if (nregistros != 0){
                cursor.moveToFirst();
                do {
                    String descricao = new String(cursor.getString(0));
                    list.add(descricao);
                } while (cursor.moveToNext());
                if (cursor != null && !cursor.isClosed()){
                    cursor.close();
                    return list;
                }else
                    return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
