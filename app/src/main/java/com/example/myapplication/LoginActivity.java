package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity{

    private EditText email, password;
    private String userEmail, userPassword;
    private CheckBox chk;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        login();
        register();
        restoreInfo();
    }

    private void register(){
        Button btn_reg = findViewById(R.id.register);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void login(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button btn_login = findViewById(R.id.login);
        chk = findViewById(R.id.chk);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get and check the user input
                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString();
                if(chk.isChecked()){
                    memInfo();
                } else {
                    SharedPreferences.Editor et=getSharedPreferences("data",0).edit();
                    et.clear();
                    et.apply();
                }
                if(checkUserInput())
                    loginEvent();
            }
        });
    }

    public void loginEvent(){
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, jump to the main activity
                            Message("Login Success");
                            startActivity(new Intent(LoginActivity.this, Curriculum.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Message("Please check your Email or Password");
                        }
                    }
                });
    }

    /**
     * @return true when the email and password conforms to the correct format
     */
    public boolean checkUserInput() {
        String strPattern = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+$";
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Message("Please enter your Email or Password");
            return false;
        } else if(!userEmail.matches(strPattern) || userPassword.length() < 6){
            Message("Please check E-mail or password");
            return false;
        }
        return true;
    }

    /**
     * @param s Output the toast message in layout
     */
    public void Message(String s){
        Toast toast = Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    //load the user information of the login page
    private void memInfo(){
        SharedPreferences.Editor editor=getSharedPreferences("data",0).edit();
        editor.putString("email",userEmail);
        editor.putString("password",userPassword);
        editor.apply();
    }

    //restore the user information
    private void restoreInfo(){
        SharedPreferences sp=getSharedPreferences("data",0);
        email.setText(sp.getString("email",""));
        password.setText(sp.getString("password",""));
        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
            chk.setChecked(true);
    }

    /**
     * For Junit testing user information
     * @param strEmail the testing user email
     * @param strPwd the testing user password
     */
    public void setUser(String strEmail, String strPwd){
        userEmail = strEmail;
        userPassword = strPwd;
    }
}
