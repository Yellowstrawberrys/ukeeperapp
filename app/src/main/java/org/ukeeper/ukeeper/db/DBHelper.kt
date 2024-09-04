package org.ukeeper.ukeeper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context:Context)  : SQLiteOpenHelper(context, "ukeeper", null, 1){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `times` (
                `date` TEXT NOT NULL,
                `time` REAL NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}
