package org.itsmygod.hatharabbi;
import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import com.google.firebase.messaging.FirebaseMessaging;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import java.util.ArrayList;
import java.util.Locale;
import android.net.Uri;
import android.webkit.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {
 private WebView web;
 private LinearLayout home, root, nav;
 private ScrollView scroll;
 private static final int VOICE_REQUEST=501;
 private static final String SITE="https://www.itsmygod.org/";
 private static final int GREEN=Color.rgb(55,122,125), GOLD=Color.rgb(161,113,37), DARK=Color.rgb(249,247,240), MAROON=Color.rgb(121,144,183), CARD=Color.rgb(232,242,244);
 private int dp(int v){return (int)(getResources().getDisplayMetrics().density*v);}
 private GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(17);t.setPadding(dp(12),dp(12),dp(12),dp(12));return t;}
 private void open(String url){scroll.setVisibility(android.view.View.GONE);web.setVisibility(android.view.View.VISIBLE);web.loadUrl(url);}
 private void showHome(){web.setVisibility(android.view.View.GONE);scroll.setVisibility(android.view.View.VISIBLE);}
 private void addTile(String icon,String title,String desc,String url,int color){
  LinearLayout tile=new LinearLayout(this);tile.setOrientation(LinearLayout.HORIZONTAL);tile.setGravity(android.view.Gravity.CENTER_VERTICAL);tile.setLayoutDirection(android.view.View.LAYOUT_DIRECTION_RTL);
  tile.setBackground(bg(CARD,22));tile.setPadding(dp(14),dp(10),dp(14),dp(10));tile.setElevation(dp(3));
  TextView symbol=text(icon,25,GOLD);symbol.setBackground(bg(color,16));tile.addView(symbol,new LinearLayout.LayoutParams(dp(62),dp(62)));
  LinearLayout labels=new LinearLayout(this);labels.setOrientation(LinearLayout.VERTICAL);
  TextView head=text(title,18,Color.rgb(33,65,70));head.setGravity(android.view.Gravity.RIGHT);head.setTypeface(null,Typeface.BOLD);labels.addView(head);
  TextView sub=text(desc,12,Color.rgb(80,104,107));sub.setGravity(android.view.Gravity.RIGHT);labels.addView(sub);
  LinearLayout.LayoutParams labelParams=new LinearLayout.LayoutParams(0,-2,1);labelParams.setMargins(dp(8),0,dp(8),0);tile.addView(labels,labelParams);
  TextView arrow=text("‹",27,GOLD);tile.addView(arrow);
  LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(106));lp.setMargins(dp(16),dp(6),dp(16),dp(6));home.addView(tile,lp);tile.setOnClickListener(v->{Uri u=Uri.parse(url);String slug=u.getLastPathSegment();openCategory(slug,title);});
 }
 private void openCategory(String slug,String title){Intent intent=new Intent(this,LibraryActivity.class);intent.putExtra("slug",slug);intent.putExtra("title",title);startActivity(intent);}
 private void addNav(LinearLayout parent,String label,String symbol,Runnable action){
  TextView item=text(symbol+"\\n"+label,12,GOLD);item.setOnClickListener(v->action.run());parent.addView(item,new LinearLayout.LayoutParams(0,dp(65),1));
 }
 @Override public void onCreate(Bundle b){super.onCreate(b);
  getWindow().setStatusBarColor(DARK);getWindow().setNavigationBarColor(DARK);
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(DARK);setContentView(root);
  TextView header=text("۞   هذا ربي   ۞",26,GOLD);header.setTypeface(null,Typeface.BOLD);header.setBackgroundColor(DARK);root.addView(header,new LinearLayout.LayoutParams(-1,dp(66)));
  home=new LinearLayout(this);home.setOrientation(LinearLayout.VERTICAL);home.setBackgroundColor(DARK);
  scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(home);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);hero.setBackground(bg(GREEN,26));hero.setPadding(dp(14),dp(20),dp(14),dp(20));
  TextView bismillah=text("﷽",31,GOLD);hero.addView(bismillah);
  TextView intro=text("في رحاب أهل البيت عليهم السلام",20,Color.WHITE);intro.setTypeface(null,Typeface.BOLD);hero.addView(intro);
  TextView subtitle=text("مكتبة إسلامية تجمع المعرفة والصوت والصورة",13,Color.rgb(222,230,220));hero.addView(subtitle);
  LinearLayout.LayoutParams heroLp=new LinearLayout.LayoutParams(-1,dp(190));heroLp.setMargins(dp(16),dp(8),dp(16),dp(16));home.addView(hero,heroLp);
  TextView nativeLink=text("تصفح جميع التصنيفات وأحدث المقالات  ◀",19,GREEN);nativeLink.setOnClickListener(v->startActivity(new Intent(this,LibraryActivity.class)));home.addView(nativeLink);
  TextView section=text("اكتشف المكتبة",21,GOLD);section.setGravity(android.view.Gravity.RIGHT);section.setTypeface(null,Typeface.BOLD);home.addView(section);
  addTile("♫","المكتبة الصوتية","تسجيلات ومحاضرات وقصائد",SITE+"category/sawtiyat/",GREEN);
  addTile("▶","المحاضرات الإسلامية","مجالس ومحاضرات مرئية",SITE+"category/lectures/",MAROON);
  addTile("✦","القرآن الكريم","تلاوات قرآنية مباركة",SITE+"category/quran-2/",GREEN);
  addTile("❖","اللطميات","قصائد ومجالس عزاء",SITE+"category/reciters/",MAROON);
  TextView tokenButton=text("اختبار الإشعارات  •  نسخ رمز FCM",17,GREEN);
  tokenButton.setOnClickListener(v -> FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
    if (!task.isSuccessful() || task.getResult() == null) { Toast.makeText(this,"تعذر الحصول على الرمز. تأكد من الإنترنت وخدمات Google",Toast.LENGTH_LONG).show(); return; }
    String token=task.getResult();
    ClipboardManager clipboard=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    clipboard.setPrimaryClip(ClipData.newPlainText("FCM token",token));
    new android.app.AlertDialog.Builder(this).setTitle("رمز اختبار الإشعارات").setMessage("تم نسخ الرمز. الصقه في خانة Test on device داخل Firebase.").setPositiveButton("حسناً",null).show();
  }));
  home.addView(tokenButton);
  TextView footer=text("هذا ربي  •  itsmygod.org",12,GOLD);home.addView(footer);
  web=new WebView(this);web.setVisibility(android.view.View.GONE);root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setMediaPlaybackRequiresUserGesture(false);
  web.setWebChromeClient(new WebChromeClient(){@Override public void onPermissionRequest(PermissionRequest request){runOnUiThread(()->{boolean audio=false;for(String r:request.getResources())if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r))audio=true;if(audio&&checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});else request.deny();});}});
  web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){Uri u=r.getUrl();String scheme=u.getScheme();if("https".equals(scheme)||"http".equals(scheme))return false;try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception ignored){}return true;}});
  web.setDownloadListener((url,ua,disposition,mime,len)->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(Exception e){Toast.makeText(this,"تعذر فتح التحميل",Toast.LENGTH_SHORT).show();}});
  nav=new LinearLayout(this);nav.setGravity(android.view.Gravity.CENTER);nav.setBackgroundColor(CARD);nav.setLayoutDirection(android.view.View.LAYOUT_DIRECTION_RTL);
  addNav(nav,"الرئيسية","⌂",()->showHome());
  addNav(nav,"المكتبة","▦",()->startActivity(new Intent(this,LibraryActivity.class)));
  addNav(nav,"بحث صوتي","🎙",()->startVoice());
  addNav(nav,"إيقاف","Ⅱ",()->media("pause"));
  root.addView(nav,new LinearLayout.LayoutParams(-1,dp(70)));
  if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 102);
  FirebaseMessaging.getInstance().subscribeToTopic("all_users");
 }
 private void startVoice(){try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar");i.putExtra(RecognizerIntent.EXTRA_PROMPT,"تحدث الآن");startActivityForResult(i,VOICE_REQUEST);}catch(Exception e){Toast.makeText(this,"التعرف الصوتي غير متاح على الجهاز",Toast.LENGTH_LONG).show();}}
 private void media(String action){
  if(web.getVisibility()!=android.view.View.VISIBLE){Toast.makeText(this,"افتح تسجيلاً أولاً",Toast.LENGTH_SHORT).show();return;}
  String js="(function(){var a='"+action+"';var els=document.querySelectorAll('audio,video');var found=false;for(var i=0;i<els.length;i++){found=true;if(a==='pause')els[i].pause();else els[i].play().catch(function(){});}if(a==='pause'&&window.speechSynthesis)window.speechSynthesis.cancel();if(!found){var buttons=document.querySelectorAll('button,[role=button]');for(var j=0;j<buttons.length;j++){var b=buttons[j];var t=(b.innerText||b.getAttribute('aria-label')||b.title||'').trim();if((a==='pause'&&/إيقاف|ايقاف|توقف|pause|⏸/.test(t))||(a==='play'&&/تشغيل|play|▶/.test(t))){b.click();break;}}}return found;})()";
  web.evaluateJavascript(js,null);
 }

 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request!=VOICE_REQUEST||result!=RESULT_OK||data==null)return;ArrayList<String> results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(results==null||results.isEmpty())return;String cmd=results.get(0).trim();String normalized=cmd.replace("أ","ا").replace("إ","ا").replace("آ","ا").replace("ّ","");if(normalized.matches(".*(اطف|اوقف|وقف|اسكت|توقف|ايقاف).*")){media("pause");return;}if(normalized.matches(".*(شغل|اكمل|استأنف|استكمل).*")){media("play");return;}if(normalized.matches(".*(الرئيسيه|الرئيسية|الصفحه الرئيسيه).*")){showHome();return;}if(normalized.contains("قران")||normalized.contains("القران")){openCategory("quran-2","القرآن الكريم");return;}if(normalized.contains("محاضر")){openCategory("lectures","المحاضرات الإسلامية");return;}if(normalized.contains("صوتي")||normalized.contains("تسجيل")){openCategory("sawtiyat","المكتبة الصوتية");return;}if(normalized.contains("لطمي")){openCategory("reciters","اللطميات");return;}if(normalized.startsWith("ابحث عن ")||normalized.startsWith("بحث عن ")){String q=normalized.replaceFirst("^(ابحث عن|بحث عن)\\s*","").trim();if(!q.isEmpty()){Intent search=new Intent(this,LibraryActivity.class);search.putExtra("search",q);startActivity(search);}return;}Toast.makeText(this,"قل: ابحث عن... أو أطفئ أو شغّل",Toast.LENGTH_LONG).show();}
 @Override public void onBackPressed(){if(web.getVisibility()==android.view.View.VISIBLE){if(web.canGoBack()){web.goBack();return;}showHome();return;}super.onBackPressed();}
 @Override protected void onDestroy(){web.destroy();super.onDestroy();}
}
