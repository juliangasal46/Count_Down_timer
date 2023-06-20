package com.example.count_down_timer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import com.example.count_down_timer.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    // Binding = Usado para interactuar mejor con la UI, sin necesidad de poner findViewById(...)
    // si no que los podemos usar por referencias directas a los elementos
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataHelper: DataHelper
    private lateinit var mp: MediaPlayer

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHelper = DataHelper(applicationContext)

        binding.btnPause.setOnClickListener{startStopAction()}
        binding.btnReset.setOnClickListener{resetAction()}
        binding.btnShowDialog.setOnClickListener{showEditTextDialog()}
        binding.btnActuarAlarma.setOnClickListener{chequearAlarma()}

        mp = MediaPlayer.create(applicationContext, R.raw.alarma_sonido) // Mirar cada cuanto tiempo suena ...

        if(dataHelper.timerCounting())
        {
            startTimer()
        } else
        {
            stopTimer()
            if(dataHelper.startTime() != null && dataHelper.stopTime() != null)
            {
                showEditTextDialog() // Cada vez que se entre a la app y se mire si
                // está el timer ejecutándose, si está parado es que ha terminado y necesita
                // introducir un valor para la alarma
                val time = Date().time - calcRestartTime().time
                binding.tvTime.text = timeStringFromLong(time)
            }
        }

        // Cada cuanto tiempo se comprobará el timer ? -> 500 milisegundos
        timer.scheduleAtFixedRate(timeTask(), 0, 500)
        timer.scheduleAtFixedRate(checkAlarm(), 0,500)
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

    private inner class checkAlarm: TimerTask(){
        override fun run() {
            if ((binding.tvAlarma.text.toString() == binding.tvTime.text.toString())
                && binding.tvTime.text != "00:00:00") {
                // Mirar que sean iguales para parar el timer pero que no sean iguales a 0
                dataHelper.setTimerCounting(false)
                mp.start()
            }
        }
    }

    private fun chequearAlarma(){

        if (mp.isPlaying()) {
            mp.stop()
            mp.prepare() // Cuando stop hay que preparar parar start()
            binding.btnActuarAlarma.text = "Start Alarm"
        } else{
            mp.start()
            binding.btnActuarAlarma.text = "Stop Alarm"
        }
    }

    private fun showEditTextDialog()
    {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertdialog, null)
        val editText_Dialog_Horas = dialogLayout.findViewById<EditText>(R.id.etHoras)
        val editText_Dialog_Minutos = dialogLayout.findViewById<EditText>(R.id.etMinutes)
        val editText_Dialog_Segundos = dialogLayout.findViewById<EditText>(R.id.etSeconds)

        with(alertDialogBuilder){
            setTitle("¿Cuándo quieres que suene la alarma?")

            setPositiveButton("DONE"){ dialog, which ->

                // Chequear cuando sean menores de 10 poner 09
                if(editText_Dialog_Segundos.text.toString().toInt() < 10){
                    editText_Dialog_Segundos.setText("0${editText_Dialog_Segundos.text}")
                }
                if(editText_Dialog_Minutos.text.toString().toInt() < 10){
                    editText_Dialog_Minutos.setText("0${editText_Dialog_Minutos.text}")
                }
                if(editText_Dialog_Horas.text.toString().toInt() < 10){
                    editText_Dialog_Horas.setText("0${editText_Dialog_Horas.text}")
                }
                binding.tvAlarma.setText("${editText_Dialog_Horas.text}:${editText_Dialog_Minutos.text}:${editText_Dialog_Segundos.text}")
            }

            setNegativeButton("CANCEL"){ dialog, which->  }

            setView(dialogLayout)
            show()
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

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}