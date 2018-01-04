package obu.ckt.cricket;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import obu.ckt.cricket.comon.DBConstants;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.model.User;

public class SignUpActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;
    private DatabaseHandler db;
    private ImageView ivBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initControls();
    }

    private void initControls() {
        etName = (EditText) findViewById(R.id.et_name_sing_up);
        etEmail = (EditText) findViewById(R.id.et_email_sing_up);
        etPassword = (EditText) findViewById(R.id.et_password_sing_up);
        btnSignUp = (Button) findViewById(R.id.btn_sing_up);
        ivBack = (ImageView) findViewById(R.id.iv_back_sign_up);
        db = new DatabaseHandler(this);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    private void signUp() {
        if (etName.getText().toString().isEmpty())
            Snackbar.make(btnSignUp, "Please enter name", Snackbar.LENGTH_SHORT).show();
        else if (etEmail.getText().toString().isEmpty())
            Snackbar.make(btnSignUp, "Please enter email", Snackbar.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().isEmpty())
            Snackbar.make(btnSignUp, "Please enter password", Snackbar.LENGTH_SHORT).show();
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            Snackbar.make(btnSignUp, "Please enter valid email", Snackbar.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().length() < 4)
            Snackbar.make(btnSignUp, "Password should be 4 or more characters", Snackbar.LENGTH_SHORT).show();
        else if (db.isEmailExists(etEmail.getText().toString())) {
            Snackbar.make(btnSignUp, "Email already exists", Snackbar.LENGTH_SHORT).show();
        } else if (!Utils.isNetworkAvailable(SignUpActivity.this)) {
            Utils.singleAlertDialog(SignUpActivity.this, "Your not connected to internet, please connect to internet and try again");
        } else {
            fireBaseSignUp();
        }

    }

    private void fireBaseSignUp() {
        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dbSignUp();
                            insertUserIntoFireDb(task.getResult().getUser().getUid());
                        } else {
                            Snackbar.make(btnSignUp, "Please try again", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dbSignUp() {
        db.insertUser(etName.getText().toString(), etEmail.getText().toString(),
                etPassword.getText().toString());
        finish();
    }

    private void insertUserIntoFireDb(String userId) {
        User user = new User(userId, etName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString());
        FirebaseDatabase.getInstance().getReference().child(DBConstants.USER).child(userId).setValue(user);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
