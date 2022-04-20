package com.example.fittr.authentication
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.databinding.ActivityRegisterBinding
import com.example.fittr.util.Variables
import org.json.JSONObject
import java.util.regex.Pattern


class Register : AppCompatActivity() {

    private lateinit var etUserName :EditText
    private lateinit var etEmail : EditText
    private lateinit var etPassword :EditText
    private lateinit var etRePassword : EditText
    private lateinit var btnValidate : Button

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etUserName = findViewById(R.id.nameField)
        etEmail = findViewById(R.id.emailEditText)
        etPassword = findViewById(R.id.passwordEditText)
        etRePassword = findViewById(R.id.repasswordField)
        btnValidate = findViewById(R.id.registerBtn)

        btnValidate.setOnClickListener{
            val username = etUserName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val repassword = etRePassword.text.toString().trim()

            if (username.isEmpty()){
                etUserName.error = "Username is Required"
                return@setOnClickListener
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()){
                etEmail.error = "Email Required"
                return@setOnClickListener
            }else if (password.isEmpty()){
                etPassword.error = "Password Required"
                return@setOnClickListener
            }else if (repassword != password){
                etRePassword.error = "Password does not match"
                return@setOnClickListener
            }else{
                Toast.makeText(this,"Successful", Toast.LENGTH_SHORT).show()
            }
            register(username,email,password)
        }

    }

    fun register(fullName:String,email:String,password:String)
    {
        val queue = Volley.newRequestQueue(applicationContext)
        var url="${Variables.url}/register"
        val builder = Uri.parse(url).buildUpon()
        val params = mapOf<String, Any>(
            "fullName" to fullName,
            "email" to email,
            "password" to password
        )
        val jsonObject = JSONObject(params)

        val request = object : JsonObjectRequest(
            Request.Method.POST, builder.toString(), jsonObject,
            Response.Listener { response ->
                var strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                Variables.setUserId(jsonObj.getInt("userId"))
                Variables.email = email
                Variables.password = password
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()

            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext,"Registeration Failed", Toast.LENGTH_SHORT).show()
            }) {

        }
        queue.add(request)
    }


}