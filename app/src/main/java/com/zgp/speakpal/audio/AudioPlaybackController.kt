package com.zgp.speakpal.audio

import android.media.MediaPlayer
import java.io.File

class AudioPlaybackController {
    private var player: MediaPlayer? = null

    fun play(path: String, onFinished: () -> Unit = {}): Result<Unit> = runCatching {
        check(File(path).exists()) { "录音文件不存在" }
        release()

        player = MediaPlayer().apply {
            setDataSource(path)
            setOnCompletionListener {
                release()
                onFinished()
            }
            setOnErrorListener { _, _, _ ->
                release()
                onFinished()
                true
            }
            prepare()
            start()
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
