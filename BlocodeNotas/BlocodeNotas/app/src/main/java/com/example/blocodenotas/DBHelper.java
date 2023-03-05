package com.example.blocodenotas;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bancodedados.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "contato";

    private Context context;
    private SQLiteDatabase db;

    private SQLiteStatement insertStmt;
    private static final String INSERT = "insert into " + TABLE_NAME + "(nome,endereco,empresa) values (?,?,?)";

    public DBHelper (Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        db = getWritableDatabase();
        insertStmt = db.compileStatement(INSERT);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, endereco TEXT, empresa TEXT);";
                db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("Example","Upgrading database, this will drop table and recreate.");
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    public long insert (String nome, String endereco,String empresa){
        this.insertStmt.bindString(1,nome);
        this.insertStmt.bindString(2,endereco + " ");
        this.insertStmt.bindString(3,empresa + " ");
        return this.insertStmt.executeInsert();

    }
    public void deleteAll(){
        this.db.delete(TABLE_NAME,null,null);
    }
    public List<Contato> queryGetAll(){
        List<Contato> list = new ArrayList<Contato>();
        try{
            Cursor cursor = this.db.query(TABLE_NAME,
                    new String [] {"nome","endereco","empresa"},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            int nregistros = cursor.getCount();
            if (nregistros!=0){
                cursor.moveToFirst();
                do{
                    Contato contato = new Contato (cursor.getString(0),
                            cursor.getString(1), cursor.getString(2));
                    list.add(contato);
                }while (cursor.moveToNext());
                if (cursor!=null && !cursor.isClosed()){
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
