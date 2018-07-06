package io.github.phearing.phearing.room.audiometry

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.phearing.phearing.common.application.ApplicationComponent
import io.github.phearing.phearing.common.application.PHApplication
import io.github.phearing.phearing.room.headphone.DELETE_FLAG
import io.github.phearing.phearing.room.headphone.INSERT_FLAG
import io.github.phearing.phearing.room.headphone.UPDATE_FLAG
import javax.inject.Inject
import javax.inject.Scope

class AudiometryDataRepo {
    @Inject
    lateinit var audiometryDataDao: AudiometryDataDao

    init {
        DaggerAudiometryDataRepoComponent.builder()
                .applicationComponent(PHApplication.applicationComponent)
                .audiometryDataRepoModule(AudiometryDataRepoModule())
                .build().inject(this)
    }

    fun insertAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(audiometryDataDao, INSERT_FLAG).execute(*audiometryData)
    }

    fun deleteAudiometryData(vararg audiometryData: AudiometryData) {
        PrimaryAudiometryDataTask(audiometryDataDao, DELETE_FLAG).execute(*audiometryData)
    }

    fun loadNextAudiometryData(time: Long, num: Int): LiveData<List<AudiometryData>> {
        return audiometryDataDao.loadNext(time, num)
    }

    private class PrimaryAudiometryDataTask(private val mDao: AudiometryDataDao, private val mFlag: Int)
        : AsyncTask<AudiometryData, Unit, Unit>() {

        override fun doInBackground(vararg p0: AudiometryData?) {
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
annotation class AudiometryDataScope

@Module
class AudiometryDataRepoModule {
    @Provides
    @AudiometryDataScope
    fun provideDao(context: Context): AudiometryDataDao {
        return Room.databaseBuilder(context.applicationContext,
                AudiometryDatabase::class.java, "audiometry_data_database")
                .build().audiometryDataDao()
    }
}

@AudiometryDataScope
@Component(modules = [AudiometryDataRepoModule::class], dependencies = [ApplicationComponent::class])
interface AudiometryDataRepoComponent {
    fun inject(audiometryDataRepo: AudiometryDataRepo)
}