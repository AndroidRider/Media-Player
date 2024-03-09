package com.androidrider.mediaplayer.Activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidrider.mediaplayer.R
import com.androidrider.mediaplayer.Utils.FontUtils
import com.androidrider.mediaplayer.databinding.ActivityFeedbackBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        FontUtils.setAppFont(this)
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarFeedbackActivity)
        setSupportActionBar(toolbar)
        // Enable the Up button (back arrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Assuming you have a button with id "sendButton"
        binding.sendButton.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val email = "dev.rider36@gmail.com"
        val subject = binding.edtSubject.text.toString()
        val message = "Email Address: ${binding.edtEmail.text}\n\nFeedback: ${binding.edtMessage.text}"

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = android.net.Uri.parse("mailto:$email")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(emailIntent)
        } catch (ex: android.content.ActivityNotFoundException) {
            // Handle case where no email app is available
            // You can show a Toast or dialog to inform the user
        }
    }


    // Handle the click on the back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}


//
//class FeedbackActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityFeedbackBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
//        binding = ActivityFeedbackBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        supportActionBar?.title = "Feedback"
//
//
//        binding.sendButton.setOnClickListener {
//
//            val subject = binding.edtSubject.text.toString()
//            val email = binding.edtEmail.text.toString()
//            val message = binding.edtMessage.text.toString()
//
//            val userName = "dev.rider36@gmail.com"
//            val password = "Devloper@36"
//
//            val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            if (subject.isNotEmpty() && message.isNotEmpty() && (cm.activeNetworkInfo?.isConnectedOrConnecting == true)) {
//                Thread{
//                    try {
//                        val properties = Properties()
//                        properties["mail.smtp.auth"] = "true"
//                        properties["mail.smtp.starttls.enable"] = "true"
//                        properties["mail.smtp.host"] = "smtp.gmail.com"
//                        properties["mail.smtp.port"] = "587"
//
//                        val session = Session.getInstance(properties, object : Authenticator() {
//                            override fun getPasswordAuthentication(): PasswordAuthentication {
//                                return PasswordAuthentication(userName, password)
//                            }
//                        })
//                        val mail = MimeMessage(session)
//                        mail.subject = subject
//                        mail.setText(message)
//                        mail.setFrom(InternetAddress(userName))
//                        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userName))
//                        Transport.send(mail)
//
//                    }
//                    catch (e: Exception) {
//                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }.start()
//                Toast.makeText(this, "Thanks for feedback", Toast.LENGTH_SHORT).show()
//                finish()
//
//            } else {
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
//
//            // Clear EditText fields
//            binding.edtSubject.text?.clear()
//            binding.edtEmail.text?.clear()
//            binding.edtMessage.text?.clear()
//        }
//
//    }
//
//}