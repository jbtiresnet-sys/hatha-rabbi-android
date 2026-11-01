package org.itsmygod.hatharabbi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "new_content";

    @Override public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        String title = "هذا ربي";
        String body = "محتوى جديد في المكتبة";
        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) title = message.getNotification().getTitle();
            if (message.getNotification().getBody() != null) body = message.getNotification().getBody();
        }
        if (message.getData().containsKey("title")) title = message.getData().get("title");
        if (message.getData().containsKey("body")) body = message.getData().get("body");
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "أخبار المكتبة", NotificationManager.IMPORTANCE_DEFAULT));
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon).setContentTitle(title).setContentText(body)
                .setAutoCancel(true).setContentIntent(pending).setPriority(NotificationCompat.PRIORITY_DEFAULT);
        manager.notify((int)(System.currentTimeMillis() % Integer.MAX_VALUE), builder.build());
    }
}
