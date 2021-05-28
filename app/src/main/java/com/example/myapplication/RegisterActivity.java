package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText id, email, password, rePassword;
    private String userId, userEmail, userPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        signup();
        login();
    }

    private void login(){
        Button btn_login = findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void signup(){
        id = findViewById(R.id.ID);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);

        Button btn_reg = findViewById(R.id.btn);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the user input
                userId = id.getText().toString().trim();
                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString();
                //check the user information with in correct format before signup
                if(checkUserInput())
                    RegistEvent();
            }
        });
    }

    public void RegistEvent(){
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update user profile with the signed-in user's information
                            //and save user Email and password Automatically
                            Message("Authentication Success.");
                            updateUserProfile();
                            memInfo();

                            //Jump to new activity after one sec
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(RegisterActivity.this, Curriculum.class));
                                }
                            }, 1000);
                        } else {
                            // If sign in fails, display a message to the user.
                            Message("Authentication failed.");
                        }
                    }
                });
    }


    /**
     * @return true when the email and password conforms to the correct format
     */
    public boolean checkUserInput() {
        String strPattern = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+$";
        if(userId.isEmpty()){
            Message("Please enter your name");
            return false;
        } else if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Message("Please enter your Email or Password");
            return false;
        } else if(userPassword.length() < 6) {
            Message("Please enter your password length at least 6");
            return false;
        } else if(!rePassword.getText().toString().equals(userPassword)){
            Message("Please check your Password");
            return false;
        } else if(!userEmail.matches(strPattern)){
            Message("Please enter correct E-mail address");
            return false;
        }
        return true;
    }

    private void updateUserProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        //load the saveUserName method
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userId)
                .build();
        //uodate the user profile
        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Sign_up", "userIdUpdate: success.");
                        } else {
                            Log.w("Sign_up", "userIdUpdate: failed.");
                        }
                    }
                });
    }

    /**
     * @param s Output the toast message in layout
     */
    public void Message(String s){
        Toast toast = Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    //save the user email and password for the future login
    private void memInfo(){
        SharedPreferences.Editor editor=getSharedPreferences("data",0).edit();
        editor.putString("email",userEmail);
        editor.putString("password",userPassword);
        editor.apply();
    }

    /**
     * For Junit testing user information
     * @param strEmail the testing user email
     * @param strPwd the testing user password
     */
    public void setUser(String strid, String strEmail, String strPwd, String strRePwd){
        userId = strid;
        userEmail = strEmail;
        rePassword.setText(strRePwd);
        userPassword = strPwd;
    }
}
