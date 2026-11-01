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
  TextView logo=new TextView(this);logo.setText("هٰذا\nرَبّي");
  logo.setTextColor(Color.WHITE);logo.setTextSize(67);logo.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
  logo.setGravity(Gravity.CENTER);logo.setLineSpacing(dp(8),1f);
  GradientDrawable arch=new GradientDrawable();arch.setColor(Color.BLACK);
  arch.setCornerRadii(new float[]{dp(160),dp(160),dp(160),dp(160),dp(8),dp(8),dp(8),dp(8)});
  arch.setStroke(dp(6),Color.WHITE);logo.setBackground(arch);
  logo.setPadding(dp(22),dp(75),dp(22),dp(50));
  root.addView(logo,new LinearLayout.LayoutParams(-1,dp(370)));
  TextView red=new TextView(this);red.setText("◆");red.setTextColor(Color.RED);
  red.setTextSize(28);red.setGravity(Gravity.CENTER);root.addView(red);
  TextView loading=new TextView(this);loading.setText("جارٍ فتح تطبيق هذا ربي...");
  loading.setTextColor(Color.WHITE);loading.setTextSize(17);loading.setGravity(Gravity.CENTER);
  root.addView(loading);
  handler.postDelayed(next,1800);
 }
 @Override protected void onDestroy(){handler.removeCallbacks(next);super.onDestroy();}
}