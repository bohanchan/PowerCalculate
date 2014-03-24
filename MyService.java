package com.example.powercalculate;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service
{
	private final String SERVICE_TAG = "SERVICE_TAG";
	
	private IBinder myBinder = new MyBinder();
	
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
		System.out.println("WTF");
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
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i(SERVICE_TAG , "Service onBind()");
		Toast.makeText(this, intent.getStringExtra("TotalTime"), Toast.LENGTH_LONG).show();
		return myBinder;
	}
}
