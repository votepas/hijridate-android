package net.mypapit.mobile.mobimoon;

import java.util.Calendar;




import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnDateChangedListener, OnClickListener {
	
	DatePicker datePicker;
	TextView gregorianText,hijriText,phaseText;;
	private int year,month,day,adjust;
	ImageView resetbtn,moonview;
	private final int[] moonphase = {
			R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8
	
	};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Calendar calender = Calendar.getInstance();
		
		setContentView(R.layout.activity_main);
		year = calender.get(Calendar.YEAR);
		month = calender.get(Calendar.MONTH);
		day = calender.get(Calendar.DAY_OF_MONTH);
		
		
		
		super.onCreate(savedInstanceState);
	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	adjust = Integer.parseInt(pref.getString("dayAdjustment", "0"));
    	Log.d("net.mypapit.mobile",adjust+" recovered");

		
		
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		phaseText = (TextView) findViewById(R.id.textView1);
		gregorianText = (TextView) findViewById(R.id.textView2);
		hijriText = (TextView) findViewById(R.id.textView3);
		resetbtn = (ImageView) findViewById(R.id.imageButton1);
		moonview = (ImageView) findViewById(R.id.imageView1);
		
		
		resetbtn.setClickable(true);
		resetbtn.setOnClickListener(this);
		
	
		
		datePicker.init(year,month,day,this);
		refreshDate(datePicker,year,month,day);
		
    
		
		
		
		
		
	}
	
	public void onResume() {
		super.onResume();
		refreshDate(datePicker,year,month,day);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
    public boolean onOptionsItemSelected(MenuItem item){
    	Intent intent;
    	switch(item.getItemId()) {
    	case R.id.menu_settings:
			intent = new Intent(getBaseContext(),SettingsActivity.class);
			startActivity(intent);
    	
    	break;
    	case R.id.menu_about:
    		try {
    			showDialog();
    		} catch (NameNotFoundException ex){
    			Toast toast = Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
    			toast.show();
    			
    		
    		}
    	break;
    		
    	}
    	return super.onOptionsItemSelected(item);
    	
    }

	@Override
	
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		// TODO Auto-generated method stub
		refreshDate( view,  year,  month,  day);
		
		
		
		
		
		
	}
	
	private int moonPhase(int y, int m, int d)
	{
		
		/*
		  calculates the moon phase (0-7), accurate to 1 segment.
		  0 = > new moon.
		  4 => full moon.
		  */
	
		double c,e;
		double jd;
		int b;
	
		if (m < 3) {
			y--;
			m += 12;
		}
		++m;
		c =  365.25*y;
		e =  30.6*m;
		jd = c+e+d-694039.09;  /* jd is total days elapsed */
		jd /= 29.53;           /* divide by the moon cycle (29.53 days) */
		b = (int) jd;		   /* int(jd) -> b, take integer part of jd */
    	jd -= b;		   /* subtract integer part to leave fractional part of original jd */
    	b = (int) (jd*8 + 0.5);	   /* scale fraction from 0-8 and round by adding 0.5 */
    	b = b & 7;		   /* 0 and 8 are the same so turn 8 into 0 */
    	
		return b;
	}
	
	public void refreshDate(DatePicker view, int year, int month, int day) {
		StringBuffer sbuffer = new StringBuffer();
		HijriConvert hc;
		
		final String[] strMonths = getResources().getStringArray(R.array.month_text);
		
		final String[] strDays = getResources().getStringArray(R.array.day_in_week); 
		
		final String hijriMonth[] = getResources().getStringArray(R.array.hijrimonth_text); 

		final String strPhase[] = getResources().getStringArray(R.array.moon_phase_text); 
				
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(year, month,day);
		
		

		sbuffer.append( strDays[calendar.get(Calendar.DAY_OF_WEEK)-1]+ ", " + day +" " + strMonths[month] + " "+ year);
		
		gregorianText.setText(sbuffer.toString());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	adjust = Integer.parseInt(pref.getString("dayAdjustment", "0"));

		
		
		sbuffer = new StringBuffer();
		
		hc = new HijriConvert(day ,month+1,year);
		
		hc.calibrate(adjust);
		sbuffer.append( ( (hc.getDay() ))  +" " + hijriMonth[hc.getMonth()] + " "+ hc.getYear());
		
		hijriText.setText(sbuffer.toString());
		
		int phase=moonPhase(year,month,day);
		phaseText.setText(strPhase[phase]);
		
		
		
		moonview.setImageDrawable(getResources().getDrawable(moonphase[phase]));
		
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		if (view == resetbtn){
			final Calendar calender = Calendar.getInstance();
			
			
			year = calender.get(Calendar.YEAR);
			month = calender.get(Calendar.MONTH);
			day = calender.get(Calendar.DAY_OF_MONTH);
			
			datePicker.init(year,month,day,this);
			refreshDate(datePicker,year,month,day);
			
			
		}
		
	}
	

    private void showDialog() throws NameNotFoundException {
		// TODO Auto-generated method stub
    	final Dialog dialog = new Dialog(this);
    	dialog.setContentView(R.layout.about_dialog);
    	dialog.setTitle("About HijriDate "+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
    	dialog.setCancelable(true);
    	
    	//text
    	TextView text = (TextView) dialog.findViewById(R.id.tvAbout);
    	text.setText(R.string.txtLicense);
    	
    	//icon image
    	ImageView img = (ImageView) dialog.findViewById(R.id.ivAbout);
    	img.setImageResource(R.drawable.ic_launcher);
    	
    	
    	
    	dialog.show();
    	
    	
    	
    	
		
	}
	
	
	
}


