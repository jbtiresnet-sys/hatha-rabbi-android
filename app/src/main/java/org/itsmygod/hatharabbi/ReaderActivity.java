package org.itsmygod.hatharabbi;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.widget.*;
import android.view.View;
import android.graphics.Color;

/** Content reader: external link fallback until all custom post types expose REST content. */
public class ReaderActivity extends Activity {
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  String url=getIntent().getStringExtra("url"), title=getIntent().getStringExtra("title");
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.setBackgroundColor(Color.WHITE);
  TextView heading=new TextView(this);heading.setText(title==null?"هذا ربي":title);
  heading.setTextSize(22);heading.setPadding(24,24,24,24);root.addView(heading);
  TextView info=new TextView(this);info.setText("فتح المادة من الموقع الأصلي");info.setPadding(24,20,24,20);root.addView(info);
  Button open=new Button(this);open.setText("فتح المحتوى");root.addView(open);
  open.setOnClickListener(v->{if(url!=null && url.startsWith("https://www.itsmygod.org/"))
    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));});
  setContentView(root);
 }
}
