package org.marcfischer.modbusmonitor

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.ListFragment
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.lang.Exception

class SerialPort : ListFragment() {
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    class PortConfig  {
        companion object {
            var baudRate: Int = 19200
            var dataBits: Int = 8
            var stopBits: Int = UsbSerialPort.STOPBITS_1
            var parity: Int = UsbSerialPort.PARITY_EVEN
        }
    }

    var noUsbDevicesAvailable: Boolean = true   // set to true in case no USB Serial Adapters are found
    private var serialPort:String = ""
    var connectedPort : UsbSerialPort? = null
    /**
     * This method provides the main activity with a menu (top right 3 dots)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        refresh()   // check immediately for available ports
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_devices, menu)
    }

    override fun onResume() {
        super.onResume()
        //refresh()
    }

   fun getPortInstance() :UsbSerialPort? {
        return connectedPort
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.refresh) {
            refresh()
            true
        } else if (id == R.id.baud_rate) {
            val baudRates = resources.getStringArray(R.array.baud_rates)
            val pos = listOf(*baudRates).indexOf(PortConfig.baudRate.toString())
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Baud rate")
            builder.setSingleChoiceItems(
                baudRates, pos
            ) { dialog, item ->
                PortConfig.baudRate = baudRates[item].toInt()
                dialog.dismiss()
            }
            builder.create().show()
            true
        } else if (id == R.id.serial_settings) {
            val baudRates = resources.getStringArray(R.array.baud_rates)
            val baudPos = listOf(*baudRates).indexOf(PortConfig.baudRate.toString())
            val stopBits = resources.getStringArray(R.array.stopbits)
            val stopPos = listOf(*stopBits).indexOf(PortConfig.stopBits.toString())
            val builder = AlertDialog.Builder(activity)
            with(builder){
                setTitle("Serial Settings")
                setMessage("Baud Rate")
                setSingleChoiceItems(
                    baudRates, baudPos
                ) { dialog, item ->
                    PortConfig.baudRate = baudRates[item].toInt()
                }
                setMessage("Stop Bits")
                setSingleChoiceItems(stopBits, stopPos){
                    dialog, item ->
                    PortConfig.stopBits = stopBits[item].toInt()
                }

                create().show()
            }


            true
        } else if (id == R.id.serialPort) {
            val listPos: Int = 0
            val builder = AlertDialog.Builder(activity)
            with(builder) {
                setTitle("Serial port")
                if (noUsbDevicesAvailable) {
                    setMessage("No USB Serial Devices Available. Try to refresh")
                } else{
                    setMessage("Connect to: $serialPort")
                }

                setNeutralButton("Refresh") { dialog, which ->
                    refresh()
                }
                create().show()
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun connectPort(usbManager: UsbManager, driver: UsbSerialDriver) : UsbSerialPort? {
        try {
            val permissionIntent = PendingIntent.getBroadcast(
                this.context,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(driver.device, permissionIntent) // prompt user to access device
            serialPort = driver.device.productName.toString()
            val connection: UsbDeviceConnection = usbManager.openDevice(driver.device)  // establish a USB connection
            noUsbDevicesAvailable = if (connection == null) {
                Toast.makeText(
                    this.context,
                    "Unable to connect to USB port. Check permissions",
                    Toast.LENGTH_LONG
                ).show()
                true
            } else {
                Toast.makeText(
                    this.context,
                    "Connected to $serialPort",
                    Toast.LENGTH_LONG
                ).show()
                false
            }
            val port : UsbSerialPort = driver.ports[0]
            port.open(connection)
            return port
        } catch (e: Exception) {
            Toast.makeText(
                this.context,
                "Error: Unable to connect to USB port.",
                Toast.LENGTH_LONG
            ).show()
        }
        return null
    }


    private fun refresh() {
        val usbManager = requireActivity().getSystemService(Context.USB_SERVICE) as UsbManager
        var availableDrivers: List<UsbSerialDriver> =
            UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Toast.makeText(this.context, "No Serial Adapters Found", Toast.LENGTH_LONG).show()
            noUsbDevicesAvailable = true
        } else {
            connectedPort = connectPort(usbManager, availableDrivers[0])
        }
    }
}
