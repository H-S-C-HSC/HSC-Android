package com.example.hsc_android.network.Ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hsc_android.R
import com.example.hsc_android.network.Data.voice
import com.example.hsc_android.network.Data.voiceItem
import com.example.hsc_android.network.network.HscAPI
import com.example.hsc_android.network.network.HscConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class voiceAdapter(private val voiceList: ArrayList<voice>) : RecyclerView.Adapter<voiceAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val title = itemView.findViewById<TextView>(R.id.voiceItem_title)
        val number = itemView.findViewById<TextView>(R.id.voiceItem_number)

        fun bind(item: voice){
            title.text = item.title
            number.text = item.id.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = voiceList[position]

        holder.itemView.setOnClickListener {
            data.id.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = HscConnector.getAPI().choiVoice(it)
                    response.body()?.let {Log.d("MYTAG", it.message)}
                }
            }
        }

        holder.bind(voiceList[position])

    }

    override fun getItemCount(): Int = voiceList.size

}
