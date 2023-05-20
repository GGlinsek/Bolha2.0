package fri.ris.Activities

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import fri.ris.DatabaseHandler
import fri.ris.R

class LoginActivity : AppCompatActivity () {

    val databaseHandler: DatabaseHandler = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tv_new_account = findViewById<TextView>(R.id.tv_registracija)
        val username = findViewById<EditText>(R.id.et_username)
        val password = findViewById<EditText>(R.id.et_password)
        val loginBtn = findViewById<Button>(R.id.btn_login)



        loginBtn.setOnClickListener {
            val user = databaseHandler.loginUser(username.text.toString(),password.text.toString())
            if (user.id == 1) {
                Toast.makeText(applicationContext,"Prijava uspešna",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext,"Neuspešna prijava",Toast.LENGTH_SHORT).show()
            }
        }

        tv_new_account.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}