package io.github.phearing.phearing

import android.util.Log
import androidx.test.runner.AndroidJUnit4
import org.junit.runner.RunWith
import org.junit.After
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import io.github.phearing.phearing.room.headphone.Headphone
import io.github.phearing.phearing.room.headphone.HeadphoneDao
import io.github.phearing.phearing.room.headphone.HeadphoneDatabase
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.util.*


@RunWith(AndroidJUnit4::class)
class RoomTest {
    private lateinit var mDao: HeadphoneDao
    private lateinit var mDb: HeadphoneDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        mDb = Room.inMemoryDatabaseBuilder(context, HeadphoneDatabase::class.java).build()
        mDao = mDb.headphoneDao()
    }

    @After
    fun closeDb() {
        mDb.close()
    }

    @Test
    fun test() {
        val headphone = Headphone("hd4.5 btnc", "111", "222", Date().time)
        mDao.insert(headphone)
        val list = mDao.loadAll()
        Log.e("RoomTest", list.toString())
        Log.e("RoomTest2", list.value?.size.toString())

        assertTrue(false)
        //if (list.isEmpty()) assertTrue(false)
    }
}