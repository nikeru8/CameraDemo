package com.hello.kaiser.camerademo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 100;

    private Button mButton;
    private ImageView mImageView;

    String imageFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSet();
        initListener();
    }

    private void initSet() {
        mButton = (Button) findViewById(R.id.button_take_pic);
        mImageView = (ImageView) findViewById(R.id.image_view_show);
    }

    private void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //創建一個資料夾去存圖片
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //當創建資料夾失敗...
            }

            //當創建的資料夾不為null直，把創建資料夾的路徑帶給相機，並開啟系統資料夾
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.hello.kaiser.camerademo.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        // 獲取你在actiivity_layout地方的ImageView大小
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        //獲取剛剛拍照圖片的大小
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //修改你要顯示圖片的尺寸大小
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        //使用Bitmap size去調整ImageView內顯示的圖片
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);

        //顯影像
        mImageView.setImageBitmap(bitmap);
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
}
