package org.itsmygod.hatharabbi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.*;
import android.graphics.Color;

/** Playback for direct, public HTTP(S) audio/video files. */
public class MediaActivity extends Activity {
 private MediaPlayer player;
 private VideoView video;
 private String url;
 private boolean videoMode;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  url=getIntent().getStringExtra("media_url");
  String title=getIntent().getStringExtra("title");
  videoMode=getIntent().getBooleanExtra("video",false);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.setBackgroundColor(Color.rgb(247,250,251));
  TextView heading=new TextView(this);heading.setText(title==null?"المشغل":title);
  heading.setTextSize(22);heading.setPadding(24,24,24,24);root.addView(heading);
  if(url==null || !url.startsWith("https://")) {
   TextView error=new TextView(this);error.setText("رابط الوسائط غير متاح أو غير آمن");root.addView(error);
   setContentView(root);return;
  }
  if(videoMode) {
   video=new VideoView(this);root.addView(video,new LinearLayout.LayoutParams(-1,0,1));
   video.setVideoURI(Uri.parse(url));
   MediaController controls=new MediaController(this);video.setMediaController(controls);
   video.setOnErrorListener((mp,what,extra)->{Toast.makeText(this,"تعذر تشغيل الفيديو",Toast.LENGTH_LONG).show();return true;});
   video.start();
  } else {
   TextView status=new TextView(this);status.setText("جارٍ تجهيز الصوت...");
   status.setPadding(24,24,24,24);root.addView(status);
   Button play=new Button(this);play.setText("تشغيل");root.addView(play);
   Button pause=new Button(this);pause.setText("إيقاف مؤقت");root.addView(pause);
   play.setEnabled(false);
   player=new MediaPlayer();
   try {
    player.setDataSource(this,Uri.parse(url));
    player.setOnPreparedListener(mp->{status.setText("جاهز للاستماع");play.setEnabled(true);mp.start();});
    player.setOnErrorListener((mp,what,extra)->{status.setText("تعذر تشغيل الملف الصوتي");return true;});
    player.prepareAsync();
    play.setOnClickListener(v->{if(player!=null)player.start();});
    pause.setOnClickListener(v->{if(player!=null && player.isPlaying())player.pause();});
   }catch(Exception e){status.setText("تعذر فتح الملف الصوتي");}
  }
  Button external=new Button(this);external.setText("فتح الملف خارجيًا");root.addView(external);
  external.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url))));
  setContentView(root);
 }
 @Override protected void onDestroy(){if(video!=null)video.stopPlayback();if(player!=null){player.release();player=null;}super.onDestroy();}
}
