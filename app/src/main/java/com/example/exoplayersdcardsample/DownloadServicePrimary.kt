package com.example.exoplayersdcardsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper


class PrimaryDownloadService : AbstractDownloadService()
class SdCardDownloadService : AbstractDownloadService()

sealed class AbstractDownloadService : DownloadService(NOTIFICATION_ID) {
    companion object {
        const val NOTIFICATION_ID = 123
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel", "channel", importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun getScheduler(): Scheduler? = null

    override fun getDownloadManager() = when (this) {
        is PrimaryDownloadService -> MyApp.downloadManagerPrimary
        is SdCardDownloadService -> MyApp.downloadManagerSdCard
    }

    override fun getForegroundNotification(downloads: MutableList<Download>, notMetRequirements: Int): Notification =
        DownloadNotificationHelper(this, "channel").buildProgressNotification(
            this,
            android.R.drawable.stat_sys_download,
            null,
            this::class.simpleName,
            downloads,
            notMetRequirements
        )

}