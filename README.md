# `HC05.aab`

    Android App in Kotlin to Communicate HC05 via Arduino UNO

![blocks (1)](https://github.com/user-attachments/assets/4c02b483-4d52-4848-b45f-a7fcaaa61f29)

### 1. **ListPicker1.BeforePicking**
- **Function:** Before the user selects from the list of Bluetooth devices, the app retrieves the available Bluetooth devices.
- **Action:** Sets the elements of `ListPicker1` to the list of paired Bluetooth devices, obtained using `BluetoothClient1.AddressesAndNames`.

### 2. **ListPicker1.AfterPicking**
- **Function:** After the user selects a Bluetooth device from the list.
- **Actions:** 
  - Sets the `Selection` property of `ListPicker1` to the chosen device's address.
  - Connects to the selected Bluetooth device using `BluetoothClient1.Connect` by passing the selected address.
  - Sets the text of `ListPicker1` to "Connected" after a successful connection.

### 3. **Button1.Click**
- **Function:** Sends a command when `Button1` is clicked.
- **Action:** Uses `BluetoothClient1.SendText` to send the text `"1111111"` to the connected Bluetooth device.

### 4. **Button2.Click**
- **Function:** Sends a different command when `Button2` is clicked.
- **Action:** Uses `BluetoothClient1.SendText` to send the text `"0000000"` to the connected Bluetooth device.

### Summary:
- The app connects to a Bluetooth device selected by the user from a list and allows sending predefined text commands ("1111111" or "0000000") via Bluetooth when buttons are pressed.

```py

# pip install bleak
# pip install pyserial

from bleak import BleakScanner
import serial.tools.list_ports
import asyncio
import serial
import time

async def main():
    devices = await BleakScanner.discover()
    for device in devices:
        print(device)

asyncio.run(main())

def find_hc05_com_port():
    # List all available COM ports
    ports = serial.tools.list_ports.comports()
    
    for port in ports:
        # Replace with a more specific check if needed
        if "USB Serial Device" in port.description or "HC-05" in port.description:
            return port.device
    
    return None

# Dynamically find the HC-05 COM port
bluetooth_serial_port = find_hc05_com_port()

if bluetooth_serial_port:
    try:
        # Initialize serial connection
        ser = serial.Serial(bluetooth_serial_port, baudrate=9600, timeout=1)
        time.sleep(2)  # Give the connection time to establish

        # Send the "1" command
        ser.write(b'1')
        print(f"Sent '1' to HC-05 on port {bluetooth_serial_port}")

    except serial.SerialException as e:
        print(f"Serial Exception: {e}")
    except Exception as e:
        print(f"Failed to send: {e}")
    finally:
        if 'ser' in locals() and ser.is_open:
            ser.close()
else:
    print("HC-05 not found among the available COM ports.")
```
