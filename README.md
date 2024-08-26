# `HC05.aab`

    Android App in Java to Communicate HC05 via Arduino UNO

![image](https://github.com/user-attachments/assets/3bda758e-612b-4aa7-904d-14e89fa0d8cf)
![Arduino](https://github.com/user-attachments/assets/a9070b23-6209-4017-8a65-3da5941be96b)

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

![blocks](https://github.com/user-attachments/assets/4c02b483-4d52-4848-b45f-a7fcaaa61f29)

