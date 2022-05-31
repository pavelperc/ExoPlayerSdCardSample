package com.example.exoplayersdcardsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.updateLayoutParams
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val BUNNY_URL = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

    private val player by lazy {
        ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    CacheDataSource.Factory()
                        .setCache(MyApp.simpleCachePrimary)
                        .setCacheWriteDataSinkFactory(null)
                        .setUpstreamDataSourceFactory(
                            CacheDataSource.Factory()
                                .setCache(MyApp.simpleCacheSdCard)
                                .setCacheWriteDataSinkFactory(null)
                                .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                        )
                )
            )
            .build()
    }

    lateinit var thread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView.post {
            playerView.updateLayoutParams {
                height = playerView.width * 9 / 16
            }
        }

        playerView.player = player
        player.setMediaItem(MediaItem.fromUri(BUNNY_URL))
        player.prepare()

        buttonDownloadPrimary.setOnClickListener {
            DownloadService.sendAddDownload(
                this,
                PrimaryDownloadService::class.java,
                DownloadRequest.Builder(BUNNY_URL, BUNNY_URL.toUri()).build(),
                false
            )
        }
        buttonDownloadSdCard.setOnClickListener {
            DownloadService.sendAddDownload(
                this,
                SdCardDownloadService::class.java,
                DownloadRequest.Builder(BUNNY_URL, BUNNY_URL.toUri()).build(),
                false
            )
        }
        buttonDeletePrimary.setOnClickListener {
            DownloadService.sendRemoveAllDownloads(this, PrimaryDownloadService::class.java, false)
        }
        buttonDeleteSdCard.setOnClickListener {
            DownloadService.sendRemoveAllDownloads(this, SdCardDownloadService::class.java, false)
        }

        thread = thread {
            while (true) {
                Thread.sleep(100)
                val primarySize = MyApp.primaryDir.walkTopDown().sumOf { it.length() } / 1024 / 1024
                val sdCardSize = MyApp.sdCardDir.walkTopDown().sumOf { it.length() } / 1024 / 1024

                runOnUiThread {
                    textViewPrimarySize.text = "${MyApp.primaryDir.absolutePath}: ${primarySize}MB"
                    textViewSdCardSize.text = "${MyApp.sdCardDir.absolutePath}: ${sdCardSize}MB"
                }
            }
        }

    }

    override fun onDestroy() {
        player.stop()
        thread.interrupt()
        super.onDestroy()
    }
}