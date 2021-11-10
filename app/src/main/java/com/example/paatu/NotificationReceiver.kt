package com.example.paatu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val answerBundle = intent!!.extras
        val userAnswer = answerBundle!!.getInt("userAnswer")
        if (userAnswer == 1) {
            Log.d("shuffTest", "Prev")
        } else if (userAnswer == 2) {
            Log.d("shuffTest", "Play")
        } else if (userAnswer == 3) {
            Log.d("shuffTest", "Next")
        }
    }
}