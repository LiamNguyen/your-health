package fi.letsdev.yourhealth.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.libelium.mysignalsconnectkit.BluetoothManagerHelper;
import com.libelium.mysignalsconnectkit.BluetoothManagerService;
import com.libelium.mysignalsconnectkit.callbacks.BluetoothManagerCharacteristicsCallback;
import com.libelium.mysignalsconnectkit.callbacks.BluetoothManagerHelperCallback;
import com.libelium.mysignalsconnectkit.callbacks.BluetoothManagerQueueCallback;
import com.libelium.mysignalsconnectkit.callbacks.BluetoothManagerServicesCallback;
import com.libelium.mysignalsconnectkit.pojo.LBSensorObject;
import com.libelium.mysignalsconnectkit.utils.BitManager;
import com.libelium.mysignalsconnectkit.utils.LBValueConverter;
import com.libelium.mysignalsconnectkit.utils.StringConstants;
import com.libelium.mysignalsconnectkit.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fi.letsdev.yourhealth.utils.Constants;

class MySignalSensorService implements
	BluetoothManagerServicesCallback,
	BluetoothManagerCharacteristicsCallback,
	BluetoothManagerQueueCallback,
	BluetoothManagerHelperCallback
{

	private static MySignalSensorService instance;

	private MySignalSensorService(Context context) {
		// Prepare mysignals service

		this.context = context;
		try {
			mService = BluetoothManagerService.getInstance();
			mService.initialize(context);
			mService.setServicesCallback(this);
			mService.setCharacteristicsCallback(this);
			mService.setQueueCallback(this);
		} catch (Exception e) {
			Log.d(TAG, "Failed when setting up listener");
		}

		// Prepare intent for MySignalsDataReceiver
		broadcastIntent = new Intent();
		broadcastIntent.setAction(Constants.IntentActions.MYSIGNAL_HR_DATA_RECEIVE);
	}

	public static MySignalSensorService getInstance(Context context) {
		if (instance == null) {
			instance = new MySignalSensorService(context);
		}
		return instance;
	}

	private final static String TAG = MySignalSensorService.class.getSimpleName();
	private final static String kMySignalsId = Constants.MYSIGNALS_ID;

	private Context context;
	private BluetoothManagerService mService;
	private BluetoothManagerHelper bluetoothManager;
	private BluetoothDevice selectedDevice;
	private boolean writtenService;
	private ArrayList<BluetoothGattCharacteristic> notifyCharacteristics;
	private ArrayList<LBSensorObject> selectedSensors;
	private BluetoothGattService selectedService;
	private BluetoothGattCharacteristic characteristicSensorList;
	private final Intent broadcastIntent;
	private Integer currentBpm = 0;

	void startService() {
		scanBluetoothDevices();
		createInterface();
	}

	private void scanBluetoothDevices() {
		bluetoothManager = BluetoothManagerHelper.getInstance();
		bluetoothManager.setInitParameters(this, this.context);

		if (bluetoothManager.getBluetoothAdapter(context) == null) return;

		List<BluetoothDevice> devicesBonded = bluetoothManager.getBondedDevices();
		if (devicesBonded.size() > 0) {
			selectedDevice = null;
			for (BluetoothDevice deviceItem : devicesBonded) {
				String name = deviceItem.getName();
				if (name != null) {
					if (name.toLowerCase().contains(kMySignalsId)) {
						Log.d(TAG, "Address: " + name);
						this.selectedDevice = deviceItem;
						break;
					} }
			}
			if (selectedDevice != null) {
				performConnection();
			} else {
				bluetoothManager.startLEScan(true);
			}
		} else {
			bluetoothManager.startLEScan(true);
		}
	}

	private void createInterface() {

		if (BluetoothManagerHelper.hasBluetooth(this.context)) {
			writtenService = false;
			notifyCharacteristics = new ArrayList<>();
			selectedSensors = createSensorsDisplay();
			selectedDevice = null;
		} else {
			Log.d(TAG, "The device does not have BLE technology, please use an Android device with BLE technology.");
		}
	}

	private ArrayList<LBSensorObject> createSensorsDisplay() {

		int maxNotifications = Utils.getMaxNotificationNumber();

		ArrayList<LBSensorObject> sensors = new ArrayList<>();

		LBSensorObject object = LBSensorObject.newInstance();

		object.tag = 1;
		object.tickStatus = true;
		object.uuidString = StringConstants.kUUIDECGSensor;

		LBSensorObject.preloadValues(object);
		sensors.add(object);

		object = LBSensorObject.newInstance();

		object.tag = 2;
		object.tickStatus = (maxNotifications > 7);
		object.uuidString = StringConstants.kUUIDPulsiOximeterSensor;

		LBSensorObject.preloadValues(object);
		sensors.add(object);

		return sensors;
	}

	@Override
	public void onListDevicesFound(ArrayList<BluetoothDevice> devices) {
		for (BluetoothDevice deviceItem : devices) {
			String name = deviceItem.getName();

			if (name != null) {
				if (name.toLowerCase().contains(kMySignalsId)) {
					Log.d(TAG, "Address: " + name);
					this.selectedDevice = deviceItem;
					break;
				}
			}
		}

		if (selectedDevice != null) {

			bluetoothManager.stopLeScan();

			boolean bonded = mService.startBonding(selectedDevice);

			if (bonded) {

				Log.d(TAG, "Bonding starting...");
			}
		}
	}

	private void performConnection() {
		final Handler handler = new Handler();
		final Runnable postExecution = new Runnable() {
			@Override
			public void run() {
				try {
					if (mService != null) {
						if (mService.discoverServices()) {
							if (selectedDevice != null) {
								Log.d(TAG, "Device discoverServices: " + selectedDevice.getAddress());
							}
						}
					}
				} catch (Exception e) {
					Log.d(TAG, "Failed to perform connection with MySignals device\n" + e);
				}
			}
		};

		if (mService.connectToDevice(selectedDevice, this.context)) {
			Log.d(TAG, "Device connected!!");
			handler.postDelayed(postExecution, 2000);
		}
	}

	@Override
	public void onManagerDidNotFoundDevices() {
		Log.d(TAG, "Device MySignals not found!!!");
	}

	@Override
	public void onBondAuthenticationError(BluetoothGatt gatt) {

		Log.d(TAG, "Bonding authentication error!!!");
	}

	@Override
	public void onBonded() {
		performConnection();
	}

	@Override
	public void onBondedFailed() {
		Log.d(TAG, "Bonded failed!!!");
	}

	@Override
	public void onConnectedToDevice(BluetoothDevice device, int status) {
		if (device == null) return;
		Log.d(TAG, "Device " + device.getName() + " " + device.getAddress() + " connected!!\nStatus: " + status);
	}

	@Override
	public void onServicesFound(List<BluetoothGattService> services) {
		if (services != null) {
			selectedService = null;

			for (BluetoothGattService service : services) {
				String uuidService = service.getUuid().toString().toUpperCase();

				if (uuidService.equals(StringConstants.kServiceMainUUID)) {
					selectedService = service;
					break;
				}
			}

			if (selectedService != null) {
				writtenService = false;
				mService.readCharacteristicsForService(selectedService);
			}
		}
	}

	@Override
	public void onDisconnectFromDevice(BluetoothDevice device, int newState) {
		Log.d(TAG, "Device disconnected!!");
	}

	@Override
	public void onReadRemoteRssi(int rssi, int status) {

		Log.d(TAG, "RSSI: " + rssi + " dBm - Status: " + status);
	}

	@Override
	public void onCharacteristicsFound(List<BluetoothGattCharacteristic> characteristics, BluetoothGattService service) {
		if (service.getUuid().toString().toUpperCase().equals(StringConstants.kServiceMainUUID)) {
			if (!writtenService) {
				characteristicSensorList = null;
				writtenService = true;

				for (BluetoothGattCharacteristic characteristic : characteristics) {
					if (characteristic.getUuid().toString().toUpperCase().equals(StringConstants.kSensorList)) {
						characteristicSensorList = characteristic;
						break;
					}
				}

				if (characteristicSensorList != null) {
					BitManager bitManager = BitManager.newObject();
					bitManager.objectByte = BitManager.createByteObjectFromSensors(selectedSensors, BitManager.BLUETOOTH_DISPLAY_MODE.BLUETOOTH_DISPLAY_MODE_GENERAL, this.context);

					byte[] data = BitManager.convertToData(bitManager.objectByte);

					mService.writeCharacteristicQueue(characteristicSensorList, data);
				}
			}
		}
	}

	@Override
	public void onCharacteristicWritten(BluetoothGattCharacteristic characteristic, int status) {}

	@Override
	public void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
		readCharacteristic(characteristic);
	}

	@Override
	public void onCharacteristicSubscribed(BluetoothGattCharacteristic characteristic, boolean isUnsubscribed) {
		if (isUnsubscribed) {
			Log.d(TAG, "Unsubscribed from characteristic!!");
		} else {
			Log.d(TAG, "Subscribed to characteristic!!");
		}
	}

	@Override
	public void onFinishWriteAllCharacteristics() {}

	@Override
	public void onStartWriteCharacteristic(BluetoothGattCharacteristic characteristic, int status) {
		if (status != BluetoothGatt.GATT_SUCCESS) {
			Log.d(TAG, "writing characteristic error: " + status + " - " + characteristic.getUuid().toString().toUpperCase());
		} else {
			String uuid = characteristic.getService().getUuid().toString().toUpperCase();

			if (uuid.equals(StringConstants.kServiceMainUUID)) {
				for (BluetoothGattCharacteristic charac : notifyCharacteristics) {
					mService.writeCharacteristicSubscription(charac, false);
				}

				notifyCharacteristics.clear();

				for (BluetoothGattCharacteristic charac : selectedService.getCharacteristics()) {
					for (LBSensorObject sensor : selectedSensors) {
						if (sensor.uuidString.toUpperCase().equals(charac.getUuid().toString().toUpperCase()) && sensor.tickStatus) {
							notifyCharacteristics.add(charac);
							mService.writeCharacteristicSubscription(charac, true);
						}
					}
				}
			}
		}
	}

	@Override
	public void onStartReadCharacteristic(BluetoothGattCharacteristic characteristic) {}

	@Override
	public void onStartWriteQueueDescriptor(BluetoothGattDescriptor descriptor) {}

	@Override
	public void onFinishWriteAllDescriptors() {
		if (characteristicSensorList != null) {
			BitManager bitManager = BitManager.newObject();
			bitManager.objectByte = BitManager.createByteObjectFromSensors(selectedSensors, BitManager.BLUETOOTH_DISPLAY_MODE.BLUETOOTH_DISPLAY_MODE_GENERAL, this.context);

			byte[] data = BitManager.convertToData(bitManager.objectByte);

			mService.writeCharacteristicQueue(characteristicSensorList, data);
		}
	}

	@Override
	public void onFinishReadAllCharacteristics() {}

	@Override
	public void onCharacteristicChangedQueue(BluetoothGattCharacteristic characteristic) {
		readCharacteristic(characteristic);
	}

	// Receive data from MySignals sensors and broadcast if there are changes

	private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		try {
			String uuid = characteristic.getUuid().toString().toUpperCase();

			byte[] value = characteristic.getValue();

			if (value == null) return;

			// Pulsioximeter data
			if (uuid.equals(StringConstants.kUUIDPulsiOximeterSensor)) {
				HashMap<String, String> dataDict = LBValueConverter.manageValuePulsiOximeter(value);

				if (uuid.equals(StringConstants.kUUIDPulsiOximeterSensor)) {
					Log.d(TAG, "kUUIDPulsiOximeterSensor dict: " + dataDict.get("1"));

					Integer bpm = Integer.parseInt(dataDict.get("1"));

					if (isBpmEnterWarningRange(bpm) || isNewBpmCreateMuchDifferentThanCurrentBpm(bpm)) {
						currentBpm = bpm;

						broadcastIntent.putExtra(Constants.IntentExtras.BPM, currentBpm);
						context.sendBroadcast(broadcastIntent);
					}
				}
			}
		} catch (Exception e) {
			Log.d(TAG, "Read characteristic failed: " + e);
		}
	}

	private boolean isBpmEnterWarningRange(Integer bpm) {
		return bpm < 30 || bpm > 140;
	}

	private boolean isNewBpmCreateMuchDifferentThanCurrentBpm(Integer bpm) {
		return bpm < currentBpm - 3 || bpm > currentBpm + 3;
	}
}
