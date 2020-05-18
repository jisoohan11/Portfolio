package com.example.worddrawing.Resource;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.worddrawing.Activity.MainActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadUtils {

    public static void send2Server(File file, final Context context) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("files", file.getName(), RequestBody.create(MultipartBody.FORM, file)).build();
        Request request = new Request.Builder().url(MainActivity.serverURL + "upload").post(requestBody).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TEST : ", response.body().string());
                Toast savedToast = Toast.makeText(context,
                                "response", Toast.LENGTH_SHORT);
                savedToast.show();
            }
        });

    }
}
