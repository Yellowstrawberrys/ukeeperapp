package org.ukeeper.ukeeper.db

import android.content.ContentValues
import android.content.Context

class DataManager(context: Context) {

    private val dbHelper = DBHelper(context)

    public fun write(date:String, time:Float) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("date", date)
            put("min", time)
        }

        db?.insert("times", null, values)
    }

    public fun read(date:String): MutableList<Float>? {
        val db = dbHelper.readableDatabase

        val cur = db.rawQuery("SELECT `time` FROM `times` WHERE `date`=\"%s\" ORDER BY `time` DESC;".format(date), null)
        val times = mutableListOf<Float>()
        with(cur) {
            while (moveToNext()) {
                times.add(cur.getFloat(0))
                if(times.size > 2) break
            }
        }
        cur.close()

        return if(times.size < 3) null
        else times
    }
}