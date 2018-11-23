package shamgar.org.peoplesfeedback;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PhoneActivity";
    private EditText phoneNo;
    private EditText code;

    private RelativeLayout rlgender,rllocation,rlphone;

    private Button sendVerifyPhoneNo,btnnext,btncontinue;
    private Button verifyCode;
    private Button reSend;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    String mVerificationId;

    PhoneAuthProvider.ForceResendingToken mResendToken;


    FirebaseAuth mAuth;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
//            Toast.makeText(this,""+currentUser.getEmail()+" : "+currentUser.getDisplayName(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PhoneActivity.this,HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuplayout);

        mAuth = FirebaseAuth.getInstance();
       // b_verify_phone_no
        phoneNo = findViewById(R.id.e_phone_no_verification);
       // code = findViewById(R.id.e_verify_code);

        sendVerifyPhoneNo = findViewById(R.id.b_verify_phone_no);
        btncontinue = findViewById(R.id.btncontinue);
        btnnext = findViewById(R.id.btnnext);

        rlgender = findViewById(R.id.rlgender);
        rllocation = findViewById(R.id.rllocation);
        rlphone = findViewById(R.id.rlphone);
        //verifyCode = findViewById(R.id.b_verify_code);
       // reSend = findViewById(R.id.b_resend);

        sendVerifyPhoneNo.setOnClickListener(this);
        btncontinue.setOnClickListener(this);
        btnnext.setOnClickListener(this);
      //  verifyCode.setOnClickListener(this);
     //   reSend.setOnClickListener(this);



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.v(TAG, "onCodeSent:" + verificationId);
                Log.v(TAG, "onCodeSent:" + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        }; // new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){}

    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "signInWithCredential:success");
                            Toast.makeText(PhoneActivity.this, "Success",Toast.LENGTH_SHORT).show();

                            //                            signOut();

                            FirebaseUser user = task.getResult().getUser();
//                            signInWithPhoneAuthCredential(credential);
//                            Log.v(TAG, "signInWithCredential:success"+user.getPhoneNumber());
                            updateUI(user);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.v(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PhoneActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.b_verify_phone_no:
                sendVerificationCode(phoneNo.getText().toString());
                break;

            case R.id.btnnext:
                rlgender.setVisibility(View.GONE);
                rllocation.setVisibility(View.VISIBLE);
                btnnext.setVisibility(View.GONE);
                btncontinue.setVisibility(View.VISIBLE);

                break;

            case R.id.btncontinue:

                rllocation.setVisibility(View.GONE);
                btncontinue.setVisibility(View.GONE);
                rlphone.setVisibility(View.VISIBLE);
                sendVerifyPhoneNo.setVisibility(View.VISIBLE);
                break;

        }

    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifySentCode(String s) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, s);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
}
