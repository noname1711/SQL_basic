package com.example.ngnhpsql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyHelper(context: Context) :SQLiteOpenHelper(context,"TUHOCDB",null,1) {
    //ctrl+i
    override fun onCreate(db: SQLiteDatabase?) {
        //tao table,column (cot)
        db?.execSQL("create table tuhoc(_id integer primary key autoincrement,user text,email text)")
        //them data mac dinh vao co so du lieu
        db?.execSQL("insert into tuhoc(user,email) values ('mot','mot@gmail.com')")
        db?.execSQL("insert into tuhoc(user,email) values ('hai','hai@gmail.com')")
        db?.execSQL("insert into tuhoc(user,email) values ('ba','ba@gmail.com')")
        db?.execSQL("insert into tuhoc(user,email) values ('hung','hung@gmail.com')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}