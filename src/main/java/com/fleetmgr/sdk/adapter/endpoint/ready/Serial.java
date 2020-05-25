package com.fleetmgr.sdk.adapter.endpoint.ready;

import com.fleetmgr.sdk.adapter.Endpoint;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class Serial extends Endpoint implements
        SerialPortEventListener {

    private static final Logger logger = LoggerFactory.getLogger(Serial.class);

    private SerialPort serialPort;

    @Override
    public void initialize(String input) throws Exception {
        JSONObject json = new JSONObject(input);
        serialPort = new SerialPort(json.getString("port"));
        serialPort.openPort();
        serialPort.setParams(json.getInt("baudrate"), 8, 1, 0);
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        serialPort.addEventListener(this);
    }

    @Override
    public void shutdown() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            logger.warn("Could not close SerialPort", e);
        }
    }

    @Override
    public void handleData(byte[] data, int size) {
        try {
            byte[] buffer = new byte[size];
            System.arraycopy(data, 0, buffer, 0, size);
            serialPort.writeBytes(buffer);
        } catch (SerialPortException e) {
            logger.error("Could not send data", e);
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            if (0 < serialPort.getInputBufferBytesCount()) {
                byte[] data = serialPort.readBytes();
                getController().send(data, data.length);
            }
        } catch (SerialPortException e) {
            logger.error("Could not read data", e);
        }
    }
}
