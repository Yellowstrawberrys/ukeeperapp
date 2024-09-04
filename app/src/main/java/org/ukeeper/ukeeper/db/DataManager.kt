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

    public fun getLatest(date: String): Float? {
        val db = dbHelper.readableDatabase

        val cur = db.rawQuery("SELECT `time` FROM `times` WHERE `date`=\"%s\" ORDER BY `time` DESC;".format(date), null)
        var a:Float? = null
        with(cur) {
            if(moveToNext()) a = cur.getFloat(0)
            cur.close()
        }
        return a
    }

    public fun getContacts(): MutableList<Array<String>> {
        val db = dbHelper.readableDatabase

        val cur = db.rawQuery("SELECT * FROM `contacts`;", null)
        val contacts = mutableListOf<Array<String>>()

        with(cur) {
            while (moveToNext()) {
                contacts.add(arrayOf(cur.getString(0), cur.getString(1)))
            }
        }
        cur.close()

        return contacts
    }

    fun removeContact(clicked: String) {
        val db = dbHelper.writableDatabase

        db.delete("contacts", "name=?", arrayOf(clicked))
    }

    fun addContact(c: String, c1: String) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("name", c)
            put("phone", c1)
        }

        db?.insert("contacts", null, values)
    }
}

