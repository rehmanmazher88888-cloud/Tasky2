package com.example.calmtask.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder {
    private var recorder: AudioRecord? = null
    private var isRecording = false
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    fun startRecording(outputFile: File, onAmplitude: (Float) -> Unit) {
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )
        recorder?.startRecording()
        isRecording = true
        val buffer = ShortArray(minBufferSize)
        Thread {
            val fos = FileOutputStream(outputFile)
            // Write WAV header placeholder (will overwrite at end)
            val headerSize = 44
            fos.write(ByteArray(headerSize))
            var totalDataLen = 0
            while (isRecording) {
                val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    totalDataLen += read * 2
                    fos.write(shortArrayToByteArray(buffer, read))
                    // Calculate amplitude
                    var sum = 0.0
                    for (i in 0 until read) {
                        sum += buffer[i].toDouble() * buffer[i].toDouble()
                    }
                    val rms = Math.sqrt(sum / read) / 32768.0
                    onAmplitude(rms.toFloat() * 2.5f) // scale for UI
                }
            }
            recorder?.stop()
            recorder?.release()
            // Update WAV header
            fos.close()
            updateWavHeader(outputFile, totalDataLen)
        }.start()
    }

    fun stopRecording() {
        isRecording = false
    }

    private fun updateWavHeader(file: File, totalAudioLen: Int) {
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = sampleRate * channels * 16 / 8
        val header = ByteArray(44)
        // RIFF header
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        // fmt chunk
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1 size
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // PCM
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (channels * 16 / 8).toByte()
        header[33] = 0
        header[34] = 16
        header[35] = 0
        // data chunk
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        val raf = java.io.RandomAccessFile(file, "rw")
        raf.seek(0)
        raf.write(header)
        raf.close()
    }

    private fun shortArrayToByteArray(data: ShortArray, items: Int): ByteArray {
        val bytes = ByteArray(items * 2)
        for (i in 0 until items) {
            bytes[i * 2] = (data[i].toInt() and 0xff).toByte()
            bytes[i * 2 + 1] = (data[i].toInt() shr 8 and 0xff).toByte()
        }
        return bytes
    }
}
