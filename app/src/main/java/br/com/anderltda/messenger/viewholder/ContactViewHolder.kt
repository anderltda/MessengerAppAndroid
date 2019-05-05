package br.com.anderltda.messenger.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.anderltda.messenger.R
import br.com.anderltda.messenger.data.entity.User
import java.text.SimpleDateFormat
import java.util.*

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun inflate(parent: ViewGroup): ContactViewHolder {
            return ContactViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
            )
        }
    }

    var name: TextView = itemView.findViewById(R.id.tv_user_name)

    var create: TextView = itemView.findViewById(R.id.tv_time)

    var number:  TextView = itemView.findViewById(R.id.tv_number)

    var online: View = itemView.findViewById(R.id.online_indicator)

    fun bind(user: User) {

        //Nome do contato
        name.text = user.name

        //Data da ultima mensagem enviada por esse contato, sem implementação, pegando a data de criação do usuario
        create.text = SimpleDateFormat("h:mm a").format(user.create!!)

        //Number de mensagens não lida, ainda sem implementação valor fixo
        number.text = "2"

        //Mostrar se o contato está online, sem implementação monstrado fixo que não está online
        online.setBackground(ContextCompat.getDrawable(online.context, R.drawable.bg_no_online));
    }
}