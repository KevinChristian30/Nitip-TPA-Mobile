package edu.bluejack22_2.nitip.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import edu.bluejack22_2.nitip.Database.Database;
import edu.bluejack22_2.nitip.Facade.ActivityChanger;
import edu.bluejack22_2.nitip.R;
import edu.bluejack22_2.nitip.Service.GoogleService;
import edu.bluejack22_2.nitip.View.HomeActivity;
import edu.bluejack22_2.nitip.View.RegisterActivity;
import edu.bluejack22_2.nitip.ViewModel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginVM;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnLoginWithGoogle;
    private Button btnRegister;
    private Button btnForgotPassword;

    private ActivityResultLauncher<Intent> launcher;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        setListener();
    }

    private void initialize() {
        loginVM = new LoginViewModel();
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        Toast.makeText(this, "Welcome, " + task.getResult().getDisplayName(), Toast.LENGTH_SHORT).show();
                        GoogleSignInAccount acc = task.getResult(ApiException.class);
                        loginVM.firebaseAuthWithGoogle(acc);
                        ActivityChanger.changeActivity(this, HomeActivity.class);
                    } catch (ApiException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
    });


        gso = GoogleService.getGso(this);
        gsc = GoogleService.getGsc(this);
        auth = FirebaseAuth.getInstance();
    }

    private void setListener() {
        btnLogin.setOnClickListener(e -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(this, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            ActivityChanger.changeActivity(this, HomeActivity.class);
                            // Do something with the user object, such as redirect to a new activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                        }
                    });

//            loginVM.loginWithEmailandPassword(email, password);
        });

        btnLoginWithGoogle.setOnClickListener(e -> {
            SignInWithGoogle();
        });

        btnRegister.setOnClickListener(e -> {
            ActivityChanger.changeActivity(this, RegisterActivity.class);
        });

        btnForgotPassword.setOnClickListener(e -> {
            ActivityChanger.changeActivity(this, ForgotPasswordActivity.class);
        });
    }

    private void SignInWithGoogle() {
        Intent intent = gsc.getSignInIntent();
        launcher.launch(intent);
    }


}