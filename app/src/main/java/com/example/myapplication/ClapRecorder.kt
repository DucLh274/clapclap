package com.example.myapplication

import android.app.Application
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.PercussionOnsetDetector
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


object ClapRecorder {
    private const val AMPLITUDE_DIFF_LOW = 5000
    private const val AMPLITUDE_DIFF_MED = 9000
    private const val AMPLITUDE_DIFF_HIGH = 30000

    private lateinit var app: Application

    private var isRecording = false
    private lateinit var mediaRecorder: MediaRecorder
    var clapDetectListener: ClapDetectListener? = null


    fun init(application: Application) {
        app = application
    }


    private var clapCount = 0
    private var firstClapTime = -1L
    private var timePeriod = 1_500L
    private var targetClapCountDetect = 2
    private var dispatcher: AudioDispatcher? = null
    var job: Job? = null
    fun startAudioDispatcher() {
        Log.e("DucLH-----startAudioDispatcher", "startAudioDispatcher")
        if (dispatcher == null) {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        }
        addProcessor()
        Log.e("DucLH-----startAudioDispatcher", "addProcessor")
        Log.e("DucLH-----startAudioDispatcher", "dispatcher : IsStop : " + dispatcher?.isStopped)
        Log.e("DucLH-----startAudioDispatcher", "RunProcessor")
        runProcessor()
    }

    fun addProcessor() {
        dispatcher?.removeAudioProcessor(curPercussionDetector)
        getPercussionDetector()
        dispatcher?.addAudioProcessor(curPercussionDetector)
    }

    fun runProcessor() {
        if (job != null) return
        job = CoroutineScope(Dispatchers.IO).launch {
            dispatcher?.run()
        }
    }

    fun stopProcessor() {
        job?.cancel()
        job = null
        dispatcher?.removeAudioProcessor(curPercussionDetector)
        dispatcher?.stop()
        dispatcher = null
    }


    val threshold = 6.0
    var sensitivity = 20.0
    var curPercussionDetector = getPercussionDetector()

    val onSetHandler = OnsetHandler { time, salience ->
        Log.e("DucLH---OnsetHandler", salience.toString())

        //Nếu là lần đầu thì lưu lại firstClapTime, tăng count
        if (clapCount == 0) {
            firstClapTime = System.currentTimeMillis()
            clapCount = 1
        } else {
            // Từ lần 2 trở đi. check xem đã quá thời gian period chưa
            // nếu đã time out -> coi nó là clap lần đầu
            // nếu chưa time out, check xem số clapCount đã đủ  =  target chưa
            val diffTime = System.currentTimeMillis() - firstClapTime
            if (diffTime > timePeriod) {
                firstClapTime = System.currentTimeMillis()
                clapCount = 1
            } else {
                clapCount++
                if (clapCount >= targetClapCountDetect) {
                    onDetected()
                    resetData()
                }
            }
        }
    }

    fun changeSensitivity(newSensitivity: Double) {
        sensitivity = newSensitivity
        addProcessor()
//        runProcessor()
    }

    fun getPercussionDetector(): PercussionOnsetDetector {
        curPercussionDetector = PercussionOnsetDetector(
            22050f, 1024, onSetHandler, sensitivity, threshold
        )
        return curPercussionDetector
    }

    val player = MediaPlayer()
    private fun onDetected() {
        Log.e("DucLH---dsadsada", "onDetected")
        resetData()
        try {
            val afd = app.assets.openFd("pewpew.mp3");
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length);
            player.prepare()
            if (player.isPlaying) return
            player.start()
            Handler(Looper.getMainLooper()).postDelayed({
                player.stop()
                player.reset()
            }, 1_000)
        } catch (e: Exception) {

        }
    }

    private fun resetData() {
        firstClapTime = -1L
        clapCount = 0
    }

}