package io.github.phearing.phearing.common.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler

const val SAMPLE_RATE_IN_HZ = 8000
const val DEFAULT_RECORDER_INTERVAL = 250L

class RecorderAverage(private val mBase: Double = 1.0) {
    private val mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT)
    private var mAudioRecord: AudioRecord? = null
    private var mRecordListener: ((db: Float) -> Unit)? = null
    private var mIsStart = false
    private var mInterval = 1000L

    fun startByInterval(interval: Long = DEFAULT_RECORDER_INTERVAL) {
        if (mAudioRecord != null) {return}
        mInterval = interval
        mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, mBufferSize)

        val buffer = ShortArray(mBufferSize)
        val handler = Handler()
        mIsStart = true
        mAudioRecord?.startRecording()

        try {
            Thread{
                while (mIsStart) {
                    Thread.sleep(mInterval)
                    val size = mAudioRecord?.read(buffer, 0, mBufferSize)?.toDouble()
                    var total = 0L
                    buffer.forEach {
                        total += it * it
                    }

                    size?.let {
                        val db = toDb(total/it)
                        handler.post {
                            mRecordListener?.invoke(db)
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        mIsStart = false
        mAudioRecord?.let {
            it.stop()
            it.release()
        }
        mAudioRecord = null
    }

    fun setRecordListener(listener: (db: Float) -> Unit) {
        mRecordListener = listener
    }

    fun justStart() {
        if (mAudioRecord != null) {return}

        mAudioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, mBufferSize)

        mAudioRecord?.startRecording()
    }

    fun getDb(): Float {
        if (mAudioRecord == null) return -1f

        val buffer = ShortArray(mBufferSize)
        val size = mAudioRecord?.read(buffer, 0, mBufferSize)?.toDouble()
        var total = 0L
        buffer.forEach {
            total += it * it
        }

        size?.let {
            return toDb(total/it)
        }
        return -1f
    }

    private fun toDb(value: Double): Float {
        val db = Math.log10(value/(mBase * mBase)) * 10
        return String.format("%.1f", db).toFloat()
    }
}

const val MAX_DURATION = 1000 * 60 * 5
const val FILE_NAME = "recorder_cache"

class RecorderMax(private val context: Context, private val mBase: Double = 1.0) {
    companion object {
        @JvmStatic
        fun getCacheDir(context: Context): String = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir.path
        } else {
            context.cacheDir.path
        }
    }

    private var mMediaRecorder: MediaRecorder? = null
    private var mRecordListener: ((db: Float) -> Unit)? = null
    private var mInterval = 1000L
    private var mIsStart = false

    fun startByInterval(interval: Long = DEFAULT_RECORDER_INTERVAL) {
        if (mMediaRecorder != null) {return}
        mInterval = interval
        mMediaRecorder = MediaRecorder()
        mIsStart = true
        val handler = Handler()

        try {
            mMediaRecorder?.let {
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                it.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                it.setOutputFile(getCacheDir(context) + FILE_NAME)
                it.setMaxDuration(MAX_DURATION)
                it.prepare()
                it.start()

                it.maxAmplitude
            }

            Thread{
                while (mIsStart) {
                    Thread.sleep(mInterval)

                    val db = toDb(mMediaRecorder?.maxAmplitude ?: 0)
                    handler.post {
                        mRecordListener?.invoke(db)
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        mIsStart = false
        mMediaRecorder?.let {
            it.stop()
            it.reset()
            it.release()
        }
        mMediaRecorder = null
    }

    fun setRecordListener(listener: (db: Float) -> Unit) {
        mRecordListener = listener
    }

    fun justStart() {
        if (mMediaRecorder != null) {return}
        mMediaRecorder = MediaRecorder()

        try {
            mMediaRecorder?.let {
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                it.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                it.setOutputFile(getCacheDir(context) + FILE_NAME)
                it.setMaxDuration(MAX_DURATION)
                it.prepare()
                it.start()

                it.maxAmplitude
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDb(): Float {
        if (mMediaRecorder == null){
            return -1f
        }
        mMediaRecorder?.let {
            return toDb(it.maxAmplitude)
        }
        return -1f
    }

    private fun toDb(amplitude: Int): Float {
        val db = Math.log10(amplitude / mBase).toFloat() * 20
        return String.format("%.1f", if (db < 0) 0f else db).toFloat()
    }
}
