package idex.mizuno.daichi.com.testphoto;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    private TextView[] user_name_text=new TextView[5];
    private TextView[] score_text=new TextView[5];

    private ArrayList<String> user_name_list=new ArrayList<>();
    private ArrayList<Integer> score_list=new ArrayList<>();
    private int[]ranking_index=new int[5];
    private String[] result_user_name=new String[5];
    private int[] result_score=new int[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        SQLiteOpenHelper sqliteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();


        //get
        String[] columns = {"user_name ", "score"};
        String selection = "user_name = ?";
        String[] selectionArgs = {"text"};
        String groupBy = null;
        String having = null;
        String orderBy = null;

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT");
        sql.append(" user_name");
        sql.append(" ,score");
        sql.append(" FROM ranking_table;");
        try {
//            Cursor cursor = db.query("ranking_table", columns, selection, selectionArgs, groupBy, having, orderBy);
            Cursor cursor = db.rawQuery(sql.toString(), null);
            StringBuilder text = new StringBuilder();
            while (cursor.moveToNext()){
                String user_name = cursor.getString(0);
                int score = cursor.getInt(1);

                Log.d("username:",user_name);
                Log.d("score:", String.valueOf(score));

                user_name_list.add(user_name);
                score_list.add(score);
            }
        } finally {
            db.close();
        }

        int check=0;

        for(int i=100;i>0;i--){
            for(int j=0;j<score_list.size();j++){
//                Log.d("i:",String.valueOf(i)+","+String.valueOf(score_list.get(j).intValue()));
                if(score_list.get(j).intValue()==i){
                    ranking_index[check]=j;
//                    Log.d("score_list:",String.valueOf(score_list.indexOf(j)));
                    check++;

                    if(check==5){
                        break;
                    }
                }
            }

            if(check==5){
                break;
            }
        }

        for(int i=0;i<ranking_index.length;i++){
            try{
                Log.d("score_index:",String.valueOf(i+1)+","+String.valueOf(ranking_index[i]));
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                break;
            }
        }

        for(int i=0;i<5;i++){
            result_user_name[i]=user_name_list.get(ranking_index[i]);
            result_score[i]=score_list.get(ranking_index[i]);

            Log.d("username:",result_user_name[i]+",score:"+String.valueOf(result_score[i]));
        }

        user_name_text[0]=(TextView)findViewById(R.id.firstUser);
        user_name_text[1]=(TextView)findViewById(R.id.secondUser);
        user_name_text[2]=(TextView)findViewById(R.id.thirdUser);
        user_name_text[3]=(TextView)findViewById(R.id.forthUser);
        user_name_text[4]=(TextView)findViewById(R.id.fifthUser);

        score_text[0]=(TextView)findViewById(R.id.firstScore);
        score_text[1]=(TextView)findViewById(R.id.secondScore);
        score_text[2]=(TextView)findViewById(R.id.thirdScore);
        score_text[3]=(TextView)findViewById(R.id.forthScore);
        score_text[4]=(TextView)findViewById(R.id.fifthScore);

        for(int i=0;i<5;i++){
            user_name_text[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            score_text[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            user_name_text[i].setTextSize(24f);
            score_text[i].setTextSize(24f);

            user_name_text[i].setText(result_user_name[i]);
            score_text[i].setText(String.valueOf(result_score[i])+"点");
        }


    }
}
