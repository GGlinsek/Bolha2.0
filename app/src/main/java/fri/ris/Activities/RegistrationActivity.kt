package fri.ris.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import fri.ris.DatabaseHandler
import fri.ris.ModelClasses.UserModelClass
import fri.ris.R

class RegistrationActivity : AppCompatActivity () {

    val DatabaseHandler: DatabaseHandler = DatabaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val btnRegister= findViewById<Button>(R.id.btn_register)
        val username = findViewById<EditText>(R.id.et_register_username)
        val email = findViewById<EditText>(R.id.et_register_mail)
        val password = findViewById<EditText>(R.id.et_register_password)

        btnRegister.setOnClickListener {
            val check = DatabaseHandler.registerUser(UserModelClass(0, email.text.toString(),username.text.toString(),password.text.toString(),false,""))
            print(check)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}