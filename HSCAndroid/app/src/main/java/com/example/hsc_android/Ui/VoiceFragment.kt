package com.example.hsc_android.network.Ui

import android.graphics.Bitmap
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hsc_android.R
import com.example.hsc_android.network.network.HscConnector
import kotlinx.android.synthetic.main.fragment_voice.*
import kotlinx.android.synthetic.main.fragment_voice.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Exception

class VoiceFragment : Fragment() {
    var mediaRecorder: MediaRecorder? = null
    private var count = 1
    private var getAudioFile: File? = null
    private lateinit var file: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_voice, container, false)

        view.voice_recording_btn.setOnClickListener {
            count++
            recording(count)
        }
        view.voice_photo_imageView.setOnClickListener {
            voice_photo_imageView.let {
                if(it.visibility != View.GONE) it.visibility = View.GONE
            }
        }
        setInit()
        return view
    }

    fun setImage(bitmap: Bitmap){
        CoroutineScope(Dispatchers.Main).launch {
            voice_photo_imageView?.visibility = View.VISIBLE
            voice_photo_imageView?.setImageBitmap(bitmap)
        }
    }

    private fun setInit() {
        /*file = File(Environment.getExternalStorageDirectory(), "hsc.mp3")*/

        CoroutineScope(Dispatchers.IO).launch {
            val result = HscConnector.getAPI().voiceList()
            result.body()?.let{
                val adapter = result.body()?.let {
                    voiceAdapter(it.voices)
                }

                withContext(Dispatchers.Main) {
                    voice_recyclerView.adapter = adapter
                    voice_recyclerView.layoutManager = LinearLayoutManager(context)
                }
            }
        }

        if(result.isSuccessful && result.code() == 200){
            val adapter = result.body()?.let {
                voiceAdapter(it)
            }
            voice_recyclerView.adapter = adapter
            voice_recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            Log.e("voice",result.message())
        }


    }

    private fun recording(count: Int) {
        if (count % 2 == 0) {

            startRecording()

        } else if (count % 2 == 1) {
            stopRecording()
            postVoice()
        }
    }

    private fun postVoice() {
        var title = view?.voice_title_edit.toString()
        var soundPart: MultipartBody.Part

        var titlePart = MultipartBody.Part.createFormData(
            "title",
            title
        )

        soundPart = MultipartBody.Part.createFormData(
            "sound",
            file.name,
            getAudioFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        CoroutineScope(Dispatchers.IO).launch {
            val result = HscConnector.getAPI().saveVoice(titlePart, soundPart)

            if (result.isSuccessful) {
                Log.e("voice send", result.message())
            } else {
                Log.e("voice send", result.message())
            }
        }

    }

    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.let {
                it.setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
                it.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.DEFAULT)
                it.setOutputFile(file.absolutePath)
                it.prepare()
                it.start()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun stopRecording() {
        mediaRecorder?.let {
            it.stop()
            it.release()
        }
        mediaRecorder = null
    }
}