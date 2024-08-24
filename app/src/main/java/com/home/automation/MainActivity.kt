package com.droiduino.bluetoothconn

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private var deviceName: String? = null
    private lateinit var deviceAddress: String
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonConnect: Button
    private lateinit var buttonToggle: Button
    private lateinit var imageView: ImageView
    private lateinit var textViewInfo: TextView

    companion object {
        const val CONNECTING_STATUS = 1
        const val MESSAGE_READ = 2
        lateinit var mmSocket: BluetoothSocket
        lateinit var connectedThread: ConnectedThread
        lateinit var createConnectThread: CreateConnectThread
        lateinit var handler: Handler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        buttonConnect = findViewById(R.id.buttonConnect)
        buttonToggle = findViewById(R.id.buttonToggle)
        imageView = findViewById(R.id.imageView)
        textViewInfo = findViewById(R.id.textViewInfo)

        progressBar.visibility = View.GONE
        buttonToggle.isEnabled = false
        imageView.setBackgroundColor(getColor(R.color.colorOff))

        deviceName = intent.getStringExtra("deviceName")
        deviceName?.let {
            deviceAddress = intent.getStringExtra("deviceAddress") ?: ""
            toolbar.subtitle = "Connecting to $it..."
            progressBar.visibility = View.VISIBLE
            buttonConnect.isEnabled = false

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            createConnectThread = CreateConnectThread(bluetoothAdapter, deviceAddress)
            createConnectThread.start()
        }

        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    CONNECTING_STATUS -> handleConnectingStatus(msg)
                    MESSAGE_READ -> handleMessageRead(msg)
                }
            }
        }

        buttonConnect.setOnClickListener {
            val intent = Intent(this@MainActivity, SelectDeviceActivity::class.java)
            startActivity(intent)
        }

        buttonToggle.setOnClickListener {
            handleButtonToggle()
        }
    }

    private fun handleConnectingStatus(msg: Message) {
        when (msg.arg1) {
            1 -> {
                toolbar.subtitle = "Connected to $deviceName"
                progressBar.visibility = View.GONE
                buttonConnect.isEnabled = true
                buttonToggle.isEnabled = true
            }
            -1 -> {
                toolbar.subtitle = "Device fails to connect"
                progressBar.visibility = View.GONE
                buttonConnect.isEnabled = true
            }
        }
    }

    private fun handleMessageRead(msg: Message) {
        val arduinoMsg = msg.obj.toString()
        when (arduinoMsg.toLowerCase()) {
            "led is turned on" -> {
                imageView.setBackgroundColor(getColor(R.color.colorOn))
                textViewInfo.text = "Arduino Message: $arduinoMsg"
            }
            "led is turned off" -> {
                imageView.setBackgroundColor(getColor(R.color.colorOff))
                textViewInfo.text = "Arduino Message: $arduinoMsg"
            }
        }
    }

    private fun handleButtonToggle() {
        var cmdText: String? = null
        val btnState = buttonToggle.text.toString().toLowerCase()

        when (btnState) {
            "turn on" -> {
                buttonToggle.text = "Turn Off"
                cmdText = "111111111111"
                Toast.makeText(this, "Turned ON", Toast.LENGTH_SHORT).show()
            }
            "turn off" -> {
                buttonToggle.text = "Turn On"
                cmdText = "000000000000"
                Toast.makeText(this, "Turned OFF", Toast.LENGTH_SHORT).show()
            }
        }

        connectedThread.write(cmdText!!)
    }

    class CreateConnectThread(bluetoothAdapter: BluetoothAdapter, address: String) : Thread() {

        init {
            val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            var tmp: BluetoothSocket? = null
            val uuid: UUID = bluetoothDevice.uuids[0].uuid

            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.e("Socket Error", "Socket's create() method failed", e)
            }
            mmSocket = tmp!!
        }

        override fun run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()

            try {
                mmSocket.connect()
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget()
                connectedThread = ConnectedThread(mmSocket)
                connectedThread.start()
            } catch (e: IOException) {
                try {
                    mmSocket.close()
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget()
                } catch (e: IOException) {
                    Log.e("Socket Error", "Could not close the client socket", e)
                }
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e("Socket Error", "Could not close the client socket", e)
            }
        }
    }

    class ConnectedThread(private val socket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = socket.inputStream
        private val mmOutStream: OutputStream = socket.outputStream

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes = 0

            while (true) {
                try {
                    buffer[bytes] = mmInStream.read().toByte()
                    if (buffer[bytes] == '\n'.toByte()) {
                        val readMessage = String(buffer, 0, bytes)
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }

        fun write(input: String) {
            try {
                mmOutStream.write(input.toByteArray())
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e("Socket Error", "Unable to close socket", e)
            }
        }
    }

    override fun onBackPressed() {
        createConnectThread.cancel()
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
}
