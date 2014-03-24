package com.example.powercalculate;

import android.app.Service;
import android.os.CountDownTimer;
import android.os.Vibrator;

class MyCountDownTimer extends CountDownTimer
{
	private static final int VIBRATOR_TIME = 1000;
	private Vibrator vibrator;
	
	public MyCountDownTimer(long millisInFuture, long countDownInterval, Vibrator vibrator)
	{
		super(millisInFuture, countDownInterval);
		this.vibrator = vibrator;
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
		
		//end();
	}

	@Override
	public void onTick(long millisUntilFinished)
	{
		String str = "離滿體時間約剩餘：" + Long.toString(millisUntilFinished/1000/60)+ " : " + Long.toString(millisUntilFinished/1000%60);
		//showNotification(str);
		//edtHours.setText(str);
	}
}