package shamgar.org.peoplesfeedback;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("signUp", MODE_PRIVATE);

        String gender=pref.getString("Gender",null);
        String state=pref.getString("state",null);
        String district=pref.getString("district",null);
        String phone=pref.getString("phone no",null);
        String constit=pref.getString("constit",null);

        Toast.makeText(getApplicationContext(),"Gender="+gender+"\n"+"state="+state+"\n"+"district="+district+"\n"+"number="+phone+"\n"+"constituency="+constit,Toast.LENGTH_LONG).show();
    }
}
