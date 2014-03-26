package com.example.powercalculate;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service
{
	private final String SERVICE_TAG = "SERVICE_TAG";
	private IBinder myBinder = new MyBinder();
	private MyCountDownTimer countDownTimer;
	private int totalTime;
	private Vibrator vibrator;
	private static final int VIBRATOR_TIME = 1000;
	
	class MyBinder extends Binder
	{
		MyService getService()
		{
			return MyService.this;
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(SERVICE_TAG , "Service onCreate()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(SERVICE_TAG , "Service onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(SERVICE_TAG , "Service onDestroy()");
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(SERVICE_TAG , "Service onUnbind()");
		countDownTimer.cancel();
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i(SERVICE_TAG , "Service onBind()");
		
		totalTime = intent.getExtras().getInt("TotalTime");
		Toast.makeText(this, "total time : " + Integer.toString(totalTime), Toast.LENGTH_LONG).show();
		
		vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		
		startCount();
		
		return myBinder;
	}
	
	private void startCount()
	{
		countDownTimer = new MyCountDownTimer(totalTime /** 60*/ * 1000, 1000);
		countDownTimer.start();
	}
	
	private class MyCountDownTimer extends CountDownTimer
	{
		public MyCountDownTimer(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish()
		{
			vibrator.vibrate(VIBRATOR_TIME);
			try
			{
				Thread.sleep(VIBRATOR_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			// end();
		}

		@Override
		public void onTick(long millisUntilFinished)
		{
			String str = "離滿體時間約剩餘："
					+ Long.toString(millisUntilFinished / 1000 / 60) + " : "
					+ Long.toString(millisUntilFinished / 1000 % 60);
			
			//set Broadcast sent the time String to Activity
			Intent intent = new Intent("time.CountDownTimer.powerCalculate");
			intent.putExtra("remain_time", str);
			sendBroadcast(intent);
			
			// showNotification(str);
			// edtHours.setText(str);
		}
	}
}
