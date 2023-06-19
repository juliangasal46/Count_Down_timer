package com.example.count_down_timer

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*


class DataHelper(context: Context)
{
    // Esto es para pasar los datos de una clase a otra
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

    private var timerCounting = false
    private var startTime: Date? = null
    private var stopTime: Date? = null

    fun startTime(): Date? = startTime
    fun stopTime(): Date? = stopTime
    fun timerCounting(): Boolean = timerCounting

    // Inicializamos con los valores que metamos por defecto
    init
    {
        timerCounting = sharedPreferences.getBoolean(COUNTING_TIME_KEY, false)

        val startString = sharedPreferences.getString(START_TIME_KEY, null)
        if(startString != null)
            startTime = dateFormat.parse(startString) // Le metemos la fecha inicial

        val stopString = sharedPreferences.getString(STOP_TIME_KEY, null)
        if(stopString != null)
            stopTime = dateFormat.parse(startString) // Le metemos la fecha inicial
    }

    // Instancia de una clase / objeto pero sin crearlo
    companion object
    {
        const val PREFERENCES = "prefs"
        const val START_TIME_KEY =  "startTime"
        const val STOP_TIME_KEY =  "stopTime"
        const val COUNTING_TIME_KEY =  "countingTime"
    }

    fun setStartTime(date: Date?)
    {
        startTime = date

        // La función with se usa para hacer llamadas a funciones o hacer operaciones
        // en un objeto sin tener que llamarlo cada pasada
        with(sharedPreferences.edit())
        {
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(START_TIME_KEY, stringDate)
            apply()
        }
    }

    fun setStopTime(date: Date?)
    {
        stopTime = date

        // La función with se usa para hacer llamadas a funciones o hacer operaciones
        // en un objeto sin tener que llamarlo cada pasada
        with(sharedPreferences.edit())
        {
            val stringDate = if(date == null) null else dateFormat.format(date)
            putString(STOP_TIME_KEY, stringDate)
            apply()
        }
    }

    fun setTimerCounting(value: Boolean)
    {
        timerCounting = value

        // La función with se usa para hacer llamadas a funciones o hacer operaciones
        // en un objeto sin tener que llamarlo cada pasada
        with(sharedPreferences.edit())
        {
            putBoolean(COUNTING_TIME_KEY, value) // Manda por la shared prefferences el boolean
            apply()
        }
    }
}