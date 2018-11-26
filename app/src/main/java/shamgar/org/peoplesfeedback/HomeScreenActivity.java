package shamgar.org.peoplesfeedback;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import shamgar.org.peoplesfeedback.Adapters.TabsAccessorAdaptor;

public class HomeScreenActivity extends AppCompatActivity
{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAccessorAdaptor tabsAccessorAdaptor;

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

        viewPager=(ViewPager)findViewById(R.id.mainviewpager);
        tabsAccessorAdaptor=new TabsAccessorAdaptor(getSupportFragmentManager());




        viewPager.setAdapter(tabsAccessorAdaptor);

        //

        tabLayout=(TabLayout)findViewById(R.id.maintabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
