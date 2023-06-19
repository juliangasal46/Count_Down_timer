package com.example.count_down_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.count_down_timer.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    // Binding = Usado para interactuar mejor con la UI, sin necesidad de poner findViewById(...)
    // si no que los podemos usar por referencias directas a los elementos
    lateinit var binding: ActivityMainBinding
    lateinit var dataHelper: DataHelper

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHelper = DataHelper(applicationContext)

        binding.btnPause.setOnClickListener{startStopAction()}
        binding.btnReset.setOnClickListener{resetAction()}

        if(dataHelper.timerCounting())
        {
            startTimer()
        } else
        {
            stopTimer()
            if(dataHelper.startTime() != null && dataHelper.stopTime() != null)
            {
                val time = Date().time - calcRestartTime().time
                binding.tvTime.text = timeStringFromLong(time)
            }
        }

        // Cada cuanto tiempo se comprobará el timer ? -> 500 milisegundos
        timer.scheduleAtFixedRate(timeTask(), 0, 500)
    }


    private inner class timeTask: TimerTask()
    {
        override fun run()
        {
            if(dataHelper.timerCounting())
            {
                val time = Date().time - dataHelper.startTime()!!.time
                binding.tvTime.text = timeStringFromLong(time)
            }
        }
    }


    private fun startStopAction()
    {
        if(dataHelper.timerCounting())
        {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else
        {
            if(dataHelper.stopTime() != null)
            {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else
            {
                dataHelper.setStartTime(Date()) // A new Date
            }
            startTimer()
        }
    }


    private fun calcRestartTime(): Date
    {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }


    private fun resetAction()
    {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.tvTime.text = timeStringFromLong(0) // Format the time
    }


    private fun startTimer()
    {
        dataHelper.setTimerCounting(true)
        // binding.btnPause.background = (R.drawable.pause_vector).toDrawable()
        binding.btnPause.text = "PAUSE"
    }


    private fun stopTimer()
    {
        dataHelper.setTimerCounting(false)
        binding.btnPause.text = "PLAY"
    }

    private fun timeStringFromLong(miliSeconds: Long ): String
    {
        // We will have to make a conversion to get the time correct
        val seconds = (miliSeconds / 1000) % 60
        val minutes = (miliSeconds / (1000 * 60) % 60)
        val hours = (miliSeconds / (1000 * 60 *60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String
    {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}