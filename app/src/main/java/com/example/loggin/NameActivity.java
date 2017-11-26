package com.example.loggin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameActivity extends Activity {

    private Button btnJoin;
    private EditText txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        btnJoin = (Button) findViewById(R.id.btn_join);
        txtName = (EditText) findViewById(R.id.user_text);

        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().trim().length() > 0) {

                    String user = txtName.getText().toString().trim();

                    Intent intent = new Intent(NameActivity.this,
                            MainActivity.class);
                    intent.putExtra("user", user);

                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
