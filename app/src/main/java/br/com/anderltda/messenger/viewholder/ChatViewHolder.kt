package br.com.anderltda.messenger.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.anderltda.messenger.R
import br.com.anderltda.messenger.model.Chat
import java.text.SimpleDateFormat

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun inflate(parent: ViewGroup, position: Int): ChatViewHolder {
            when (position) {
                1 -> {
                    return ChatViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_chat_you, parent, false)
                    )
                } else -> {
                    return ChatViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_chat_me, parent, false)
                    )
                }
            }
        }
    }

    var message: TextView = itemView.findViewById(R.id.tv_chat_text)

    var time: TextView = itemView.findViewById(R.id.tv_time)

    //var buttonDelete: View = itemView.findViewById(R.id.button_delete)

    //var buttonUp: View = itemView.findViewById(R.id.button_up)

    fun bind(chat: Chat) {

        //Hora do envio da mensagem
        time.text = SimpleDateFormat("h:mm a").format(chat.time)

        //Mensagem enviada
        message.text = chat.message

    }
}