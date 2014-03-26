PowerCalculate
==============

體力計算器

1.又澤說Notification可以在Service中使用import android.content.ContextWrapper 的 getBaseContext()方法
  解決intent.setClass(this, MainActivity.class);不在activity中無法使用this的問題

2.上面問題沒找到解決方法所以將showNotification方法移回MainActivity內
  在MainActivity中接收到broadcast時更新Notification
  
3.在V4版本後不建議使用Notification去new一個notification物件，開發團隊建議改變使用NotificationCompat.Builder
  參考官方Android Developer網頁：http://developer.android.com/guide/topics/ui/notifiers/notifications.html