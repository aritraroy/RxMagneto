package com.aritraroy.rxmagnetodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mFeatureRecycler;
    private Button mGrabResultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFeatureRecycler = (RecyclerView) findViewById(R.id.features_recycler);
        mGrabResultButton = (Button) findViewById(R.id.grab_result);
    }
}