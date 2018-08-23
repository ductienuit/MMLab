package vn.edu.uit.mmlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ConfirmActivity extends AppCompatActivity {
    Uri imageUri;
    String check;

    ImageView imgComfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        addControl();
        getData();
    }

    private void addControl() {
        imgComfirm = findViewById(R.id.imgConfirm);
    }

    private void getData() {

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("data");
        check = bundle.getString("imageFrom", "");

        String path = bundle.getString("uriImage", "");
        imageUri = Uri.parse(path);
        imgComfirm.setImageURI(imageUri);
    }

    public void send(View view) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            String encode = encodeImageToBase64(bitmap);

            //Post data connection and receive json
            sendPost(encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        //Encode Image to Base 64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String img64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String img64Combine = "data:image/jpeg;base64,"+ img64;
        String payload = null;
        try {
            payload = "&base64=" + URLEncoder.encode(img64Combine,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return payload;
    }

    private void sendPost(String payload) {

    }

    public void cancel(View view) {
        Intent intent = new Intent(ConfirmActivity.this, MMLabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
