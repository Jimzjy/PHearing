package io.github.phearing.phearing.room.headphone

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.phearing.phearing.common.application.ApplicationComponent
import io.github.phearing.phearing.common.application.PHApplication
import javax.inject.Inject
import javax.inject.Scope

const val DELETE_FLAG = 0
const val INSERT_FLAG = 1
const val UPDATE_FLAG = 2

class HeadphoneRepo {
    @Inject
    lateinit var headphoneDao: HeadphoneDao
    val allHeadphones: LiveData<List<Headphone>> by lazy {
        headphoneDao.loadAll()
    }

    init {
        DaggerHeadphoneRepoComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .headphoneRepoModule(HeadphoneRepoModule())
                .build().inject(this)
    }

    fun insertHeadphones(vararg headphones: Headphone) {
        PrimaryHeadphonesTask(headphoneDao, INSERT_FLAG).execute(*headphones)
    }

    fun deleteHeadphones(vararg headphones: Headphone) {
        PrimaryHeadphonesTask(headphoneDao, DELETE_FLAG).execute(*headphones)
    }

    private class PrimaryHeadphonesTask(private val mDao: HeadphoneDao, private val mFlag: Int)
        : AsyncTask<Headphone, Unit, Unit>() {

        override fun doInBackground(vararg p0: Headphone?) {
            when(mFlag) {
                DELETE_FLAG -> {
                    p0.forEach { it?.let { mDao.delete(it) } }
                }
                INSERT_FLAG -> {
                    p0.forEach { it?.let { mDao.insert(it) } }
                }
                UPDATE_FLAG -> {
                    p0.forEach { it?.let { mDao.update(it) } }
                }
            }
        }
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class HeadphoneRepoScope

@Module
class HeadphoneRepoModule {
    @Provides
    @HeadphoneRepoScope
    fun provideDao(context: Context): HeadphoneDao {
        return Room.databaseBuilder(context.applicationContext,
                HeadphoneDatabase::class.java, "headphone_database")
                .build().headphoneDao()
    }
}

@HeadphoneRepoScope
@Component(modules = [HeadphoneRepoModule::class], dependencies = [ApplicationComponent::class])
interface HeadphoneRepoComponent {
    fun inject(headphoneRepo: HeadphoneRepo)
}