package com.example.count_down_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var tvTimer_Horas: TextView
    private lateinit var tvTimer_Minutos: TextView
    private lateinit var tvTimer_Segundos: TextView
    private var horas: Int = 0
    private var minutos: Int = 0
    private var segundos: Int = 0
    private var coroutineRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlay = findViewById(R.id.btnContinue)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)
        tvTimer_Horas = findViewById(R.id.tvTimerHoras)
        tvTimer_Minutos = findViewById(R.id.tvTimerMinutos)
        tvTimer_Segundos = findViewById(R.id.tvTimerSegundos)

        showInputDialog()

        btnPlay.setOnClickListener{
            coroutineRunning = true

            val empiezaTimer = GlobalScope.launch {
                restarTiempo()
            }
        }

        btnPause.setOnClickListener{
            coroutineRunning = false
        }

        btnReset.setOnClickListener{



        }

    }

    private fun showInputDialog()  {

        val alertDialog = AlertDialog.Builder(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.alertdialog, null)

        // Pasamos la vista al builder para que construya
        alertDialog.setView(view)

        val dialog = alertDialog.create()

        dialog.show()

        // Los componentes están guardados en nuestro view que le metemos -> Coger datos de boxes
        val horasCajas = view.findViewById<EditText>(R.id.etHoras)
        val minutosCajas = view.findViewById<EditText>(R.id.etMinutes)
        val segundosCajas = view.findViewById<EditText>(R.id.etSeconds)

        view.findViewById<Button>(R.id.btnAccept).setOnClickListener{

            if(horasCajas.text.isNotEmpty()
                && minutosCajas.text.isNotEmpty()
                && segundosCajas.text.isNotEmpty()){

                horas = Integer.parseInt((horasCajas.text).toString())
                minutos = Integer.parseInt((minutosCajas.text).toString())
                segundos = Integer.parseInt((segundosCajas.text).toString())

                tvTimer_Horas.text = (horas).toString()
                tvTimer_Minutos.text = (minutos).toString()
                tvTimer_Segundos.text = (segundos).toString()

                dialog.dismiss() // Que se cierre

            } else {
                Toast.makeText(this, "Revisa los datos, hay algo mal", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener{
            dialog.dismiss() // Que se cierre
        }
    }

    private suspend fun restarTiempo() {

        lifecycleScope.launch(Dispatchers.Default) {
            while (coroutineRunning) {

                runOnUiThread {
                    if (segundos > 0) {
                        segundos -= 1
                        tvTimer_Segundos.text = (segundos).toString()
                    } else {

                        if (minutos == 0 && horas == 0) {
                            Toast.makeText(this@MainActivity, "RING RING", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            tvTimer_Segundos.text = "60"
                            segundos = 60

                            // Mirar si las horas tienen algo y si los minutos también
                            if (minutos > 0) {
                                minutos -= 1
                                tvTimer_Minutos.text = (minutos).toString()
                            } else {
                                tvTimer_Minutos.text = "59"
                                minutos = 59

                                if (horas > 0) {
                                    horas -= 1
                                    tvTimer_Horas.text = (horas).toString()
                                } else {
                                    if (horas == 0 && minutos == 0 && segundos == 0) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "RING RING",
                                            Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                        }
                    }
                }
                delay(1000)
            }
        }
    }


    suspend fun restaurarValores(){
        horas = 0
        minutos = 0
        segundos = 0

        tvTimer_Horas.text = "00"
        tvTimer_Minutos.text = "00"
        tvTimer_Segundos.text = "00"

        delay(2000)
    }



}