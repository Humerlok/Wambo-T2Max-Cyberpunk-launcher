package com.renan.wanboclean;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
    final int LIME=Color.rgb(231,240,0), TEAL=Color.rgb(0,75,73), SAGE=Color.rgb(111,165,99), BG=Color.rgb(3,8,6);
    TextView clock, weather, wifi; Handler handler=new Handler();
    Typeface profont, buttonfont;
    String browser="com.tvwebbrowser.v22";
    String[] managed={"com.globo.globotv","com.google.android.youtube.tv","org.xbmc.kodi","org.videolan.vlc","com.tvwebbrowser.v22","com.goodtoolapps.zeus","com.android.chrome"};
    boolean firstResume=true;
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    class CyberButton extends Button { Paint accent=new Paint(Paint.ANTI_ALIAS_FLAG); CyberButton(Context c){super(c);setWillNotDraw(false);} @Override protected void onDraw(Canvas c){super.onDraw(c);float d=getResources().getDisplayMetrics().density;float n=16*d;float w=getWidth(),h=getHeight();accent.setStyle(Paint.Style.STROKE);accent.setStrokeWidth(1.5f*d);accent.setColor(isFocused()?LIME:SAGE);c.drawLine(8*d,8*d,8*d+n,8*d,accent);c.drawLine(8*d,8*d,8*d,8*d+n,accent);c.drawLine(w-8*d, h-8*d, w-8*d-n,h-8*d,accent);c.drawLine(w-8*d,h-8*d,w-8*d,h-8*d-n,accent);accent.setStyle(Paint.Style.FILL);accent.setColor(LIME);c.drawRect(16*d,14*d,20*d,18*d,accent);accent.setColor(SAGE);c.drawRect(25*d,14*d,29*d,18*d,accent);}}
    GradientDrawable bg(int color,int stroke){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(12));g.setStroke(dp(1),stroke);return g;}
    TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(Color.rgb(245,245,232));t.setTextSize(size);t.setGravity(Gravity.CENTER_VERTICAL);if(profont!=null)t.setTypeface(profont);return t;}
    Button tile(String text,String pkg,String action){Button b=new CyberButton(this);b.setText(text);b.setTextColor(Color.rgb(245,245,232));b.setTextSize(12.5f);b.setAllCaps(false);b.setSingleLine(true);b.setLetterSpacing(.03f);b.setGravity(Gravity.CENTER);if(buttonfont!=null)b.setTypeface(buttonfont);b.setFocusable(true);b.setMinHeight(0);b.setPadding(dp(8),0,dp(8),0);b.setBackground(bg(Color.argb(185,7,15,12),Color.rgb(39,70,62)));b.setOnFocusChangeListener((v,has)->v.setBackground(bg(has?Color.argb(215,0,75,73):Color.argb(185,7,15,12),has?LIME:Color.rgb(39,70,62))));b.setOnClickListener(v->{if(action!=null) openAction(action); else launch(pkg);});return b;}
    void add(GridLayout g,View v,int w){GridLayout.LayoutParams p=new GridLayout.LayoutParams();p.width=0;p.height=dp(145);p.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1,1);p.setMargins(dp(8),dp(6),dp(8),dp(6));g.addView(v,p);}
    void rootCommand(String command){try{java.lang.Process p=Runtime.getRuntime().exec(new String[]{"/system/xbin/su","0"});OutputStream o=p.getOutputStream();o.write((command+"\nexit\n").getBytes("UTF-8"));o.flush();p.waitFor();}catch(Exception ignored){}}
    void stopPackage(ActivityManager am,String p){try{java.lang.reflect.Method m=ActivityManager.class.getDeclaredMethod("forceStopPackage",String.class);m.setAccessible(true);m.invoke(am,p);}catch(Exception ignored){rootCommand("am force-stop "+p);}}
    void closeManaged(String keep){ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);for(String p:managed)if(!p.equals(keep))stopPackage(am,p);}
    void closeAllManaged(){ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);for(String p:managed)stopPackage(am,p);}
    void launch(String p){try{closeManaged(p);Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null)startActivity(i);else Toast.makeText(this,"Aplicativo não disponível",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Não foi possível abrir",Toast.LENGTH_SHORT).show();}}
    void openAction(String a){try{closeAllManaged();if(a.equals("settings"))startActivity(new Intent(Settings.ACTION_SETTINGS));else if(a.equals("hdmi"))startActivity(new Intent().setComponent(new ComponentName("com.mediatek.wwtv.tvcenter","com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity")));else if(a.equals("image"))startActivity(new Intent().setComponent(new ComponentName("com.toptech.zoomsettings","com.toptech.zoomsettings.MainActivity")));else if(a.equals("apps"))showApps();else if(a.equals("clean"))confirmClean();}catch(Exception e){Toast.makeText(this,"Função não disponível",Toast.LENGTH_SHORT).show();}}
    void confirmClean(){new AlertDialog.Builder(this).setTitle("Limpar memória").setMessage("Fechar aplicativos de mídia e limpar caches temporários?").setNegativeButton("Cancelar",null).setPositiveButton("Limpar",(d,w)->{closeAllManaged();rootCommand("pm trim-caches 1G");Toast.makeText(this,"Limpeza concluída",Toast.LENGTH_SHORT).show();}).show();}
    void showApps(){final LinkedHashMap<String,ResolveInfo> map=new LinkedHashMap<>();for(String cat:new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER,Intent.CATEGORY_LAUNCHER}){Intent q=new Intent(Intent.ACTION_MAIN);q.addCategory(cat);for(ResolveInfo r:getPackageManager().queryIntentActivities(q,0)){String key=r.activityInfo.packageName+"/"+r.activityInfo.name;if(!r.activityInfo.packageName.equals(getPackageName()))map.put(key,r);}}final ArrayList<ResolveInfo> apps=new ArrayList<>(map.values());Collections.sort(apps,(x,y)->x.loadLabel(getPackageManager()).toString().compareToIgnoreCase(y.loadLabel(getPackageManager()).toString()));LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setPadding(dp(18),dp(8),dp(18),dp(8));for(ResolveInfo r:apps){Button b=tile(r.loadLabel(getPackageManager()).toString(),null,null);b.setTextSize(16);b.setOnClickListener(v->{try{Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_LAUNCHER);i.setClassName(r.activityInfo.packageName,r.activityInfo.name);startActivity(i);}catch(Exception e){}});list.addView(b,new LinearLayout.LayoutParams(-1,dp(54)));}ScrollView scroll=new ScrollView(this);scroll.setBackgroundColor(BG);scroll.addView(list);new AlertDialog.Builder(this).setTitle("Aplicativos").setView(scroll).setNegativeButton("Fechar",null).show();}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);profont=getResources().getFont(R.font.profont);buttonfont=getResources().getFont(R.font.profont_propo);browser=has("com.tvwebbrowser.v22")?"com.tvwebbrowser.v22":"com.android.chrome";build();updateStatus();handler.postDelayed(new Runnable(){public void run(){updateStatus();handler.postDelayed(this,1800000);}},1800000);}
    @Override protected void onResume(){super.onResume();if(!firstResume)handler.postDelayed(()->closeAllManaged(),300);firstResume=false;}
    boolean has(String p){try{getPackageManager().getPackageInfo(p,0);return true;}catch(Exception e){return false;}}
    void build(){LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(28),dp(18),dp(28),dp(18));BitmapDrawable city=(BitmapDrawable)getResources().getDrawable(R.drawable.cybercity);city.setGravity(Gravity.FILL);root.setBackground(new LayerDrawable(new Drawable[]{city,new ColorDrawable(Color.argb(150,0,0,0))}));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(dp(16),0,dp(16),0);top.setBackground(bg(Color.argb(128,3,35,34),Color.rgb(15,71,65)));
        clock=label("--:--",24);weather=label("☁  --°C",17);wifi=label("Wi‑Fi",16);top.addView(clock,new LinearLayout.LayoutParams(0,dp(52),1));top.addView(weather,new LinearLayout.LayoutParams(dp(170),dp(52)));top.addView(wifi,new LinearLayout.LayoutParams(dp(100),dp(52)));root.addView(top,new LinearLayout.LayoutParams(-1,dp(56)));
        Space sp=new Space(this);root.addView(sp,new LinearLayout.LayoutParams(1,0,1));
        GridLayout grid=new GridLayout(this);grid.setColumnCount(6);grid.setRowCount(1);add(grid,tile("▶  YouTube","com.google.android.youtube.tv",null),1);add(grid,tile("◆  Kodi","org.xbmc.kodi",null),1);add(grid,tile("▲  VLC","org.videolan.vlc",null),1);add(grid,tile("▣  Play Store","com.android.vending",null),1);add(grid,tile("◎  Navegador",browser,null),1);add(grid,tile("▤  Apps",null,"apps"),1);root.addView(grid,new LinearLayout.LayoutParams(-1,dp(170)));
        Space sp2=new Space(this);root.addView(sp2,new LinearLayout.LayoutParams(1,0,1));
        LinearLayout bottom=new LinearLayout(this);bottom.setGravity(Gravity.CENTER);Button s=tile("⚙  Configurações",null,"settings"),h=tile("▱  HDMI",null,"hdmi"),i=tile("⌁  Imagem",null,"image"),c=tile("Limpar",null,"clean");for(Button x:new Button[]{s,h,i}){x.setTextSize(14);bottom.addView(x,new LinearLayout.LayoutParams(0,dp(58),1));}c.setTextSize(11);bottom.addView(c,new LinearLayout.LayoutParams(dp(92),dp(58)));root.addView(bottom,new LinearLayout.LayoutParams(-1,dp(70)));setContentView(root);grid.getChildAt(0).requestFocus();}
    void updateStatus(){clock.setText(new java.text.SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()));ConnectivityManager c=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);wifi.setText(c.getActiveNetworkInfo()!=null?"Wi‑Fi  ●":"Wi‑Fi  ×");new Thread(()->{try{HttpURLConnection c1=(HttpURLConnection)new URL("https://wttr.in/?format=%t").openConnection();c1.setConnectTimeout(2500);c1.setReadTimeout(2500);BufferedReader r=new BufferedReader(new InputStreamReader(c1.getInputStream()));String v=r.readLine();runOnUiThread(()->weather.setText("☁  "+(v==null?"--":v.trim())));}catch(Exception e){runOnUiThread(()->weather.setText("☁  --"));}}).start();}
}
