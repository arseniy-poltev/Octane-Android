package com.octane.app.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.octane.app.Preference.SharedPref;
import com.octane.app.R;
import com.octane.app.fragment.HelpFragment;

import org.w3c.dom.Text;

public class HelpActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    TextView btnSkip;
    static final int NUM_ITEMS = 6;
    HelpFragment helpFragment;
    int backButtonCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        backButtonCount = 0;
        btnSkip = findViewById(R.id.btnSkip);

        helpFragment = HelpFragment.newInstance("","");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment,helpFragment);
        transaction.commit();

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveReadHelp();
            }
        });
    }

    void haveReadHelp(){
        SharedPref.getInstance(this).setReadHelpFlag();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("mylog","pos:"+position);
        if(position == NUM_ITEMS - 1){
            btnSkip.setText(R.string.btn_start_text);
        }else{
            btnSkip.setText(R.string.btn_skip_text);
        }

    }

    @Override
    public void onPageSelected(int position) {


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
