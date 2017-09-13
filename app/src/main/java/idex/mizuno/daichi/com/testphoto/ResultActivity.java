package idex.mizuno.daichi.com.testphoto;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{

    private int max_number;
    private int total_score=0;

    private TextView total_score_text;

    private BootstrapButton register_buttn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        SharedPreferences sharedPref = this.getSharedPreferences("score", Context.MODE_PRIVATE);

        max_number=sharedPref.getInt("max_number",0);

        for(int i=1;i<max_number;i++){
            Log.d("shared:",String.valueOf(sharedPref.getInt(String.valueOf(i),0)));
            total_score+=sharedPref.getInt(String.valueOf(i),0);
        }

        Log.d("max_number:",String.valueOf(max_number));
        Log.d("total_score:",String.valueOf(total_score));
        total_score=total_score/(max_number);
        total_score=88;

        Log.d("total_score:",String.valueOf(total_score));

        total_score_text=(TextView)findViewById(R.id.score_text);
        total_score_text.setText(String.valueOf(total_score)+"点");


        register_buttn=(BootstrapButton)findViewById(R.id.register);
        register_buttn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==register_buttn){
            dialog();
        }
    }

    public void dialog(){
        // カスタムビューを設定
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.custom_dialog,
                (ViewGroup)findViewById(R.id.layout_root));

        // アラーとダイアログ を生成
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登録");
        builder.setView(layout);

        //点数を表示
        TextView id
                = (TextView)layout.findViewById(R.id.showscore);
        id.setText("スコア:"+String.valueOf(total_score));
        id.setTextSize(18f);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OK ボタンクリック処理
                // ID と PASSWORD を取得
                EditText id
                        = (EditText)layout.findViewById(R.id.userName);
                String strId   = id.getText().toString();

                SQLiteOpenHelper sqliteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
                SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

                //insert
                ContentValues values = new ContentValues();
                values.put("user_name", strId);
                values.put("score", total_score);
                db.insert("ranking_table", null, values);

                Intent intent=new Intent(ResultActivity.this,FirstActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(ResultActivity.this, null).toBundle());
            }
        });
        // 表示
        builder.create().show();
    }
}
