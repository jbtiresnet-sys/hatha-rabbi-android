package org.itsmygod.hatharabbi;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
public class SplashActivity extends Activity {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private final Runnable next=()->{startActivity(new Intent(this,MainActivity.class));finish();};
 private int dp(int v){return (int)(getResources().getDisplayMetrics().density*v);}
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  getWindow().setStatusBarColor(Color.BLACK);getWindow().setNavigationBarColor(Color.BLACK);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.setGravity(Gravity.CENTER);root.setPadding(dp(22),dp(35),dp(22),dp(35));
  root.setBackgroundColor(Color.BLACK);setContentView(root);
  ImageView emblem=new ImageView(this);
  emblem.setImageResource(R.drawable.app_icon);
  emblem.setScaleType(ImageView.ScaleType.FIT_CENTER);
  root.addView(emblem,new LinearLayout.LayoutParams(-1,dp(310)));
  TextView brand=new TextView(this);
  brand.setText("هٰذا رَبّي");brand.setTextSize(38);
  brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
  brand.setTextColor(Color.WHITE);brand.setGravity(Gravity.CENTER);
  root.addView(brand);
  TextView accent=new TextView(this);
  accent.setText("◆");accent.setTextColor(Color.RED);
  accent.setTextSize(24);accent.setGravity(Gravity.CENTER);root.addView(accent);
  TextView loading=new TextView(this);loading.setText("جارٍ فتح تطبيق هذا ربي...");
  loading.setTextColor(Color.WHITE);loading.setTextSize(17);loading.setGravity(Gravity.CENTER);
  root.addView(loading);
  handler.postDelayed(next,1800);
 }
 @Override protected void onDestroy(){handler.removeCallbacks(next);super.onDestroy();}
}