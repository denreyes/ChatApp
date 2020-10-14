package io.djnr.chatapp.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import io.djnr.chatapp.R;
import io.djnr.chatapp.base.BaseActivity;
import io.djnr.chatapp.chat.ChatActivity;

public class SignUpActivity extends BaseActivity {
    private EditText etUsername, etPassword;
    private TextView tvUsernameError, tvPasswordError, tvLogin;
    private Button btnSignUp;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.et_user_name);
        etPassword = findViewById(R.id.et_password);
        tvUsernameError = findViewById(R.id.tv_error_user_name);
        tvPasswordError = findViewById(R.id.tv_error_password);
        tvLogin = findViewById(R.id.tv_login);
        btnSignUp = findViewById(R.id.btn_signup);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // Check if fields are valid, if so then create account.
                if (areFieldsValid(username, password)) {
                    createAccount(username, password);
                }
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Checks if username and password fields are valid. (8-16 characters)
     */
    private boolean areFieldsValid(String username, String password) {
        boolean isUsernameValid = username.length() >= 8 && username.length() <= 16;
        boolean isPasswordValid = password.length() >= 8 && password.length() <= 16;

        tvUsernameError.setVisibility(isUsernameValid ? View.GONE : View.VISIBLE);
        tvPasswordError.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);

        return isUsernameValid && isPasswordValid;
    }

    /**
     * Firebase only accepts an email as a username. So a workaround of appending
     * a fake email domain to the username is required.
     * This calls {@link FirebaseAuth}'s create user with email and password.
     * When user is created, user is signed in automatically
     * and will proceed to {@link ChatActivity}.
     */
    private void createAccount(final String username, String password) {
        String email = username.trim() + "@chatappuser.com";
        showProgress();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                            // Create account success, proceed to Chat with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                            user.updateProfile(profileUpdates);

                            Intent i = new Intent(SignUpActivity.this, ChatActivity.class);
                            i.putExtra("username", username);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            // If create account fails, display error message.
                            tvUsernameError.setVisibility(View.VISIBLE);
                            tvPasswordError.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
