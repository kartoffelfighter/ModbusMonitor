package org.marcfischer.modbusmonitor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().add(R.id.main_fragment, SerialPort(), "devices").commit()
        }
        else {
            onBackStackChanged()
        }
    }

    private fun onBackStackChanged(){
        supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount>0)
    }

    override fun onNewIntent(intent: Intent){
        super.onNewIntent(intent)
        if("android.hardware.usb.action.USB_DEVICE_ATTACHED" == intent.action){
            Toast.makeText(this,"USB Device Attached", Toast.LENGTH_SHORT).show()
        }
    }


}