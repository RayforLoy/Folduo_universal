package jp.bunkaich.sukashimotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.*;

/** Own surface lets capture exclude only our pixels, without ever hiding the visible freeze. */
final class SnapshotSurface extends SurfaceView implements SurfaceHolder.Callback {
    final SnapshotView image;private SurfaceControlViewHost host;private final Runnable committed;
    SnapshotSurface(Context context,SnapshotView image,Runnable committed){
        super(context);this.image=image;this.committed=committed;
        setZOrderOnTop(true);getHolder().setFormat(android.graphics.PixelFormat.TRANSLUCENT);
        getHolder().addCallback(this);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        setOnTouchListener((v,event)->true);
    }
    @Override public void surfaceCreated(SurfaceHolder holder){
        drawFallback(holder,getWidth(),getHeight());
        host=new SurfaceControlViewHost(getContext(),getDisplay(),getHostToken());
        image.logicalWidth=getWidth();host.setView(image,getWidth(),getHeight());
        SurfaceControlViewHost.SurfacePackage surface=host.getSurfacePackage();
        if(surface!=null)setChildSurfacePackage(surface);
        // GPU completion can precede attaching the embedded surface to its parent transaction.
        // Keep the source app visible until both the child and the parent have been submitted.
        image.afterFrame(()->post(()->postOnAnimation(()->postOnAnimation(committed))));
    }
    @Override public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
        drawFallback(holder,width,height);
        if(host!=null){image.logicalWidth=width;host.relayout(width,height);}
    }
    private void drawFallback(SurfaceHolder holder,int width,int height){
        if(width<=0||height<=0)return;Canvas canvas=null;
        try{
            image.logicalWidth=width;image.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));image.layout(0,0,width,height);
            canvas=holder.lockHardwareCanvas();canvas.drawColor(Color.BLACK);image.draw(canvas);
        }catch(Exception ignored){/* The embedded surface still provides the normal frame. */}
        finally{if(canvas!=null)holder.unlockCanvasAndPost(canvas);}
    }
    @Override public void surfaceDestroyed(SurfaceHolder holder){if(host!=null){host.release();host=null;}}
}
