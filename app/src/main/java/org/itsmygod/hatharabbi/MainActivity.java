package org.itsmygod.hatharabbi;
import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.*;
import android.widget.*;
import android.graphics.Color;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {
 private WebView web;
 private static final String HOME="https://www.itsmygod.org/";
 @Override public void onCreate(Bundle b){ super.onCreate(b);
  getWindow().setStatusBarColor(Color.rgb(18,61,44));
  LinearLayout layout=new LinearLayout(this);layout.setOrientation(LinearLayout.VERTICAL);layout.setBackgroundColor(Color.rgb(18,61,44));
  TextView title=new TextView(this);title.setText("هذا ربي");title.setTextColor(Color.rgb(212,175,90));title.setTextSize(24);title.setGravity(17);title.setPadding(12,16,12,16);layout.addView(title);
  web=new WebView(this);layout.addView(web,new LinearLayout.LayoutParams(-1,0,1));setContentView(layout);
  WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setMediaPlaybackRequiresUserGesture(false);
  web.setWebChromeClient(new WebChromeClient(){ @Override public void onPermissionRequest(PermissionRequest request){ runOnUiThread(()->{ boolean audio=false;for(String r:request.getResources()) if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(r))audio=true; if(audio && checkSelfPermission(Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE}); else request.deny();}); }});
  web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){Uri u=r.getUrl();String scheme=u.getScheme();if("https".equals(scheme)||"http".equals(scheme))return false;try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(Exception ignored){}return true;}});
  web.setDownloadListener((url,userAgent,contentDisposition,mimeType,contentLength)->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(Exception e){Toast.makeText(this,"تعذر فتح التحميل",Toast.LENGTH_SHORT).show();}});
  if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},100);
  web.loadUrl(HOME);
 }
 @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}
