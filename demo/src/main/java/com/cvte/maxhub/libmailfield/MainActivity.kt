package com.cvte.maxhub.libmailfield

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cvte.maxhub.mailfield.EmailTagView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var emailTagView = findViewById<EmailTagView>(R.id.tagViewEmail)
        emailTagView.setRecipientLimit("@cvte.com")

    }

}
