package com.example.paatu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MusicProgressReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null && intent.action != null) {
            val activityIntent = Intent(context, MainActivity::class.java)
            Toast.makeText(context, intent.getStringExtra("MediaPlayerProgress"), Toast.LENGTH_SHORT).show()
            activityIntent.putExtra("MediaPlayerProgress", intent.getStringExtra("MediaPlayerProgress"))
            context!!.startActivity(activityIntent)
        }
    }
}