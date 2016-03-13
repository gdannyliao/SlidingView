package com.example.android.customviews.drawer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.android.customviews.R;
import com.example.android.customviews.drawer.DrawerView;

public class DrawerViewActivity extends AppCompatActivity {

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer_view);

		DrawerView drawer = (DrawerView) findViewById(R.id.drawerView1);
		drawer.addItem("haha", null);
		drawer.addItem("hi", null);
	}
}
