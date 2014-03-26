PowerCalculate
==============

體力計算器

1.showNotification方法移回MainActivity內
2.在MainActivity中接收到broadcast時更新Notification
  在V4版本後不建議使用Notification去new一個notification物件，開發團隊建議改變使用NotificationCompat.Builder
  參考官方Android Developer網頁：http://developer.android.com/guide/topics/ui/notifiers/notifications.html