package com.example.hwengvoc

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView

class MyDBHelper(val context:Context):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "dictionary.db"
        val DB_VERSION = 1
        val VID = "vid"
        val WORD = "word"
        val MEAN = "meaning"
    }

    fun getAllRecord(TABLE_NAME:String){
        val strsql = "select * from $TABLE_NAME;"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        showRecord(cursor)
        cursor.close()
        db.close()
    }

    private fun showRecord(cursor: Cursor){
        cursor.moveToFirst()
        val attrcount = cursor.columnCount
        val activity = context as MainActivity
        activity.binding.tableLayout.removeAllViewsInLayout()

        //타이틀 만들기
        val tablerow = TableRow(activity)
        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        tablerow.layoutParams = rowParam
        val viewParam = TableRow.LayoutParams(0, 100, 1f)
        for(i in 0 until attrcount){
            val textView = TextView(activity)
            textView.layoutParams = viewParam
            textView.text = cursor.getColumnName(i)
            textView.setBackgroundColor(Color.LTGRAY)
            textView.gravity= Gravity.CENTER
            tablerow.addView(textView)
        }
        activity.binding.tableLayout.addView(tablerow)
        if(cursor.count==0) return
        // 레코드 추가하기
        do{
            val row = TableRow(activity)
            row.layoutParams = rowParam
            row.setOnClickListener {
                for(i in 0 until attrcount){
                    val textView = row.getChildAt(i) as TextView
                    when(textView.tag){
                        0->activity.binding.pIdEdit.setText(textView.text)
                        1->activity.binding.pNameEdit.setText(textView.text)
                        2->activity.binding.pQuantityEdit.setText(textView.text)
                    }
                }
            }
            for(i in 0 until attrcount){
                val textView = TextView(activity)
                textView.tag = i
                textView.layoutParams = viewParam
                textView.text = cursor.getString(i)
                textView.textSize = 13.0f
                textView.gravity= Gravity.CENTER
                row.addView(textView)
            }
            activity.binding.tableLayout.addView(row)
        }while(cursor.moveToNext())
    }

    fun insertVoc(voc:VocData, TABLE_NAME:String):Boolean{
        val values = ContentValues()
        values.put(WORD, voc.word)
        values.put(MEAN, voc.meaning)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists DEFAULT("+
                "$VID integer primary key autoincrement, "+
                "$WORD text, "+
                "$MEAN text);"
        db!!.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists $TABLE_NAME;"
        db!!.execSQL(drop_table)
        onCreate(db)
    }

    fun findVoc(word: String, TABLE_NAME: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $WORD='$word';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }

    fun findVoc2(meaning: String, TABLE_NAME: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $MEAN = '$meaning';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        showRecord(cursor)
        cursor.close()
        db.close()
        return flag
    }

    fun deleteVoc(vid: String, TABLE_NAME: String): Boolean {
        val strsql = "select * from $TABLE_NAME where $VID='$vid';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            db.delete(TABLE_NAME, "$VID=?", arrayOf(vid))
        }
        cursor.close()
        db.close()
        return flag
    }

    fun updateVoc(voc: VocData, TABLE_NAME: String): Boolean {
        val vid = product.pId
        val strsql = "select * from $TABLE_NAME where $VID='$vid';"
        val db = writableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        if(flag){
            cursor.moveToFirst()
            val values = ContentValues()
            values.put(WORD, voc.word)
            values.put(MEAN, voc.meaning)
            db.update(TABLE_NAME, values,"$VID=?", arrayOf(vid.toString()))
        }
        cursor.close()
        db.close()
        return flag
    }



}