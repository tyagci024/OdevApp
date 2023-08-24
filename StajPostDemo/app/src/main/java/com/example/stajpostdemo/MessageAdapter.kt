package com.example.stajpostdemo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messages: MutableList<Any> = mutableListOf()) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.text_sent_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        when (val message = messages[position]) {
            is PostData -> holder.textView.text = message.question
            is ResponseData -> {
                holder.textView.text = message.answer
                holder.textView.setBackgroundResource(R.drawable.dark_chat_bubble)
                holder.textView.setTextColor(Color.WHITE)
            }
        }
    }



    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Any) {
        if (message is PostData || message is ResponseData) {
            messages.add(message)
            notifyItemInserted(messages.size - 1)
        } else {
            throw IllegalArgumentException("Unsupported type")
        }
    }

    fun addResponseMessage(message: ResponseData) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
