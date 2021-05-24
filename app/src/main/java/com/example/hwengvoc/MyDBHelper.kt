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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyDBHelper(val context:Context):SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        val DB_NAME = "dictionary.db"
        val DB_VERSION = 1
        var TABLE_NAMES = mutableListOf<String>("기본_단어장")
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

        //기본 단어장 추가한 경우
        if(checkInitialize(TABLE_NAMES.get(0))){
            //중복으로 DicList 버튼 생기는거 방지
            if(myDicFragment!!.dicList.size == 0){
                myDicFragment!!.dicList.add(DicData(TABLE_NAMES.get(0).replace("_", " "), countVoc(
                    TABLE_NAMES.get(0))))
                myDicFragment!!.adapter!!.notifyDataSetChanged()
            }
            return
        }

        val scan = Scanner(context.resources.openRawResource(R.raw.words_small))
        var count = 0

        while(scan.hasNextLine()){
            val word = scan.nextLine()
            val meaning = scan.nextLine()
            insertVoc(VocData(0, word, meaning), TABLE_NAMES.get(0))
            count++
        }
        myDicFragment!!.dicList.add(DicData(TABLE_NAMES.get(0).replace("_", " "), count))
        myDicFragment!!.adapter!!.notifyDataSetChanged()
        scan.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        for(TABLE_NAME in TABLE_NAMES){
            db!!.execSQL("drop table if exists '$TABLE_NAME';")
            onCreate(db)
        }
    }

//    fun getAllRecord(TABLE_NAME:String){
//        val strsql = "select * from $TABLE_NAME;"
//        val db = readableDatabase
//        val cursor = db.rawQuery(strsql, null)
//        showRecord(cursor)
//        cursor.close()
//        db.close()
//    }
//
//    private fun showRecord(cursor: Cursor){
//        cursor.moveToFirst()
//        val attrcount = cursor.columnCount
//        val activity = context as MainActivity
//        val selectedFrag = activity.binding.bottomNavi.selectedItemId
//        when(selectedFrag){
//            R.id.myDicBtn->{
//                val myDicFragment = activity.supportFragmentManager.findFragmentById(R.id.fragContainer) as MyDicFragment
//                myDicFragment!!.dicList
//            }
//        }
//
//        //타이틀 만들기
//        val tablerow = TableRow(activity)
//        val rowParam = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
//        tablerow.layoutParams = rowParam
//        val viewParam = TableRow.LayoutParams(0, 100, 1f)
//        for(i in 0 until attrcount){
//            val textView = TextView(activity)
//            textView.layoutParams = viewParam
//            textView.text = cursor.getColumnName(i)
//            textView.setBackgroundColor(Color.LTGRAY)
//            textView.gravity= Gravity.CENTER
//            tablerow.addView(textView)
//        }
//        activity.binding.tableLayout.addView(tablerow)
//        if(cursor.count==0) return
//        // 레코드 추가하기
//        do{
//            val row = TableRow(activity)
//            row.layoutParams = rowParam
//            row.setOnClickListener {
//                for(i in 0 until attrcount){
//                    val textView = row.getChildAt(i) as TextView
//                    when(textView.tag){
//                        0->activity.binding.pIdEdit.setText(textView.text)
//                        1->activity.binding.pNameEdit.setText(textView.text)
//                        2->activity.binding.pQuantityEdit.setText(textView.text)
//                    }
//                }
//            }
//            for(i in 0 until attrcount){
//                val textView = TextView(activity)
//                textView.tag = i
//                textView.layoutParams = viewParam
//                textView.text = cursor.getString(i)
//                textView.textSize = 13.0f
//                textView.gravity= Gravity.CENTER
//                row.addView(textView)
//            }
//            activity.binding.tableLayout.addView(row)
//        }while(cursor.moveToNext())
//    }

//    fun findVoc(word: String, TABLE_NAME: String): Boolean {
//        val strsql = "select * from $TABLE_NAME where $WORD='$word';"
//        val db = readableDatabase
//        val cursor = db.rawQuery(strsql, null)
//        val flag = cursor.count!=0
//        showRecord(cursor)
//        cursor.close()
//        db.close()
//        return flag
//    }
//
//    fun findVoc2(meaning: String, TABLE_NAME: String): Boolean {
//        val strsql = "select * from $TABLE_NAME where $MEAN = '$meaning';"
//        val db = readableDatabase
//        val cursor = db.rawQuery(strsql, null)
//        val flag = cursor.count!=0
//        showRecord(cursor)
//        cursor.close()
//        db.close()
//        return flag
//    }
//    fun updateVoc(voc: VocData, TABLE_NAME: String): Boolean {
//        val vid = product.pId
//        val strsql = "select * from $TABLE_NAME where $VID='$vid';"
//        val db = writableDatabase
//        val cursor = db.rawQuery(strsql, null)
//        val flag = cursor.count!=0
//        if(flag){
//            cursor.moveToFirst()
//            val values = ContentValues()
//            values.put(WORD, voc.word)
//            values.put(MEAN, voc.meaning)
//            db.update(TABLE_NAME, values,"$VID=?", arrayOf(vid.toString()))
//        }
//        cursor.close()
//        db.close()
//        return flag
//    }

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
        val values = ContentValues()
        values.put(WORD, voc.word)
        values.put(MEAN, voc.meaning)
        val db = writableDatabase
        val flag = db.insert(TABLE_NAME, null, values)>0
        db.close()
        return flag
    }

    fun deleteVoc(vid: String, TABLE_NAME: String): Boolean {
        val strsql = "select * from '$TABLE_NAME' where $VID='$vid';"
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

    fun checkInitialize(TABLE_NAME: String):Boolean{
        val strsql = "select * from '$TABLE_NAME';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val flag = cursor.count!=0
        return flag
    }

    fun countVoc(TABLE_NAME: String):Int{
        val strsql = "select * from '$TABLE_NAME';"
        val db = readableDatabase
        val cursor = db.rawQuery(strsql, null)
        var count = 0
        while(cursor.moveToNext()){
            count++
        }
        return count
    }



}