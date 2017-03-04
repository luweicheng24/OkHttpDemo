package com.example.administrator.okhttpdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends  AppCompatActivity {

   OkHttpClient okHttpClient = new OkHttpClient();
    public static final String url = "http://10.2.3.5:8080/LoginDemo/login?name=luweicheng&key=123";
    private TextView tv;
    private String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        img = (ImageView) findViewById(R.id.img);
        if(Build.VERSION.SDK_INT>=23){
                 L.e("版本"+Build.VERSION.SDK_INT);
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,permission,0);
            }

        }


    }

    /**
     * get请求
     * @param view
     */
    public void doGet(View view){
        //Request request = new Request.Builder().url("").build();
        Request request = new Request.Builder().get().url(url).build();
        okhttp3.Call call =  okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                 L.e("failure"+e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                 L.e("success"+response.body());
                  InputStream is =  response.body().byteStream();
                  final StringBuilder str = new StringBuilder();
                   int len;
                   byte[] buf = new byte[1024];
                  while ((len = is.read(buf))!=-1){
                      str.append(new String(buf));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(str);
                    }
                });
            }
        });



    }

    /**
     * post请求
     * @param view
     */
    public void doPost(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder().add("name", "kobe").add("key", "123456").build();
                Request request = new Request.Builder().post(body).url("http://10.2.3.5:8080/LoginDemo/login").build();
                okhttp3.Call call = okHttpClient.newCall(request);
                try {
                    final Response response = call.execute();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                tv.setText(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 下载
     * @param view
     */
    public void doDownLoad(View view){
        Toast.makeText(this,"开始下载",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().get().url("http://10.2.3.5:8080/test1/login.png").build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        L.e("请求失败"+e.getMessage());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        L.e(response.body().contentLength()+""+Environment.getExternalStorageState());
                        final InputStream is = response.body().byteStream();
                       /* final Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ban.png");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageBitmap(bmp);

                            }
                        });*/
                        File file = new File(Environment.getExternalStorageDirectory(),"ban.png");
                        if(!file.exists()){
                            L.e(file.getAbsolutePath()+"不存在");
                            file.createNewFile();
                            return ;
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        int len;
                        byte[] buf = new byte[128];
                        while ((len = is.read(buf))!=-1){
                            fos.write(buf,0,len);
                        }

                        try {
                            fos.flush();
                            is.close();
                            fos.close();
                        }catch (IOException e){
                                 e.printStackTrace();
                        }
                        L.e("下载成功");
                    }
                });

            }
        }

        ).start();
    }

    /**
     * 上传string文件
     * @param view
     */
    public void doPostString(View  view){
        new Thread(new Runnable() {
           @Override
           public void run() {
               RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),"{username:luwei,password:123}");
               Request request = new Request.Builder().post(requestBody).url("http:10.2.3.5:8080/test1/loginString").build();
               try {
                   okHttpClient.newCall(request).execute();
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
       }).start();

    }

    public void upLoad(View  view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ban.png");
                if(!file.exists()){
                    L.e(file.getAbsolutePath()+"不存在");
                }
                 L.e(file.length()+"");
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
                Request request = new Request.Builder().post(requestBody).url("http://10.2.3.5:8080/test1/upLoadFile").build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        L.e("上传失败"+e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        L.e("上传成功");
                    }
                });



            }
        }).start();

    }
}