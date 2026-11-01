package org.itsmygod.hatharabbi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

/** Native category and post browsing with WordPress REST pagination. */
public class LibraryActivity extends Activity {
 private LinearLayout list;
 private int category = 0, page = 1;
 private String title = "المكتبة";
 private boolean loading = false;
 private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }
 private TextView label(String value, int size) {
  TextView t = new TextView(this); t.setText(value); t.setTextSize(size);
  t.setTextColor(Color.rgb(31,67,75)); t.setPadding(dp(14),dp(14),dp(14),dp(14));
  t.setGravity(Gravity.RIGHT); return t;
 }
 private void row(String value, Runnable action) {
  TextView t = label(value, 17);
  GradientDrawable bg = new GradientDrawable(); bg.setColor(Color.WHITE); bg.setCornerRadius(dp(12));
  t.setBackground(bg);
  LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
  p.setMargins(dp(12),dp(6),dp(12),dp(6)); list.addView(t,p);
  t.setOnClickListener(v -> action.run());
 }
 @Override public void onCreate(Bundle b) {
  super.onCreate(b); category = getIntent().getIntExtra("category",0);
  title = getIntent().getStringExtra("title"); if(title == null) title = "المكتبة";
  LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
  root.setBackgroundColor(Color.rgb(247,250,251)); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
  TextView heading = label(title,24); heading.setTypeface(null,Typeface.BOLD); root.addView(heading);
  ScrollView scroller = new ScrollView(this); list = new LinearLayout(this);
  list.setOrientation(LinearLayout.VERTICAL); scroller.addView(list);
  root.addView(scroller,new LinearLayout.LayoutParams(-1,0,1));
  Button next = new Button(this); next.setText("عرض المزيد"); root.addView(next);
  next.setOnClickListener(v -> loadPosts());
  setContentView(root);
  if(category == 0) loadCategories(); else { loadChildren(); loadPosts(); }
 }
 private void loadCategories() {
  row("التصنيفات",()->{});
  WpApi.getArray("categories?per_page=100&hide_empty=false", (data,error) -> {
   if(error != null) { row("تعذر تحميل التصنيفات: "+error, this::loadCategories); return; }
   if(data == null) return;
   for(int i=0;i<data.length();i++) {
    JSONObject item = data.optJSONObject(i); if(item == null) continue;
    int id = item.optInt("id"); String name = item.optString("name");
    row("▦  "+name, () -> {
     Intent intent = new Intent(this,LibraryActivity.class);
     intent.putExtra("category",id); intent.putExtra("title",name); startActivity(intent);
    });
   }
  });
  loadPosts();
 }
 private void loadChildren() {
  WpApi.getArray("categories?parent="+category+"&per_page=100&hide_empty=false",(data,error)->{
   if(data == null) return;
   for(int i=0;i<data.length();i++) {
    JSONObject item=data.optJSONObject(i); if(item==null)continue;
    int id=item.optInt("id");String name=item.optString("name");
    row("◈  "+name,()->{Intent intent=new Intent(this,LibraryActivity.class);
     intent.putExtra("category",id);intent.putExtra("title",name);startActivity(intent);});
   }
  });
 }
 private void loadPosts() {
  if(loading) return; loading = true;
  String endpoint="posts?per_page=20&page="+page+"&_fields=id,title,link,excerpt";
  if(category != 0) endpoint += "&categories="+category;
  WpApi.getArray(endpoint,(data,error)->{
   loading=false;
   if(error != null) {
    if(error.contains("HTTP 400") && page>1) { Toast.makeText(this,"لا توجد صفحات أخرى",Toast.LENGTH_SHORT).show(); return; }
    row("تعذر تحميل المحتوى، اضغط للمحاولة",this::loadPosts); return;
   }
   if(data==null || data.length()==0) { if(page==1)row("لا توجد مقالات في هذا التصنيف",()->{});return; }
   for(int i=0;i<data.length();i++) {
    JSONObject post=data.optJSONObject(i);if(post==null)continue;
    String name=WpApi.rendered(post,"title"), link=post.optString("link");
    row("📖  "+name,()->{Intent intent=new Intent(this,ReaderActivity.class);
     intent.putExtra("url",link);intent.putExtra("title",name);startActivity(intent);});
   }
   page++;
  });
 }
}
