
# pip install bleak

import asyncio
from bleak import BleakScanner, BleakClient

async def main():
    devices = await BleakScanner.discover()
    for device in devices:
        print(device)

asyncio.run(main())


'''
(myenv) C:\Users\surface\Documents\GitHub\HC05.aab>python HC05.p
4C:82:A9:22:7A:89: None
'''
