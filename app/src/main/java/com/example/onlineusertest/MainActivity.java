package com.example.onlineusertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onlineUser(View view) {
        openOnlineUserList();
    }

    private void openOnlineUserList() {
        if(checkSigInStatus()){
            startActivity(new Intent(this,OnlineUserActivity.class));
        }
        else{
            authenticate();
        }
    }

    FirebaseUser authUser;
    private static final int RC_SIGN_IN = 1;
    private boolean checkSigInStatus() {
        authUser = FirebaseAuth.getInstance().getCurrentUser();
        return authUser != null;
    }

    public void authenticate() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.FirebaseTheme)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser()!=null ) {
                openOnlineUserList();
            } else {
                Toast.makeText(this,"Sign in Failed. Please Sign in To Continue",Toast.LENGTH_SHORT).show();
            }
        }
    }

}