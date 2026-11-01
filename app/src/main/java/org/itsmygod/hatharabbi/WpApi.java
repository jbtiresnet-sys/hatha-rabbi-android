package org.itsmygod.hatharabbi;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Public WordPress REST API client. No API keys or private credentials. */
public final class WpApi {
 public static final String BASE = "https://www.itsmygod.org/wp-json/wp/v2/";
 private static final ExecutorService IO = Executors.newSingleThreadExecutor();
 private static final Handler MAIN = new Handler(Looper.getMainLooper());
 public interface Callback { void onResult(JSONArray result, String error); }
 public static void getArray(String endpoint, Callback callback) {
  IO.execute(() -> {
   JSONArray result = null; String error = null; HttpURLConnection connection = null;
   try {
    URL url = new URL(BASE + endpoint);
    connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(12000); connection.setReadTimeout(15000);
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty("User-Agent", "HathaRabbiAndroid/0.2");
    int status = connection.getResponseCode();
    if(status != 200) throw new Exception("HTTP " + status);
    try(InputStream in = connection.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
     byte[] buf = new byte[4096]; int n;
     while((n = in.read(buf)) != -1) out.write(buf, 0, n);
     result = new JSONArray(out.toString("UTF-8"));
    }
   } catch(Exception e) { error = e.getMessage(); }
   finally { if(connection != null) connection.disconnect(); }
   final JSONArray data = result; final String failure = error;
   MAIN.post(() -> callback.onResult(data, failure));
  });
 }

 public interface ObjectCallback { void onResult(JSONObject result, String error); }
 public static void getObject(String endpoint, ObjectCallback callback) {
  IO.execute(() -> {
   JSONObject result = null; String error = null; HttpURLConnection connection = null;
   try {
    connection = (HttpURLConnection) new URL(BASE + endpoint).openConnection();
    connection.setConnectTimeout(12000); connection.setReadTimeout(15000);
    connection.setRequestProperty("Accept", "application/json");
    if(connection.getResponseCode()!=200) throw new Exception("HTTP "+connection.getResponseCode());
    try(InputStream in=connection.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
     byte[] buffer=new byte[4096];int n;
     while((n=in.read(buffer))!=-1)out.write(buffer,0,n);
     result=new JSONObject(out.toString("UTF-8"));
    }
   }catch(Exception e){error=e.getMessage();}
   finally{if(connection!=null)connection.disconnect();}
   final JSONObject data=result;final String failure=error;
   MAIN.post(()->callback.onResult(data,failure));
  });
 }
 public static String rendered(JSONObject object, String key) {
  JSONObject field = object.optJSONObject(key);
  return android.text.Html.fromHtml(field == null ? "" : field.optString("rendered", ""), android.text.Html.FROM_HTML_MODE_COMPACT).toString().trim();
 }
 private WpApi() {}
}
