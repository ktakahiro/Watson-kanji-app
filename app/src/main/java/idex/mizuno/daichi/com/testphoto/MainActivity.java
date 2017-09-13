package idex.mizuno.daichi.com.testphoto;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private DrawSurfaceView mCanvasView;
    private BootstrapButton mRedoBtn;
    private BootstrapButton mResetBtn;

    private LinearLayout linearLayout;
    private TextView[] input_text;
    private TextView input_text_back;
    private String[] text={"おかいもの" ,"うんどうかい"};
    private int text_size=0;

    private String[] text_per_character;

    private int input_text_number=0;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permission check
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        Random r = new Random();
        int n = r.nextInt(2);
        text_per_character=text[n].split("");

        byte[] b = new byte[0];
        String value="";
        Log.d("test","test");
        try {
            b = text_per_character[0].getBytes("UTF-8");
            value = new String(b, "Shift_JIS");

            Log.d("value:",value);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("value:",String.valueOf(value));

        Log.d("test","test");

        mCanvasView = (DrawSurfaceView) findViewById(R.id.canvasView);

        mRedoBtn = (BootstrapButton) findViewById(R.id.redoBtn);
        mRedoBtn.setOnClickListener(this);
        mResetBtn = (BootstrapButton) findViewById(R.id.resetBtn);
        mResetBtn.setOnClickListener(this);

        linearLayout=(LinearLayout)findViewById(R.id.input_type);

        input_text_back=(TextView) findViewById(R.id.input_text_back);

        text_size=text.length;

        input_text=new TextView[text_per_character.length-1];
        for(int i=0;i<text_per_character.length-1;i++){
            input_text[i]=new TextView(this);
            input_text[i].setText(text_per_character[i+1]);
        }


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                130,
                1.0f
        );

        input_text[0].setBackground(getDrawable(R.drawable.textview1));
        for(int i=0;i<text_per_character.length-1;i++){
            input_text[i].setLayoutParams(param);
            input_text[i].setTextSize(24f);
            input_text[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(input_text[i]);
        }

        Log.d("max_number:",String.valueOf(text_per_character.length));

    }


    @Override
    public void onClick(View v) {
         if (v == mRedoBtn) {
             Intent intent=new Intent(MainActivity.this,FirstActivity.class);
             startActivity(intent);

        } else if (v == mResetBtn) {
             input_text_number++;
             mCanvasView.reset(input_text_number);


             MyAsyncTask myAsyncTask = new MyAsyncTask(this);
             Integer[] params=new Integer[3];
             params[0]=(int)input_text_number;
             params[1]=text_per_character.length-1;
             params[2]=get_char_code(text_per_character[input_text_number-1]);
             myAsyncTask.execute(params);

             input_text[input_text_number-1].setBackground(getDrawable(R.drawable.textview2));
             if(input_text_number!=text_per_character.length-1){
                 input_text[input_text_number].setBackground(getDrawable(R.drawable.textview1));
             }

             if(input_text_number==text_per_character.length-2){
                 mResetBtn.setText("採点");
                 mResetBtn.setBootstrapBrand(DefaultBootstrapBrand.WARNING);

             }

             if(input_text_number==text_per_character.length-1){
                 showDialog();
             }
        }
    }

    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("採点中");
        progressDialog.setMessage("採点中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismiss_Dialog(){
        progressDialog.dismiss();
    }

    private int get_char_code(String value){
		int moji; 
		switch (value){
			case "あ":
				moji = 33440;
				break;
			case "い":
				moji = 33442;
				break;
			case "う":
				moji = 33444;
				break;
			case "え":
				moji = 33446;
				break;
			case "お":
				moji = 33448;
				break;
			case "か":
				moji = 33449;
				break;
			case "が":
				moji = 33450;
				break;
			case "き":
				moji = 33451;
				break;
			case "ぎ":
				moji = 33452;
				break;
			case "く":
				moji = 33453;
				break;
			case "ぐ":
				moji = 33454;
				break;
			case "け":
				moji = 33455;
				break;
			case "げ":
				moji = 33456;
				break;
			case "こ":
				moji = 33457;
				break;
			case "ご":
				moji = 33458;
				break;
			case "さ":
				moji = 33459;
				break;
			case "ざ":
				moji = 33460;
				break;
			case "し":
				moji = 33461;
				break;
			case "じ":
				moji = 33462;
				break;
			case "す":
				moji = 33463;
				break;
			case "ず":
				moji = 33464;
				break;
			case "せ":
				moji = 33465;
				break;
			case "ぜ":
				moji = 33466;
				break;
			case "そ":
				moji = 33467;
				break;
			case "ぞ":
				moji = 33468;
				break;
			case "た":
				moji = 33469;
				break;
			case "だ":
				moji = 33470;
				break;
			case "ち":
				moji = 33471;
				break;
			case "ぢ":
				moji = 33472;
				break;
			case "つ":
				moji = 33474;
				break;
			case "づ":
				moji = 33475;
				break;
			case "て":
				moji = 33476;
				break;
			case "で":
				moji = 33477;
				break;
			case "と":
				moji = 33478;
				break;
			case "ど":
				moji = 33479;
				break;
			case "な":
				moji = 33480;
				break;
			case "に":
				moji = 33481;
				break;
			case "ぬ":
				moji = 33482;
				break;
			case "ね":
				moji = 33483;
				break;
			case "の":
				moji = 33484;
				break;
			case "は":
				moji = 33485;
				break;
			case "ば":
				moji = 33486;
				break;
			case "ぱ":
				moji = 33487;
				break;
			case "ひ":
				moji = 33488;
				break;
			case "び":
				moji = 33489;
				break;
			case "ぴ":
				moji = 33490;
				break;
			case "ふ":
				moji = 33491;
				break;
			case "ぶ":
				moji = 33492;
				break;
			case "ぷ":
				moji = 33493;
				break;
			case "へ":
				moji = 33494;
				break;
			case "べ":
				moji = 33495;
				break;
			case "ぺ":
				moji = 33496;
				break;
			case "ほ":
				moji = 33497;
				break;
			case "ぼ":
				moji = 33498;
				break;
			case "ぽ":
				moji = 33499;
				break;
			case "ま":
				moji = 33500;
				break;
			case "み":
				moji = 33501;
				break;
			case "む":
				moji = 33502;
				break;
			case "め":
				moji = 33503;
				break;
			case "も":
				moji = 33504;
				break;
			case "や":
				moji = 33506;
				break;
			case "ゆ":
				moji = 33508;

				break;
			case "よ":
				moji = 33510;
				break;
			case "ら":
				moji = 33511;
				break;
			case "り":
				moji = 33512;
				break;
			case "る":
				moji = 33513;
				break;
			case "れ":
				moji = 33514;
				break;
			case "ろ":
				moji = 33515;
				break;
			case "わ":
				moji = 33517;
				break;
			case "ゐ":
				moji = 33518;
				break;
			case "を":
				moji = 33519;
				break;
			case "ゑ":
				moji = 33520;
				break;
			case "ん":
				moji = 33521;
				break;
			default :
				moji = 0;
		}

        return moji;
    }
}
