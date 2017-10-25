package obu.ckt.cricket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import obu.ckt.cricket.database.DatabaseHandler;

public class SignUpActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnSignUp;
    private DatabaseHandler db;

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
        db = new DatabaseHandler(this);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    private void signUp() {
        if (etName.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
        else if (etEmail.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().length() < 6)
            Toast.makeText(getApplicationContext(), "Password should be more then 6 characters", Toast.LENGTH_SHORT).show();
        else if (db.isEmailExists(etEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
        } else {
            db.insertUser(etName.getText().toString(), etEmail.getText().toString(),
                    etPassword.getText().toString());
            finish();
        }

    }
}
