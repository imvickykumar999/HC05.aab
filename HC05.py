
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

# List all available COM ports
ports = serial.tools.list_ports.comports()

for port in ports:
    print(f"Port: {port.device}, Description: {port.description}")

# Replace with your Bluetooth COM port or device path
# Example for Windows: 'COMx' where x is the port number
# Example for Linux: '/dev/rfcomm0'
bluetooth_serial_port = port.device  # Replace 'COMx' with your actual port

# Initialize serial connection
ser = serial.Serial(bluetooth_serial_port, baudrate=9600, timeout=1)

time.sleep(2)  # Give the connection time to establish

try:
    # Send the "0" command
    ser.write(b'1')
    print("Sent '1' to HC-05")
except Exception as e:
    print(f"Failed to send: {e}")
finally:
    # Close the serial connection
    ser.close()

# OUTPUT
r'''
(myenv) C:\Users\surface\Documents\GitHub\HC05.aab>python HC05.p
4C:82:A9:22:7A:89: None
Port: COM6, Description: USB Serial Device (COM6)
Sent '1' to HC-05
'''
