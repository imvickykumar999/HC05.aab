
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
