package com.edot.driverapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText userID;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userID = findViewById(R.id.userID);
        password = findViewById(R.id.password);
    }

    public void onLogin(View view)
    {
        if(!(userID.getText().toString().isEmpty() || password.getText().toString().isEmpty())) {
            Log.d(AppConstants.LOG_TAG, "@onLogin");
            if ("Driver".equals(userID.getText().toString())) {
                if ("driver".equals(password.getText().toString())) {
                    Toast.makeText(this, R.string.loginSuccess, Toast.LENGTH_SHORT).show();
                    resetAll();
                    openUpdatePage();
                } else {
                    Toast.makeText(this, R.string.invalidLoginCredentials, Toast.LENGTH_SHORT).show();
                    resetPassword();
                }
            } else {
                Toast.makeText(this, R.string.invalidLoginCredentials, Toast.LENGTH_SHORT).show();
                resetPassword();
            }
        }
        else
        {
            Toast.makeText(this,R.string.loginFieldsEmpty,Toast.LENGTH_SHORT).show();
            resetPassword();
        }
    }

    private void resetPassword()
    {
        password.setText("");
    }

    private void resetAll()
    {
        userID.setText("");
        password.setText("");
    }

    private void openUpdatePage()
    {
        Intent intent = new Intent(this,UpdateActivity.class);
        startActivity(intent);
    }


}
