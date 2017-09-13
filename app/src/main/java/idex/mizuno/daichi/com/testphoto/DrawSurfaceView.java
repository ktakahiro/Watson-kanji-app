package idex.mizuno.daichi.com.testphoto;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import static android.R.attr.bitmap;
import static android.R.attr.cacheColorHint;

public class DrawSurfaceView extends SurfaceView implements Callback {

    private SurfaceHolder mHolder;
    private Paint mPaint;
    private Path mPath;
    private Bitmap mLastDrawBitmap;
    private Canvas mLastDrawCanvas;
    private Deque<Path> mUndoStack = new ArrayDeque<Path>();
    private Deque<Path> mRedoStack = new ArrayDeque<Path>();


    public DrawSurfaceView(Context context) {
        super(context);
        init();
    }

    public DrawSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHolder = getHolder();

        // 透過します。
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        // コールバックを設定します。
        mHolder.addCallback(this);

        // ペイントを設定します。
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 描画状態を保持するBitmapを生成します。
        clearLastDrawBitmap(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mLastDrawBitmap.recycle();
    }

    private void clearLastDrawBitmap(int number) {
        if(number!=0){
            if(mLastDrawBitmap!=null){

                File extStrageDir =
                        Environment.getExternalStorageDirectory();
                File file = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/test"+String.valueOf(number)+".png");
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Log.d("Test",mLastDrawBitmap.toString());

                try {
                    boolean result=mLastDrawBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    Log.d("result:",String.valueOf(result));
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch(IllegalStateException e2){

                }

            }
        }

        if (mLastDrawBitmap == null) {
            mLastDrawBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);

        }

        if (mLastDrawCanvas == null) {
            mLastDrawCanvas = new Canvas(mLastDrawBitmap);
        }

        mLastDrawCanvas.drawColor(0, Mode.CLEAR);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(), event.getY());
                break;

            default:
        }
        return true;
    }

    private void onTouchDown(float x, float y) {
        mPath = new Path();
        mPath.moveTo(x, y);
    }

    private void onTouchMove(float x, float y) {
        mPath.lineTo(x, y);
        drawLine(mPath);
    }

    private void onTouchUp(float x, float y) {
        mPath.lineTo(x, y);
        drawLine(mPath);
        mLastDrawCanvas.drawPath(mPath, mPaint);
    }

    private void drawLine(Path path) {
        // ロックしてキャンバスを取得します。
        Canvas canvas = mHolder.lockCanvas();

        // キャンバスをクリアします。
        canvas.drawColor(0, Mode.CLEAR);

        // 前回描画したビットマップをキャンバスに描画します。
        canvas.drawBitmap(mLastDrawBitmap, 0, 0, null);

        // パスを描画します。
        canvas.drawPath(path, mPaint);

        // ロックを外します。
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void undo() {
        if (mUndoStack.isEmpty()) {
            return;
        }

        // undoスタックからパスを取り出し、redoスタックに格納します。
        Path lastUndoPath = mUndoStack.removeLast();
        mRedoStack.addLast(lastUndoPath);

        // ロックしてキャンバスを取得します。
        Canvas canvas = mHolder.lockCanvas();

        // キャンバスをクリアします。
        canvas.drawColor(0, Mode.CLEAR);

        // 描画状態を保持するBitmapをクリアします。
        clearLastDrawBitmap(0);

        // パスを描画します。
        for (Path path : mUndoStack) {
            canvas.drawPath(path, mPaint);
            mLastDrawCanvas.drawPath(path, mPaint);

        }

        // ロックを外します。
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void redo() {
        if (mRedoStack.isEmpty()) {
            return;
        }

        // redoスタックからパスを取り出し、undoスタックに格納します。
        Path lastRedoPath = mRedoStack.removeLast();
        mUndoStack.addLast(lastRedoPath);

        // パスを描画します。
        drawLine(lastRedoPath);

        mLastDrawCanvas.drawPath(lastRedoPath, mPaint);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Download/test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mLastDrawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        Log.d("bitmap:",mLastDrawBitmap.toString());
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset(int number) {
        mUndoStack.clear();
        mRedoStack.clear();

        clearLastDrawBitmap(number);

        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(0, Mode.CLEAR);
        mHolder.unlockCanvasAndPost(canvas);
    }


}