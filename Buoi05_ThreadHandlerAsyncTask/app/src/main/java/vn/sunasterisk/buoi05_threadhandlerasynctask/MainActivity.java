package vn.sunasterisk.buoi05_threadhandlerasynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    private TextView mTextNumber;
    private Button mButtonStart;
    private Button mButtonDownloadHotGirl;
    private ImageView mImageHotGirl;

    private Handler mHandler;

    private static final int MSG_UPDATE_NUMBER = 999;
    private static final int MSG_UPDATE_NUMBER_DONE = 998;

    private boolean isCounting;

    private static final String LINK = "https://vnn-imgs-f.vgcloud." +
            "vn/2019/05/03/11/co-gai-khien-hot-girl-tram-anh-bi-lu-mo-" +
            "vi-qua-xinh-dep-3.jpg";

    private static final String LINK2 = "https://i-dulich.vnecdn." +
            "net/2018/10/03/42803623-2223161361291996-" +
            "7580101494817423360-n_680x0.jpg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        registerListeners();
    }

    private void registerListeners() {
        mButtonStart.setOnClickListener(this);
        mButtonDownloadHotGirl.setOnClickListener(this);
        listenerHandler();
    }

    private void listenerHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_NUMBER:
                        isCounting = true;
                        mTextNumber.setText(String.valueOf(msg.arg1));
                        mTextNumber.setTextColor(getRandomColor());
                        break;
                    case MSG_UPDATE_NUMBER_DONE:
                        isCounting = false;
                        Toast.makeText(MainActivity.this,
                                "DONE",
                                Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        };
    }

    private int getRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        return Color.argb(255, r, g, b);
    }

    private void initViews() {
        mTextNumber = findViewById(R.id.text_number);
        mButtonStart = findViewById(R.id.button_start);
        mButtonDownloadHotGirl = findViewById(R.id.button_download_image);
        mImageHotGirl = findViewById(R.id.image_hot_girl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                if (!isCounting) {
                    countNumbers();
                }
                break;
            case R.id.button_download_image:
                downloadAndShowImage();
                break;
            default:
                break;
        }
    }

    private void downloadAndShowImage() {
        //new ImageDownloadAsync().execute(LINK);
        new ImageDownloadAsync().execute(LINK2);
    }

    private void countNumbers() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 10; i++) {
                    Message message = new Message();
                    message.what = MSG_UPDATE_NUMBER;
                    message.arg1 = i;
                    mHandler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(MSG_UPDATE_NUMBER_DONE);
            }
        }).start();
    }

    private class ImageDownloadAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Phuong thuc nay chay trong Worker Thread
         *
         * @param strings
         * @return
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                String link = strings[0];
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageHotGirl.setImageBitmap(bitmap);
        }
    }
}
