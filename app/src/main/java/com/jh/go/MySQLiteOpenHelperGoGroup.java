package com.jh.go;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLiteOpenHelperGoGroup extends SQLiteOpenHelper {
    public MySQLiteOpenHelperGoGroup(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table groupChat (" +
                "idx integer primary key autoincrement, " +
                "group_code text, " +
                "group_name text, " +
                "member_id text, " +
                "member_name text, " +
                "message text, " +
                "time text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists friends";
        db.execSQL(sql);
        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정
    }
}
