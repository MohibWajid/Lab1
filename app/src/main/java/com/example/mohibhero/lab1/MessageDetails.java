package com.example.mohibhero.lab1;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MessageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        Bundle dataBundle = this.getIntent().getExtras();

        FragmentTransaction ftm = getSupportFragmentManager().beginTransaction();
        MessageFragment mf = new MessageFragment();
        mf.setArguments(dataBundle);

        ftm.replace(R.id.emptyFrameLayout, mf);
        ftm.commit();
    }


}

