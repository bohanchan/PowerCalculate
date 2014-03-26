package com.example.powercalculate;

import com.example.powercalculate.MyService.MyBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private Spinner powerSelector;
	private Button btnStart;
	private EditText edtNowPower, edtMaxPower;
	private EditText edtTime;

	private int numPowerPerSecond; // power per time
	private int numMaxPower, numNowPower; // max power and now power

	private int totalTime;

	//private MyCountDownTimer countDownTimer;
	private boolean start; // check weather started

	private final int NOTI_ID = 100; // notification id
	
	private Vibrator vibrator;
	
	private MyService myService;
	//private boolean isBound;
	private MyServiceConnection myServiceConnection;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupCompomentView();
	}

	private void setupCompomentView()
	{
		spinnerInitialize(); // initialize the spinner

		// initialize the components
		btnStart = (Button) findViewById(R.id.btnStart);
		edtNowPower = (EditText) findViewById(R.id.edtNowPower);
		edtMaxPower = (EditText) findViewById(R.id.edtMaxPower);
		edtTime = (EditText) findViewById(R.id.hours);

		// read the integers from the view component
		numPowerPerSecond = getPowerPerSecond();

		// set the listener for now power
		edtNowPower.addTextChangedListener(txtChangeListener);

		// set the listener for max power
		edtMaxPower.addTextChangedListener(txtChangeListener);

		btnStart.setOnClickListener(btnStartListener);

		start = false;

		//isBound = false;
		myServiceConnection = new MyServiceConnection();
	}

	// listener for watch the text ever change
	private TextWatcher txtChangeListener = new TextWatcher()
	{
		public void afterTextChanged(Editable arg0){}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){}

		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
		{
			countTime(); // calculate the remain time
		}

	};

	// initialize the spinner
	private void spinnerInitialize()
	{
		powerSelector = (Spinner) findViewById(R.id.powerSelector);

		ArrayAdapter<CharSequence> adapPowerSelector = ArrayAdapter.createFromResource(this, R.array.time, R.layout.spinner_layout);

		powerSelector.setAdapter(adapPowerSelector);

		powerSelector.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				numPowerPerSecond = getPowerPerSecond();
				countTime();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0){}
		});
	}

	// get the spinner text(one power per time)
	private int getPowerPerSecond()
	{
		return Integer.parseInt(powerSelector.getSelectedItem().toString());
	}

	/*
	 * calculate the difference of the time and display it
	 */
	private void countTime()
	{
		String maxPower = edtMaxPower.getText().toString();
		String nowPower = edtNowPower.getText().toString();

		// check the empty string
		if (!maxPower.equals("") && !nowPower.equals(""))
		{
			numMaxPower = Integer.parseInt(maxPower);
			numNowPower = Integer.parseInt(nowPower);

			// �P�_�̤j��O�|���|�p��̤j��O�y���t���ɶ�
			if (numMaxPower < numNowPower)
			{
				// ��toast�����O���~���D
				Toast.makeText(MainActivity.this, R.string.wrongTime, Toast.LENGTH_SHORT).show();
			}
			else
			{
				totalTime = (numMaxPower - numNowPower) * numPowerPerSecond;

				edtTime.setText(getHoursString());
			}
		}
	}

	// get the remain time
	private String getHoursString()
	{
		return (Integer.toString(totalTime / 60) + ":"
			  + Integer.toString(totalTime % 60) + ":00");
	}

	// listener for button
	private Button.OnClickListener btnStartListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View arg0)
		{
			if (start)
			{
				unbindService(myServiceConnection);
				
				end();
				
				//Toast.makeText(MainActivity.this, "UnBindService", Toast.LENGTH_LONG).show();
			}
			else
			{
				// bind service
				Intent intent = new Intent(MainActivity.this, MyService.class);
				intent.putExtra("TotalTime", totalTime);
				bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
				
				//Toast.makeText(MainActivity.this, "BindService", Toast.LENGTH_LONG).show();

				btnStart.setText("����");
			}

			setUpBroadcastReceiver();
			
			setStart();
		}
	};
	
	private void setUpBroadcastReceiver()
	{
		IntentFilter intentFilter = new IntentFilter("time.CountDownTimer.powerCalculate");
		myBroadcastReceiver = new MyBroadcastReceiver();
		registerReceiver(myBroadcastReceiver, intentFilter);
		
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		end();
	}

	// �{������ �פ�Ҧ�����
	public void end()
	{
		if (vibrator != null)
			vibrator.cancel();
		btnStart.setText("�}�l�˼�");
		edtTime.setText(getHoursString());

		vibrator = null;

		// ���notification�A�Ȩõ���
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(NOTI_ID);
	}

	// change the state of start flag
	public void setStart()
	{
		start = !start;
	}
	
	// set notification
	private void showNotification(String string)
	{
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
		
		notificationBuilder.setContentTitle("��O�p�⾹���椤");
		notificationBuilder.setContentText(string);
		notificationBuilder.setTicker("��O�p�⾹���椤");
		notificationBuilder.setSmallIcon(R.drawable.bear);
		

		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		////////////////////////�d�@�U�쩳�ѼƬOԣ
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTI_ID, notificationBuilder.build()); // �e�X�T���åB�]�wnotification�s��
	}

	// connection for service
	class MyServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder)
		{
			MyBinder binder = (MyBinder) iBinder;
			myService = binder.getService();
			//isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			//isBound = false;
		}
	}
	
	class MyBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String time = intent.getStringExtra("remain_time");
			edtTime.setText(time);
			showNotification(time);
		}
	}
}
