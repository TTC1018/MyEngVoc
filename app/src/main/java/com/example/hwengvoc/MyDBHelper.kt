package com.example.hwengvoc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class MyDBHelper(val context:Context):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "dictionary.db"
        val DB_VERSION = 1
        var TABLE_NAMES = mutableListOf<String>()
        val VID = "vid"
        val WORD = "word"
        val MEAN = "meaning"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        println("MyDBHelper onCreate")

        for(TABLE_NAME in TABLE_NAMES){
            val create_table = "create table if not exists '${TABLE_NAME}'("+
                    "$VID integer primary key autoincrement, "+
                    "$WORD text, "+
                    "$MEAN text);"
            db!!.execSQL(create_table)
        }
    }

    fun readDefaultDic() {
        val activity = context as MainActivity
        val myDicFragment = activity.supportFragmentManager.findFragmentByTag("mydic") as MyDicFragment?

        val scan = Scanner(context.resources.openRawResource(R.raw.words_small))
        var count = 0

        while(scan.hasNextLine()){
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            insertVoc(VocData(0, word, meaning), TABLE_NAMES.get(0))
            count++
        }
        myDicFragment!!.dicList.add(DicData(TABLE_NAMES.get(0).replace("_", " "), count))
        myDicFragment.adapter!!.notifyDataSetChanged()
        scan.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        for(TABLE_NAME in TABLE_NAMES){
            db!!.execSQL("drop table if exists '$TABLE_NAME';")
            onCreate(db)
        }
    }

    private fun findVoc(word: String, TABLE_NAME: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $WORD='$word';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        cursor.close()
        db.close()
        return flag
    }

    fun findDic(TABLE_NAME: String):LinkedList<VocData>{
        var vocList = LinkedList<VocData>()
        val strsql = "select * from '$TABLE_NAME';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0

        if(flag){
            cursor.moveToFirst()
            do {
                vocList.add(VocData(cursor.getInt(cursor.getColumnIndex("vid")), cursor.getString(cursor.getColumnIndex("word")), cursor.getString(cursor.getColumnIndex("meaning"))))
            }while(cursor.moveToNext())
        }
        return vocList
    }

    fun insertVoc(voc:VocData, TABLE_NAME:String):Boolean{
        if(findVoc(voc.word, TABLE_NAME)) //중복 삽입 방지
            return false

        val values = ContentValues()
        values.put(WORD, voc.word)
        values.put(MEAN, voc.meaning)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    fun deleteVoc(word: String, TABLE_NAME: String): Boolean {
        val delSql = "select * from '$TABLE_NAME' where $WORD='$word';"
        val db = writableDatabase
        val cursor = db.rawQuery(delSql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$WORD=?", arrayOf(word))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun createTable(TABLE_NAME: String){
        val newTable = TABLE_NAME.replace(" ", "_")
        val db = writableDatabase
        val create_table = "create table if not exists '${newTable}'("+
                "$VID integer primary key autoincrement, "+
                "$WORD text, "+
                "$MEAN text);"
        db!!.execSQL(create_table)
        db.close()

        TABLE_NAMES.add(newTable)
    }

    fun updateTable(OLD_NAME:String, NEW_NAME: String){
        val oldName = OLD_NAME.replace(" ", "_")
        val newName = NEW_NAME.replace(" ", "_")
        val strsql = "alter table '$oldName' rename to '$newName';"
        val db = writableDatabase
        db.execSQL(strsql)
        db.close()

        for(i:Int in 0..TABLE_NAMES.size-1){
            if(TABLE_NAMES.get(i).equals(oldName)){
                TABLE_NAMES.set(i, newName)
            }
        }
    }

    fun deleteTable(TABLE_NAME: String){
        val targetName = TABLE_NAME.replace(" ", "_")
        val strsql = "drop table '$targetName';"
        val db = writableDatabase
        db.execSQL(strsql)
        db.close()

        for(i:Int in 0 until TABLE_NAMES.size){
            if(TABLE_NAMES.get(i).equals(targetName)){
                TABLE_NAMES.removeAt(i)
                break
            }
        }
    }

    fun countVoc(TABLE_NAME: String):Int{
        val strsql = "select * from '$TABLE_NAME';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        var count = 0
        while(cursor.moveToNext()){
            count++
        }
        db.close()
        cursor.close()
        return count
    }

    fun initalizeDB(){
        val search_table = "select * from sqlite_master where (not (name='android_metadata' or name like '%sqlite_sequence%')) and type='table';"
        val db = readableDatabase
        val c = db!!.rawQuery(search_table, null)
        if(c.moveToFirst()){
            while(!c.isAfterLast){
                TABLE_NAMES.add(c.getString(c.getColumnIndex("name")))
                c.moveToNext()
            }
        }
        db.close()
        c.close()
    }


}