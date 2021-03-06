package br.com.anderltda.messenger.adapter

import android.view.ViewGroup
import br.com.anderltda.messenger.data.entity.User
import br.com.anderltda.messenger.viewholder.ContactViewHolder
import com.commit451.firestoreadapter.FirestoreAdapter
import com.commit451.firestoreadapter.QueryCreator


class ContactAdapter(query: QueryCreator) : FirestoreAdapter<User, ContactViewHolder>(
    User::class.java, query) {

    var onDeleteListener: ((position: Int) -> Unit)? = null

    var onUpListener: ((position: Int) -> Unit)? = null

    var onClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val holder = ContactViewHolder.inflate(parent)

        /*holder.buttonDelete.setOnClickListener {

            val position = holder.adapterPosition

            onDeleteListener?.invoke(position)

        }
        holder.buttonUp.setOnClickListener {

            val position = holder.adapterPosition

            onUpListener?.invoke(position)

        }*/
        holder.itemView.setOnClickListener {

            val position = holder.adapterPosition

            onClickListener?.invoke(position)

        }

        return holder
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(get(position))
    }
}