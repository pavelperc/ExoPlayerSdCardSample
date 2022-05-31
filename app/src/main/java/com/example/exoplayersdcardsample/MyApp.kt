package com.example.exoplayersdcardsample

import android.app.Application
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

class MyApp : Application() {

    companion object {
        lateinit var primaryDir: File
        lateinit var sdCardDir: File

        lateinit var simpleCachePrimary: SimpleCache
        lateinit var simpleCacheSdCard: SimpleCache

        lateinit var downloadManagerPrimary: DownloadManager
        lateinit var downloadManagerSdCard: DownloadManager
    }

    override fun onCreate() {
        super.onCreate()
        primaryDir = getExternalFilesDirs("content")[0]!!
        sdCardDir = getExternalFilesDirs("content")[1]!!

        val databaseProvider = StandaloneDatabaseProvider(this)
        simpleCachePrimary = SimpleCache(primaryDir, NoOpCacheEvictor(), databaseProvider)
        simpleCacheSdCard = SimpleCache(sdCardDir, NoOpCacheEvictor(), databaseProvider)

        downloadManagerPrimary = DownloadManager(
            this,
            databaseProvider,
            simpleCachePrimary,
            DefaultHttpDataSource.Factory(),
            Runnable::run
        )
        downloadManagerSdCard = DownloadManager(
            this,
            databaseProvider,
            simpleCacheSdCard,
            DefaultHttpDataSource.Factory(),
            Runnable::run
        )
    }
}