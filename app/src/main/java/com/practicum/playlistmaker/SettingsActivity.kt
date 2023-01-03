package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //val backToMain = findViewById<TextView>(R.id.back_to_main)
        //val switchDarkTheme = findViewById<SwitchCompat>(R.id.switch_dark_theme)
        val shareApp = findViewById<TextView>(R.id.share_app)
        val support = findViewById<TextView>(R.id.support)
        val userAgreement = findViewById<TextView>(R.id.user_agreement)

        shareApp.setOnClickListener {
            val sentLink = "https://practicum.yandex.ru/android-developer/"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, sentLink)
            shareIntent.setType("text/plain")
            startActivity(shareIntent)
        }

        support.setOnClickListener {
            val email = "chernov.i.u@gmail.com"
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val textMessage = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, textMessage)
            startActivity(supportIntent)
        }

        userAgreement.setOnClickListener {
            val link = "https://yandex.ru/legal/practicum_offer/"
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(userAgreementIntent)
        }
    }
}