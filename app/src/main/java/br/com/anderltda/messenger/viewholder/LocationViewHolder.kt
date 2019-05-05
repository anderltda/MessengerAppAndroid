package br.com.anderltda.messenger.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.anderltda.messenger.R
import br.com.anderltda.messenger.data.entity.Address

class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            return LocationViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact_location, parent, false)
            )
        }
    }

    var name: TextView = itemView.findViewById(R.id.tv_user_name)

    fun bind(address: Address) {

        //Nome do contato que deseja ser visto no mapa
        name.text = address.name
    }
}