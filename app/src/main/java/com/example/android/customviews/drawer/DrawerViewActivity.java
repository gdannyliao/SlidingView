package com.example.android.customviews.drawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.android.customviews.R;

public class DrawerViewActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_view);

		DrawerView drawer = findViewById(R.id.drawerView1);
		drawer.addItem("haha", null);
		drawer.addItem("hi", null);
	}
}
