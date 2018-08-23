package vn.edu.uit.mmlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

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
            //TODO

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(View view) {
        Intent intent = new Intent(ConfirmActivity.this, MMLabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
