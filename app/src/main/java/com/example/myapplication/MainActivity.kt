package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import xyz.kumaraswamy.autostart.Autostart

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.startRecording).setOnClickListener {
            ClapRecorder.startAudioDispatcher()
        }

        findViewById<Button>(R.id.stopRecording).setOnClickListener {
            ClapRecorder.stopProcessor()
        }

        findViewById<TextView>(R.id.tvSen)
        findViewById<SeekBar>(R.id.sb).setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                findViewById<TextView>(R.id.tvSen).setText(progress.toString())
                Log.e("DucLH---onProgressChanged", progress.toString() + fromUser)
                ClapRecorder.changeSensitivity(progress.toDouble())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }


    fun startRecording(v: View?) {
        ClapRecorder.clapDetectListener = object : ClapDetectListener {
            override fun onClapDetected() {
//                ClapRecorder.stopRecord()
            }
        }


    }
}