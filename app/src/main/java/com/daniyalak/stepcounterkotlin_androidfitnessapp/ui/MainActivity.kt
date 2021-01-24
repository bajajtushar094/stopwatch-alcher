package com.daniyalak.stepcounterkotlin_androidfitnessapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.daniyalak.stepcounterkotlin_androidfitnessapp.R
import com.daniyalak.stepcounterkotlin_androidfitnessapp.callback.stepsCallback
import com.daniyalak.stepcounterkotlin_androidfitnessapp.helper.GeneralHelper
import com.daniyalak.stepcounterkotlin_androidfitnessapp.service.StepDetectorService
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), stepsCallback {

    private var mStopWatch: Chronometer? = null
    private var distance: TextView? = null
    private var calories: TextView? = null
    private var steps: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, StepDetectorService::class.java)


        mStopWatch =  findViewById<Chronometer>(R.id.chronometer)
        distance = findViewById<TextView>(R.id.TV_DISTANCE)
        calories = findViewById<TextView>(R.id.TV_CALORIES)
        steps = findViewById<TextView>(R.id.TV_STEPS)
        mStopWatch!!.setOnChronometerTickListener(OnChronometerTickListener { cArg ->
            val time = SystemClock.elapsedRealtime() - cArg.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val hh = if (h < 10) "0$h" else h.toString() + ""
            val mm = if (m < 10) "0$m" else m.toString() + ""
            val ss = if (s < 10) "0$s" else s.toString() + ""
            cArg.text = "$hh:$mm:$ss"
        })
        mStopWatch!!.setBase(SystemClock.elapsedRealtime())
        val button = findViewById<View>(R.id.startstop) as Button
        button.tag = 1
        button.text = "START"
        button.setOnClickListener { v ->
            val status = v.tag as Int
            if (status == 1) {
                startService(intent)
                StepDetectorService.subscribe.register(this)
                mStopWatch!!.text = "00:00:00"
                mStopWatch!!.start()
                button.text = "STOP"
                v.tag = 0 //pause

            } else {
                onStopCommand(intent)
                mStopWatch!!.setBase(SystemClock.elapsedRealtime());
                mStopWatch!!.stop()

                button.text = "START"
                v.tag = 1 //pause
            }
        }
    }

    override fun subscribeSteps(steps: Int) {
        TV_STEPS.setText(steps.toString())
        TV_CALORIES.setText(GeneralHelper.getCalories(steps))
        TV_DISTANCE.setText(GeneralHelper.getDistanceCovered(steps))
    }
}