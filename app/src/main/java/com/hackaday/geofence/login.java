package com.hackaday.geofence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    static final int GOOGLE_SIGN_IN = 123;

    Button button;
    TextView reset;
    Button gLogin;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
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

        gLogin = findViewById(R.id.login_google);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                SignInGoogle();
            }
        });







        if(mAuth.getCurrentUser()!=null){

            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(login.this, "Already Logged in with Google", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //progressBar.setVisibility(View.GONE);
                    loginSuccess();

                }
            },1000);


        }


    }

    public void SignInGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);

                    Log.d("TAG", "signInWithCredential:success");

                    FirebaseUser user = mAuth.getCurrentUser();
                    loginSuccess();

                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        dataBaseUpdate(user);
                        Toast.makeText(login.this, "SignUp Success Logging in", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Toast.makeText(login.this, "Login Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);

                    Log.w("TAG", "signInWithCredential:failure", task.getException());

                    Toast.makeText(login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                    Toast.makeText(login.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dataBaseUpdate(FirebaseUser user){

        if (user!=null) {
            FirebaseUserMetadata metadata = user.getMetadata();
                String name = user.getDisplayName();
                String email = user.getEmail();
                String photo = String.valueOf(user.getPhotoUrl());
                String phone = user.getPhoneNumber();

                DatabaseReference googleUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                Map newUser = new HashMap();
                newUser.put("Name", name);
                newUser.put("Email", email);
                newUser.put("Photo_URL", photo);
                newUser.put("Phone", phone);
                googleUser.setValue(newUser);

        }
    }

    private void loginSuccess(){

        progressBar.setVisibility(View.GONE);
        Intent login = new Intent(com.hackaday.geofence.login.this, login_success.class);
        startActivity(login);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
}
