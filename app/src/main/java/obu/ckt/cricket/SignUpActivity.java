package obu.ckt.cricket;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import obu.ckt.cricket.database.DatabaseHandler;

public class SignUpActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;
    private DatabaseHandler db;
    private ImageView ivBack;

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
        } else {
            db.insertUser(etName.getText().toString(), etEmail.getText().toString(),
                    etPassword.getText().toString());
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
