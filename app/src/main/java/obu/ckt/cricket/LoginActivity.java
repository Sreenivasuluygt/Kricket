package obu.ckt.cricket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.interfaces.Login;
import obu.ckt.cricket.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    DatabaseHandler db;
    private EditText etEmail, etPassword;
    private TextView tvSignUp, tvSkip;
    private Button btnLogin;
    private SharePref prefs;
    private DataLayer dLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControls();
    }

    private void initControls() {
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_password_login);
        tvSignUp = (TextView) findViewById(R.id.tv_signup_login);
        tvSkip = (TextView) findViewById(R.id.tv_skip_login);
        btnLogin = (Button) findViewById(R.id.btn_log_login);


        SpannableString spanStr = new SpannableString("Sign up");
        spanStr.setSpan(new UnderlineSpan(), 0, spanStr.length(), 0);
        tvSignUp.setText(spanStr);
        spanStr = new SpannableString("Skip");
        spanStr.setSpan(new UnderlineSpan(), 0, spanStr.length(), 0);
        tvSkip.setText(spanStr);

        tvSignUp.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        db = new DatabaseHandler(this);
        prefs = SharePref.getInstance(this);
        dLayer = DataLayer.getInstance(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.tv_signup_login:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.btn_log_login:
                dbLogin();
                break;
            case R.id.tv_skip_login:
                skipLogin();
                break;
        }
    }

    private void skipLogin() {
        long userId = 0;
        String UDID = Utils.udid(LoginActivity.this);
        if (!db.isEmailExists(UDID)) {
            userId = db.insertUser(UDID, UDID, UDID);
        } else userId = Long.parseLong(db.getUserId(UDID));
        User user = new User(String.valueOf(userId), UDID, UDID, UDID);
        dLayer.saveUser(prefs, user);
        openHomeActivity();
    }

    private void dbLogin() {
        if (etEmail.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().length() < 6)
            Toast.makeText(getApplicationContext(), "Password should be more then 6 characters", Toast.LENGTH_SHORT).show();
        else
            db.validateLogin(etEmail.getText().toString(),
                    etPassword.getText().toString(), new Login() {
                        @Override
                        public void success(User user) {
                            dLayer.saveUser(prefs, user);
                            openHomeActivity();
                        }

                        @Override
                        public void failure() {
                            Utils.singleAlertDialog(LoginActivity.this, "Invalid login details");
                        }
                    });
    }

    private void openHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Utils.RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Utils.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, acct.getEmail());
            long userId = 0;
            if (!db.isEmailExists(acct.getEmail())) {
                userId = db.insertUser(acct.getDisplayName(), acct.getEmail(), acct.getEmail());
            } else userId = Long.parseLong(db.getUserId(acct.getEmail()));
            User user = new User(String.valueOf(userId), acct.getDisplayName(), acct.getEmail(), acct.getEmail());
            dLayer.saveUser(prefs, user);
            openHomeActivity();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //  updateUI(false);
        }

    }
}
