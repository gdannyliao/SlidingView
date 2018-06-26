package com.example.android.customviews.drawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.customviews.R;

public class DrawerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_view);

        DrawerView drawer = findViewById(R.id.drawerView1);
        drawer.addItem("haha", null);
        drawer.addItem("hi", null);

        findViewById(R.id.firstView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerViewActivity.this, "firstView", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.secondView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerViewActivity.this, "secondView", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.topView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrawerViewActivity.this, "topView", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
