package jp.bunkaich.sukashimotion;

import android.app.Activity;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

/** Permission-free visual preview uses generated content, never another app's screen. */
public final class PreviewActivity extends Activity {
    private final java.util.concurrent.ExecutorService worker=java.util.concurrent.Executors.newSingleThreadExecutor();
    private FrameLayout canvas;private SnapshotView snapshot;private PreviewRig rig;private boolean physical=true;private TextView degrees,radiusValue,startValue;private boolean inner=true,closed;private int angle=180,generation,blurRadius,blurStart;
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);blurRadius=MotionSettings.blurRadius(this);blurStart=MotionSettings.blurStart(this);if(saved!=null){angle=saved.getInt("angle",180);inner=saved.getBoolean("inner",true);physical=saved.getBoolean("physical",true);}getWindow().setDecorFitsSystemWindows(false);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(16,24,16,32);root.setBackgroundColor(getColor(R.color.theme_background));
        degrees=new TextView(this);degrees.setTextColor(getColor(R.color.theme_text_primary));degrees.setTextSize(18);root.addView(degrees);
        canvas=new FrameLayout(this);root.addView(canvas,new LinearLayout.LayoutParams(-1,0,1));
        SeekBar seek=new SeekBar(this);seek.setMax(180);seek.setProgress(angle);seek.setContentDescription(getString(R.string.hinge_angle));root.addView(seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}public void onProgressChanged(SeekBar s,int value,boolean fromUser){angle=value;update();}});
        radiusValue=compactLabel(root,getString(R.string.blur_radius_value,blurRadius));
        SeekBar radius=new SeekBar(this);radius.setId(R.id.blur_radius);radius.setMax(60);radius.setProgress(blurRadius);radius.setContentDescription(getString(R.string.blur_radius_description));root.addView(radius);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}public void onProgressChanged(SeekBar s,int value,boolean fromUser){blurRadius=value;MotionSettings.blurRadius(PreviewActivity.this,value);updateBlur();}});
        startValue=compactLabel(root,getString(R.string.blur_start_value,blurStart));
        SeekBar start=new SeekBar(this);start.setId(R.id.blur_start);start.setMax(90);start.setProgress(blurStart);start.setContentDescription(getString(R.string.blur_start_description));root.addView(start);
        start.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}public void onProgressChanged(SeekBar s,int value,boolean fromUser){blurStart=value;MotionSettings.blurStart(PreviewActivity.this,value);updateBlur();}});
        Button mode=new Button(this);mode.setText(getString(inner?R.string.switch_cover:R.string.switch_inner));mode.setOnClickListener(v->{inner=!inner;mode.setText(inner?getString(R.string.switch_cover):getString(R.string.switch_inner));build();});root.addView(mode);
        Button view=new Button(this);view.setText(getString(physical?R.string.view_render:R.string.view_physical));view.setOnClickListener(v->{physical=!physical;view.setText(physical?getString(R.string.view_render):getString(R.string.view_physical));if(rig!=null)rig.setPhysical(physical);update();});root.addView(view);
        Button back=new Button(this);back.setText(getString(R.string.back_settings));back.setOnClickListener(v->finish());root.addView(back);setContentView(root);getWindow().getInsetsController().hide(WindowInsets.Type.systemBars());canvas.post(this::build);update();
    }
    private void update(){degrees.setText(getString(R.string.preview_degrees,getString(inner?R.string.inner:R.string.cover),angle,getString(physical?R.string.physical_view:R.string.render_view)));if(rig!=null)rig.setAngle(angle);}
    private TextView compactLabel(LinearLayout parent,String text){TextView label=new TextView(this);label.setText(text);label.setTextColor(getColor(R.color.theme_text_secondary));label.setTextSize(14);parent.addView(label);return label;}
    private void updateBlur(){radiusValue.setText(getString(R.string.blur_radius_value,blurRadius));startValue.setText(getString(R.string.blur_start_value,blurStart));if(snapshot!=null)snapshot.setBlurSettings(blurRadius,blurStart/100f);}
    private void build(){
        int ticket=++generation;boolean mode=inner;
        float aspect=(mode?.9f:.43f)*.72f/.92f;int h=Math.max(200,Math.min(canvas.getHeight(),Math.round(canvas.getWidth()/aspect)));int w=Math.round(h*aspect);
        worker.execute(()->{
            int imageW=w-Math.round(w*.04f)*2,imageH=h-Math.round(h*.14f)*2;
            Bitmap sample=localizedSample(mode?imageW:imageW*2,imageH);
            FrameTexture innerFrame=FrameTexture.prepare(sample,getResources().getDisplayMetrics().density,()->closed||ticket!=generation);
            if(innerFrame==null)return;
            FrameTexture frame=mode?innerFrame:FrameTexture.prepare(Bitmap.createBitmap(sample,imageW,0,imageW,imageH),getResources().getDisplayMetrics().density,()->closed||ticket!=generation);
            runOnUiThread(()->{
                if(closed||ticket!=generation||frame==null)return;canvas.removeAllViews();snapshot=new SnapshotView(this,frame,mode,false);snapshot.setBlurSettings(blurRadius,blurStart/100f);
                if(!mode)snapshot.setRearFrame(innerFrame,false);
                rig=new PreviewRig(this,snapshot,innerFrame);rig.setPhysical(physical);canvas.addView(rig,new FrameLayout.LayoutParams(w,h,Gravity.CENTER));update();
            });
        });
    }

    static Bitmap sample(int w,int h){return sample(w,h,"Preview","");}
    private Bitmap localizedSample(int w,int h){return sample(w,h,getString(R.string.sample_title),getString(R.string.sample_subtitle));}
    private static Bitmap sample(int w,int h,String title,String subtitle){
        Bitmap bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(bitmap);Paint p=new Paint(3);
        p.setShader(new LinearGradient(0,0,w,h,new int[]{0xff173b38,0xff396457,0xff8faaa0},null,Shader.TileMode.CLAMP));c.drawPaint(p);p.setShader(null);
        p.setColor(0xffedfff7);p.setTextSize(w*.07f);if(p.measureText(title)>w*.88f)p.setTextSize(p.getTextSize()*w*.88f/p.measureText(title));c.drawText(title,w*.06f,h*.14f,p);
        p.setTextSize(w*.032f);if(p.measureText(subtitle)>w*.88f)p.setTextSize(p.getTextSize()*w*.88f/p.measureText(subtitle));c.drawText(subtitle,w*.06f,h*.20f,p);
        for(int row=0;row<3;row++)for(int col=0;col<4;col++){
            float x=w*(.06f+col*.235f),y=h*(.3f+row*.19f);p.setColor(new int[]{0xffe6bc8a,0xffbbd6d1,0xffcad9a7,0xffcfbad8}[(row+col)%4]);c.drawRoundRect(x,y,x+w*.18f,y+h*.13f,22,22,p);p.setColor(0xff254138);p.setTextSize(w*.065f);c.drawText(""+(1+row*4+col),x+w*.04f,y+h*.09f,p);
        }return bitmap;
    }
    @Override protected void onSaveInstanceState(Bundle saved){super.onSaveInstanceState(saved);saved.putInt("angle",angle);saved.putBoolean("inner",inner);saved.putBoolean("physical",physical);}
    @Override public void onConfigurationChanged(android.content.res.Configuration config){super.onConfigurationChanged(config);canvas.post(this::build);}
    @Override public void onDestroy(){closed=true;++generation;worker.shutdownNow();super.onDestroy();}
}
