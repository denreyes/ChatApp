package io.djnr.chatapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.djnr.chatapp.R;
import io.djnr.chatapp.base.BaseActivity;
import io.djnr.chatapp.chat.ChatActivity;

public class IndexActivity extends BaseActivity {
    private Button btnLogin, btnSignUp;
    private RelativeLayout rlMain;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_signup);
        rlMain = findViewById(R.id.rl_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IndexActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin.setEnabled(true);
        btnSignUp.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in, if so transfer to ChatActivity.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(IndexActivity.this, ChatActivity.class);
            i.putExtra("username", currentUser.getDisplayName());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            rlMain.setVisibility(View.VISIBLE);
        }
    }
}
