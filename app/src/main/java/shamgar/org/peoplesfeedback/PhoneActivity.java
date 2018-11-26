package shamgar.org.peoplesfeedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PhoneActivity";
    private EditText phoneNo;
    private EditText code;

    private RelativeLayout rlgender,rllocation,rlphone;

    private Button sendVerifyPhoneNo,btnnext,btncontinue;
    private Button verifyCode;
    private Button reSend;

    private CheckBox checkmale,checkfemale;
    FirebaseDatabase database = FirebaseDatabase.getInstance();




    ArrayList<String> state = new ArrayList<String>();
    ArrayList<String> districts = new ArrayList<String>();
    ArrayList<String> constituency = new ArrayList<String>();


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    String mVerificationId;

    PhoneAuthProvider.ForceResendingToken mResendToken;

    DatabaseReference areaReference;


    FirebaseAuth mAuth;
    private Spinner spinstate,spindist,spinnerConstituency;
    private ArrayAdapter adapter,adapter1,adapter2;
    private String result;
    private TextView stateTextview,districtTextview;
    private SharedPreferences.Editor editor;
    private String district;
    private TextView constituencyTextview;

    //    @Override
        //    protected void onStart() {
        //        super.onStart();
        //        FirebaseUser currentUser = mAuth.getCurrentUser();
        //        updateUI(currentUser);
        //    }

    private void updateUI(FirebaseUser currentUser)
    {
        if(currentUser != null)
        {
            //Toast.makeText(this,""+currentUser.getEmail()+" : "+currentUser.getDisplayName(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PhoneActivity.this,HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

         spinstate = (Spinner) findViewById(R.id.spinnerstate);
         spindist = (Spinner) findViewById(R.id.spinnerdistrict);
        spinnerConstituency = (Spinner) findViewById(R.id.spinnerConstituency);

        checkmale = (CheckBox) findViewById(R.id.checkmale);
        checkfemale = (CheckBox) findViewById(R.id.checkfemale);

        adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,state);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        adapter1= new ArrayAdapter(this,android.R.layout.simple_spinner_item,districts);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2= new ArrayAdapter(this,android.R.layout.simple_spinner_item,constituency);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




        DatabaseReference states = database.getReference("states");
        states.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot children) {

                        //Get map of users in datasnapshot
                        state.clear();
                        for(DataSnapshot states:children.getChildren())
                        {
                            state.add((String) states.getKey());
                        }
                        spinstate.setAdapter(adapter);



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });



        spinstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                stateTextview = (TextView)adapterView.getSelectedView();
                result = stateTextview.getText().toString();

                DatabaseReference ref = database.getReference("states/"+result);
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot

                                // Iterable<DataSnapshot> children=dataSnapshot.getChildren();


                                districts.clear();

                                //Toast.makeText(getApplicationContext(), (CharSequence) children,Toast.LENGTH_LONG).show();

                                for(DataSnapshot district : dataSnapshot.getChildren())
                                {
                                    districts.add((String) district.getKey());
                                }
                                spindist.setAdapter(adapter1);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                //handle databaseError
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        spindist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                districtTextview = (TextView)adapterView.getSelectedView();

                district = districtTextview.getText().toString();

                DatabaseReference ref = database.getReference("states/"+result+"/"+district);
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot

                                // Iterable<DataSnapshot> children=dataSnapshot.getChildren();


                                constituency.clear();

                                //Toast.makeText(getApplicationContext(), (CharSequence) children,Toast.LENGTH_LONG).show();

                                for(DataSnapshot constituencies : dataSnapshot.getChildren())
                                {
                                    constituency.add((String) constituencies.getKey());
                                }
                                spinnerConstituency.setAdapter(adapter2);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                //handle databaseError
                            }
                        });


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                constituencyTextview = (TextView)adapterView.getSelectedView();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });







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

               String phonenumber= phoneNo.getText().toString();

               editor.putString("phone no",phonenumber);
               editor.commit();



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

    private void sharedPreferencesForsignup() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("signUp", MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();

        if (checkmale.isChecked()==true && checkfemale.isChecked()==false)
        {
            editor.putString("Gender","MALE");
            editor.commit();
        }
        if(checkmale.isChecked()==false && checkfemale.isChecked()==true)
        {
            editor.putString("Gender","FEMALE");
            editor.commit();
        }













    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.v(TAG, "signInWithCredential:success");
                            Toast.makeText(PhoneActivity.this, "Success",Toast.LENGTH_SHORT).show();

                            //                            signOut();

                            FirebaseUser user = task.getResult().getUser();
//                            signInWithPhoneAuthCredential(credential);
//                            Log.v(TAG, "signInWithCredential:success"+user.getPhoneNumber());
                            updateUI(user);
                            // ...
                        }
                        else {
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

                if (checkmale.isChecked()==false && checkfemale.isChecked()==false)
                {
                    Toast.makeText(getApplicationContext(),"please select gender",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    rlgender.setVisibility(View.GONE);
                    rllocation.setVisibility(View.VISIBLE);
                    btnnext.setVisibility(View.GONE);
                    btncontinue.setVisibility(View.VISIBLE);

                    sharedPreferencesForsignup();
                }


                break;

            case R.id.btncontinue:

                rllocation.setVisibility(View.GONE);
                btncontinue.setVisibility(View.GONE);
                rlphone.setVisibility(View.VISIBLE);
                sendVerifyPhoneNo.setVisibility(View.VISIBLE);

                String state= stateTextview.getText().toString();
                String district= districtTextview.getText().toString();
                String constit= constituencyTextview.getText().toString();

                Toast.makeText(getApplicationContext(),"state: "+state+"\n"+" district "+district+"\n"+"constituency: "+constit,Toast.LENGTH_LONG).show();

                editor.putString("state",state);
                editor.putString("district",district);
                editor.putString("constit",constit);
                editor.commit();

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

    @Override
    protected void onStart()
    {
        super.onStart();




    }

    public void checkedButton(View view)
    {

        switch(view.getId()) {

            case R.id.checkmale:

                checkfemale.setChecked(false);


                break;

            case R.id.checkfemale:

                checkmale.setChecked(false);

                break;

        }
    }



}
