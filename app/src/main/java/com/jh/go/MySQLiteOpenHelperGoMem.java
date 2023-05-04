package com.jh.go;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelperGoMem extends SQLiteOpenHelper {
    public MySQLiteOpenHelperGoMem(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table member (" +
                "idx integer primary key autoincrement, " +
                "member_id text, " +
                "member_pw text, " +
                "member_name text, " +
                "member_hp text, " +
                "group_code text, " +
                "group_name text, " +
                "group_leader INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists member";
        db.execSQL(sql);
        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정
    }
}
