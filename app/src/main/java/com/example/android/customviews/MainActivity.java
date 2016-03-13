/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.example.android.customviews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.customviews.charting.PieActivity;
import com.example.android.customviews.drawer.DrawerViewActivity;
import com.example.android.customviews.layout.CustomRelativeLayoutActivity;
import com.example.android.customviews.progressbar.ProgressbarActivity;

public class MainActivity extends Activity implements View.OnClickListener {
	/**
	 * Called when the activity is first created.
	 */
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.buttonPie:
				intent = new Intent(MainActivity.this, PieActivity.class);
				break;
			case R.id.buttonDrawerView:
				intent = new Intent(MainActivity.this, DrawerViewActivity.class);
				break;
			case R.id.buttonProgressbar:
				intent = new Intent(MainActivity.this, ProgressbarActivity.class);
				break;
			case R.id.buttonRelative:
				intent = new Intent(MainActivity.this, CustomRelativeLayoutActivity.class);
				break;
		}
		if (intent != null) startActivity(intent);
	}
}

