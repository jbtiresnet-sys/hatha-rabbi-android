package org.itsmygod.hatharabbi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Native WordPress article reader, sharing and local bookmarks. */
public class ReaderActivity extends Activity {
 private TextView body;
 private LinearLayout mediaActions;
 private String link, title;
 private int postId;
 private SharedPreferences favorites;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  link=getIntent().getStringExtra("url");
  title=getIntent().getStringExtra("title");
  postId=getIntent().getIntExtra("post_id",0);
  favorites=getSharedPreferences("favorite_posts",MODE_PRIVATE);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.setBackgroundColor(Color.rgb(248,250,251));root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
  TextView heading=new TextView(this);heading.setText(title==null?"هذا ربي":title);
  heading.setTextColor(Color.rgb(35,76,83));heading.setTextSize(24);heading.setPadding(24,32,24,24);root.addView(heading);
  LinearLayout actions=new LinearLayout(this);
  Button save=new Button(this);actions.addView(save,new LinearLayout.LayoutParams(0,-2,1));
  Runnable update=()->save.setText(favorites.contains("post_"+postId)?"إزالة من المحفوظات":"حفظ");
  update.run();
  save.setOnClickListener(v->{
   String key="post_"+postId;
   if(favorites.contains(key)) favorites.edit().remove(key).apply();
   else favorites.edit().putString(key,(title==null?"":title)+"\n"+(link==null?"":link)).apply();
   update.run();
  });
  Button share=new Button(this);share.setText("مشاركة");actions.addView(share,new LinearLayout.LayoutParams(0,-2,1));
  share.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");
   i.putExtra(Intent.EXTRA_TEXT,(title==null?"":title)+"\n"+(link==null?"":link));
   startActivity(Intent.createChooser(i,"مشاركة المادة"));});
  root.addView(actions);
  mediaActions=new LinearLayout(this);mediaActions.setOrientation(LinearLayout.VERTICAL);root.addView(mediaActions);
  ScrollView scroll=new ScrollView(this);body=new TextView(this);
  body.setTextColor(Color.rgb(40,54,61));body.setTextSize(18);body.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
  body.setPadding(26,24,26,48);body.setText("جارٍ تحميل المادة...");
  scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  Button browser=new Button(this);browser.setText("فتح المصدر في المتصفح");root.addView(browser);
  browser.setOnClickListener(v->{if(link!=null && link.startsWith("https://www.itsmygod.org/"))
   startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(link)));});
  setContentView(root);
  if(postId<=0){body.setText("لا تتوفر نسخة أصلية لهذه المادة حاليًا.");return;}
  WpApi.getObject("posts/"+postId+"?_fields=content,title,link", (post,error)->{
   if(error!=null || post==null){body.setText("تعذر تحميل المقال. يمكنك فتح المصدر في المتصفح.");return;}
   JSONObject content=post.optJSONObject("content");
   String html=content==null?"":content.optString("rendered","");
   if(html.isEmpty())body.setText("لا يوجد نص متاح لهذه المادة.");
   else body.setText(Html.fromHtml(html,Html.FROM_HTML_MODE_COMPACT));
   addMediaLinks(html);
  });
 }

 private void addMediaLinks(String html) {
  Pattern pattern=Pattern.compile("(?:https?:)?//[^\\s\"'<>]+?\\.(?:mp3|m4a|ogg|wav|mp4|webm)(?:\\?[^\"'<>\\s]*)?",Pattern.CASE_INSENSITIVE);
  Matcher matcher=pattern.matcher(html.replace("&amp;","&"));
  java.util.HashSet<String> seen=new java.util.HashSet<>();
  while(matcher.find() && seen.size()<12){
   String media=matcher.group();if(media.startsWith("//"))media="https:"+media;
   if(!media.startsWith("https://")||!seen.add(media))continue;
   final String url=media;
   boolean video=url.toLowerCase(java.util.Locale.ROOT).matches(".*\\.(mp4|webm)(\\?.*)?$");
   Button button=new Button(this);button.setText(video?"▶ مشاهدة الفيديو":"♫ استماع للتسجيل");
   mediaActions.addView(button);
   button.setOnClickListener(v->{Intent intent=new Intent(this,MediaActivity.class);
    intent.putExtra("media_url",url);intent.putExtra("title",title);intent.putExtra("video",video);startActivity(intent);});
  }
 }
}
