package obu.ckt.cricket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import obu.ckt.cricket.comon.DBConstants;
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
    private obu.ckt.cricket.comon.RegularTextView tvSignUp, tvSkip;
    private Button btnLogin;
    private SharePref prefs;
    private DataLayer dLayer;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private DatabaseReference userReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initControls();
    }

    private void initControls() {
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_password_login);
        tvSignUp = (obu.ckt.cricket.comon.RegularTextView) findViewById(R.id.tv_signup_login);
        tvSkip = (obu.ckt.cricket.comon.RegularTextView) findViewById(R.id.tv_skip_login);
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
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }


    public void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        currentUser = mAuth.getCurrentUser();
                        if (task.isSuccessful()) {
                            userReferance = FirebaseDatabase.getInstance().getReference(DBConstants.USER)
                                    .child(currentUser.getUid());

                            userReferance.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    /*List<User> uList = new ArrayList<>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        User track = postSnapshot.getValue(User.class);
                                        uList.add(track);
                                    }*/
                                    User user = dataSnapshot.getValue(User.class);
                                    dLayer.saveUser(prefs, user);
                                    openHomeActivity();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Utils.singleAlertDialog(LoginActivity.this, "Invalid login details");
                                }
                            });

                            //dbLogin();

                        } else {
                            Utils.singleAlertDialog(LoginActivity.this, "Invalid login details");
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    signIn();
                } else {
                    Utils.singleAlertDialog(LoginActivity.this, "Your not connected to internet, please connect to internet and try again");
                }

                break;
            case R.id.tv_signup_login:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case R.id.btn_log_login:
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    login();
                } else {
                    Utils.singleAlertDialog(LoginActivity.this, "Your not connected to internet, please connect to internet and try again");
                }
                break;
            case R.id.tv_skip_login:
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    skipLogin();
                } else {
                    Utils.singleAlertDialog(LoginActivity.this, "Your not connected to internet, please connect to internet and try again");
                }
                break;
        }
    }

    private void skipLogin() {

        String UDID = Utils.udid(LoginActivity.this);
        User user = new User(UDID, "user", "user@gmail.com", "123456");
        db.insertUserWithUDID(user);
        dLayer.saveUser(prefs, user);
        openHomeActivity();
    }

    private void login() {
        if (etEmail.getText().toString().isEmpty())
            Snackbar.make(btnLogin, "Please enter email", Snackbar.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().isEmpty())
            Snackbar.make(btnLogin, "Please enter password", Snackbar.LENGTH_SHORT).show();
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches())
            Snackbar.make(btnLogin, "Please enter valid email", Snackbar.LENGTH_SHORT).show();
        else if (etPassword.getText().toString().length() < 4)
            Snackbar.make(btnLogin, "Password should be more then 4 characters", Snackbar.LENGTH_SHORT).show();
        else signInUser(etEmail.getText().toString(), etPassword.getText().toString());

    }

    private void dbLogin() {
        db.validateLogin(etEmail.getText().toString(),
                etPassword.getText().toString(), new Login() {
                    @Override
                    public void success(User user) {
                        dLayer.saveUser(prefs, user);
                        openHomeActivity();
                    }

                    @Override
                    public void failure() {
                        String name;
                        name = currentUser.getDisplayName() != null ? currentUser.getDisplayName() :
                                Utils.getUsernameFromEmail(etEmail.getText().toString());
                        db.insertUser(name, etEmail.getText().toString(),
                                etPassword.getText().toString());
                        dbLogin();
                    }
                });
    }

    private void openHomeActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
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
            User user = new User(String.valueOf(acct.getId()), acct.getDisplayName(), acct.getEmail(), acct.getEmail());
            db.insertUserWithUDID(user);
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
