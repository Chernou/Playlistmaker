package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val shareAppTextView = findViewById<TextView>(R.id.share_app)
        val supportTextView = findViewById<TextView>(R.id.support)
        val userAgreementTextView = findViewById<TextView>(R.id.user_agreement)

        shareAppTextView.setOnClickListener {
            val sentLink = getString(R.string.practicum_android_link)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, sentLink)
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        supportTextView.setOnClickListener {
            val email = getString(R.string.feedback_addressee_mail)
            val subject = getString(R.string.feedback_subject)
            val textMessage = getString(R.string.feedback_message_text)
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, textMessage)
                startActivity(this)
            }
        }

        userAgreementTextView.setOnClickListener {
            val link = getString(R.string.practicum_offer_link)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(userAgreementIntent)
        }
    }
}