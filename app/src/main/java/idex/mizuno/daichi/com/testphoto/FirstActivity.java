package idex.mizuno.daichi.com.testphoto;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.beardedhen.androidbootstrap.BootstrapAlert;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    private Button hiragana_button;
    private Button ranking_button;

    private ArrayList<String> user_name_list=new ArrayList<>();
    private ArrayList<Integer> score_list=new ArrayList<>();
    private int[]ranking_index=new int[5];
    private String[] result_user_name=new String[5];
    private int[] result_score=new int[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        SQLiteOpenHelper sqliteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();


        for(int i=0;i<ranking_index.length;i++){
            ranking_index[i]=-1;
        }


        hiragana_button=(Button)findViewById(R.id.hiragana_button);
        hiragana_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(FirstActivity.this, null).toBundle());
            }
        });

        ranking_button=(Button)findViewById(R.id.ranking_button);
        ranking_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstActivity.this,RankingActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(FirstActivity.this, null).toBundle());
            }
        });
    }
}
