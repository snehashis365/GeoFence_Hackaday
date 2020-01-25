package com.hackaday.geofence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

    Button button;
    TextView reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Toast.makeText(login.this, "Coming Soon", Toast.LENGTH_SHORT).show();

            }
        });

        reset = findViewById(R.id.forgot);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Toast.makeText(login.this, "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
