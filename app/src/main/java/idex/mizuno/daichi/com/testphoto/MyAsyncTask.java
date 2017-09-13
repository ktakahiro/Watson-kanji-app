package idex.mizuno.daichi.com.testphoto;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.StringTokenizer;


class MyAsyncTask extends AsyncTask<Integer, Object, Void> {

    private int score;
    private MainActivity mainActivity;

    private Bundle score_bundle;
    private int max_text_number;
    private int number;

    private Context context;

    private ProgressDialog progressDialog;


    public MyAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
        service.setApiKey("23bb0d6e7020b58a8ff362347b2845eb1ef7205e");

        number=(int)params[0];
        max_text_number=(int)params[1];

        ServiceCall<List<VisualClassifier>> call=service.getClassifiers();
        List<VisualClassifier> calls=call.execute();

        for(int i=0; i<calls.size();i++){
            Log.d("classify",calls.toString());
        }

        Log.d("params:",params.toString());
        String path = Environment.getExternalStorageDirectory().getPath()+"/Download/test"+String.valueOf(params[0])+".png";

        ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                .classifierIds("moji_2032281277")
                .images(new File(path))
                .threshold(0.0)
                .build();

        VisualClassification result = service.classify(options).execute();

        Log.d("VISUALCLASSIFICATION",result.toString());

        try {
            JSONObject json = new JSONObject(result.toString());
            JSONArray datas = json.getJSONArray("images").getJSONObject(0).getJSONArray("classifiers").getJSONObject(0).getJSONArray("classes");
            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.getJSONObject(i);
                // クラス名を取得
                String classes = data.getString("class");


                // スコアを取得
                String score = data.getString("score");


                byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.parseInt(classes)).array();

                String moji = String.valueOf((char)Integer.parseInt(classes));
                Log.d("BYTES",bytes+"");
                //String moji = new String(bytes,"SJIS");

                Log.d("RESULT",moji+" : "+score);
                float score_float=Float.parseFloat(score)*100f;

                this.score=(int)(score_float);

                if((int)params[0]==5){
                    MainActivity mainActivity=new MainActivity();
                    mainActivity.dismiss_Dialog();
                }

                Log.d("params:",String.valueOf(params[2])+",classes:"+Integer.parseInt(classes));
                if(classes==String.valueOf(params[2])){
                    Log.d("result:score:",String.valueOf(score));
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MyAsyncTask","json failed!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        //メインスレッドで実行される処理
        //（非同期処理の結果をUIに反映する等）

        SharedPreferences sharedPref = context.getSharedPreferences("score",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(String.valueOf(number), score);
        editor.putInt("max_number",max_text_number);
        editor.commit();

        Log.d("number:", String.valueOf(sharedPref.getInt(String.valueOf(number),0)));

        if(number==max_text_number){
            Intent intent=new Intent(context,ResultActivity.class);
            context.startActivity(intent);
        }
    }
}

