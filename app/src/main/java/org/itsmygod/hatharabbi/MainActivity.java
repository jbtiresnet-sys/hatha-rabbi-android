package org.itsmygod.hatharabbi;
import android.Manifest;
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
 private LinearLayout home, root;
 private ScrollView scroll;
 private static final int VOICE_REQUEST=501;
 private static final String SITE="https://www.itsmygod.org/";
 private static final int GREEN=Color.rgb(18,61,44), GOLD=Color.rgb(205,166,91), DARK=Color.rgb(18,19,18), MAROON=Color.rgb(119,32,48);
 private int dp(int v){return (int)(getResources().getDisplayMetrics().density*v);}
 private GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(17);t.setPadding(dp(12),dp(12),dp(12),dp(12));return t;}
 private void open(String url){scroll.setVisibility(android.view.View.GONE);web.setVisibility(android.view.View.VISIBLE);web.loadUrl(url);}
 private void addTile(String icon,String title,String desc,String url,int color){
  LinearLayout tile=new LinearLayout(this);tile.setOrientation(LinearLayout.VERTICAL);tile.setBackground(bg(color,18));tile.setPadding(dp(12),dp(8),dp(12),dp(8));
  TextView head=text(icon+"  "+title,19,GOLD);head.setTypeface(null,Typeface.BOLD);tile.addView(head);
  TextView sub=text(desc,13,Color.WHITE);tile.addView(sub);
  LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(106));lp.setMargins(dp(16),dp(6),dp(16),dp(6));home.addView(tile,lp);tile.setOnClickListener(v->open(url));
 }
 @Override public void onCreate(Bundle b){super.onCreate(b);
  getWindow().setStatusBarColor(DARK);getWindow().setNavigationBarColor(DARK);
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(DARK);setContentView(root);
  TextView header=text("۞  هذا ربي  ۞",28,GOLD);header.setTypeface(null,Typeface.BOLD);header.setBackgroundColor(GREEN);root.addView(header,new LinearLayout.LayoutParams(-1,dp(70)));
  home=new LinearLayout(this);home.setOrientation(LinearLayout.VERTICAL);home.setBackgroundColor(DARK);
  scroll=new ScrollView(this);scroll.addView(home);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  TextView intro=text("بسم الله الرحمن الرحيم\nمكتبتك الإسلامية في رحاب أهل البيت عليهم السلام",17,Color.WHITE);home.addView(intro);
  addTile("◉","البحث الصوتي","ابحث عن المحاضرات والقصائد والأدعية بصوتك",SITE,GREEN);
  addTile("♫","المكتبة الصوتية","استمع إلى أحدث التسجيلات والمحاضرات",SITE+"category/sawtiyat/",MAROON);
  addTile("▶","المحاضرات الإسلامية","شاهد واستمع إلى المحاضرات",SITE+"category/lectures/",GREEN);
  addTile("✦","القرآن الكريم","استمع إلى تلاوات القرآن الكريم",SITE+"category/quran-2/",MAROON);
  addTile("▤","اللطميات","استمع إلى قصائد الرواديد",SITE+"category/reciters/",GREEN);
  TextView footer=text("itsmygod.org",13,GOLD);home.addView(footer);
  web=new WebView(this);web.setVisibility(android.view.View.GONE);root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setMediaPlaybackRequiresUserGesture(false);
  web.setWebChromeClient(new WebChromeClient(){@Override public void onPermissionRequest(PermissionRequest request){runOnUiThread(()->{boolean audio=false;for(String r:request.getResources())if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r))audio=true;if(audio&&checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});else request.deny();});}});
  web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){Uri u=r.getUrl();String scheme=u.getScheme();if("https".equals(scheme)||"http".equals(scheme))return false;try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception ignored){}return true;}});
  web.setDownloadListener((url,ua,disposition,mime,len)->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(Exception e){Toast.makeText(this,"تعذر فتح التحميل",Toast.LENGTH_SHORT).show();}});
  Button voice=new Button(this);voice.setText("🎙  تحدث: أطفئ، شغّل، ابحث عن...");voice.setTextColor(Color.WHITE);voice.setBackground(bg(MAROON,12));root.addView(voice,new LinearLayout.LayoutParams(-1,dp(58)));voice.setOnClickListener(v->startVoice());
  if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},100);
 }
 private void startVoice(){try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar");i.putExtra(RecognizerIntent.EXTRA_PROMPT,"تحدث الآن");startActivityForResult(i,VOICE_REQUEST);}catch(Exception e){Toast.makeText(this,"التعرف الصوتي غير متاح على الجهاز",Toast.LENGTH_LONG).show();}}
 private void media(String action){
  if(web.getVisibility()!=android.view.View.VISIBLE){Toast.makeText(this,"افتح تسجيلاً أولاً",Toast.LENGTH_SHORT).show();return;}
  String js="(function(){var a='"+action+"';var els=document.querySelectorAll('audio,video');var found=false;for(var i=0;i<els.length;i++){found=true;if(a==='pause')els[i].pause();else els[i].play().catch(function(){});}if(a==='pause'&&window.speechSynthesis)window.speechSynthesis.cancel();if(!found){var buttons=document.querySelectorAll('button,[role=button]');for(var j=0;j<buttons.length;j++){var b=buttons[j];var t=(b.innerText||b.getAttribute('aria-label')||b.title||'').trim();if((a==='pause'&&/إيقاف|ايقاف|توقف|pause|⏸/.test(t))||(a==='play'&&/تشغيل|play|▶/.test(t))){b.click();break;}}}return found;})()";
  web.evaluateJavascript(js,null);
 }

 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request!=VOICE_REQUEST||result!=RESULT_OK||data==null)return;ArrayList<String> results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(results==null||results.isEmpty())return;String cmd=results.get(0).trim();String normalized=cmd.replace("أ","ا").replace("إ","ا").replace("آ","ا").replace("ّ","");if(normalized.matches(".*(اطف|اوقف|وقف|اسكت|توقف|ايقاف).*")){media("pause");return;}if(normalized.matches(".*(شغل|اكمل|استأنف|استكمل).*")){media("play");return;}if(normalized.startsWith("ابحث عن ")||normalized.startsWith("بحث عن ")){String q=normalized.replaceFirst("^(ابحث عن|بحث عن)\\s*","").trim();if(!q.isEmpty())open(SITE+"?s="+Uri.encode(q));return;}Toast.makeText(this,"قل: ابحث عن... أو أطفئ أو شغّل",Toast.LENGTH_LONG).show();}
 @Override public void onBackPressed(){if(web.getVisibility()==android.view.View.VISIBLE){if(web.canGoBack()){web.goBack();return;}web.setVisibility(android.view.View.GONE);scroll.setVisibility(android.view.View.VISIBLE);return;}super.onBackPressed();}
 @Override protected void onDestroy(){web.destroy();super.onDestroy();}
}
