package com.example.paatu

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paatu.databinding.MusicItemBinding

class MusicAdapter(var musicList: ArrayList<Music>, val itemClickListener: MusicItemClickListener):
    RecyclerView.Adapter<MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {

        val musicItemBinding =
            MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(musicItemBinding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {

        holder.setData(musicList[position], itemClickListener)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(musicList: ArrayList<Music>) {

//        this.contactList.clear()
        this.musicList = musicList
        notifyDataSetChanged()
    }

}

class MusicViewHolder(val binding: MusicItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun setData(music: Music, itemClickListener: MusicItemClickListener) {

//        val image = getImage(music.songURI.toString())

        binding.apply {

            tvSongTitle.apply {
                text = music.name
            }
//            ivLogo.apply {
//                if(image != null) {
//                    Glide.with(binding.root.context).asBitmap()
//                        .load(image)
//                        .into(this)
//                }
//                else {
//                    Glide.with(binding.root.context).asBitmap()
//                        .load(R.drawable.astronaut)
//                        .into(this)
//                }
//            }

            clItem.setOnClickListener {
                itemClickListener.onItemClicked(adapterPosition, music)
            }

        }

    }

    private fun getImage(songURI: String): ByteArray? {

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(songURI)
        val imageAsBitmap = retriever.embeddedPicture
        retriever.release()
        return imageAsBitmap
    }


}
