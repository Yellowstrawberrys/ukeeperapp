package org.ukeeper.ukeeper.db

import android.content.ContentValues
import android.content.Context

class DataManager(context: Context) {

    private val dbHelper = DBHelper(context)

    public fun write(date:String, time:Int) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("date", date)
            put("time", time)
        }

        db?.insert("times", null, values)
    }

    public fun read(date:String): MutableList<Int>? {
        val db = dbHelper.readableDatabase

        val cur = db.rawQuery("SELECT * `time` FROM `times` WHERE `date`=\"%s\" ORDER BY `time` DESC;".format(date), null)
        val times = mutableListOf<Int>()
        with(cur) {
            while (moveToNext()) {
                times.add(cur.getInt(1))
                if(times.size > 3) break
            }
        }
        cur.close()

        return if(times.size < 3) null
        else times
    }
}