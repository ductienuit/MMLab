package vn.edu.uit.mmlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.uit.mmlab.modal.FaceResult;
import vn.edu.uit.mmlab.modal.UploadSuccess;
import vn.edu.uit.mmlab.network.ApiUtils;
import vn.edu.uit.mmlab.network.MMLabAPI;

public class ConfirmActivity extends AppCompatActivity {
    Uri imageUri;
    String check;

    ImageView imgComfirm;
    MMLabAPI mAPI;
    UploadSuccess uploadSuccess;
    FaceResult faceResult;

    android.app.AlertDialog uploadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        init();
    }

    private void init() {
        addControl();
        getData();

        uploadSuccess = new UploadSuccess();
        faceResult = new FaceResult();

        mAPI = ApiUtils.getAPIService();

        uploadingProgress = new SpotsDialog
                .Builder()
                .setContext(this)
                .setTheme(R.style.CustomProgress)
                .setCancelable(false)
                .build();
    }

    private void addControl() {
        imgComfirm = findViewById(R.id.imgConfirm);
    }

    /**
     * Get data from MMLab Activity
     */
    private void getData() {

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("data");
        check = bundle.getString("imageFrom", "");

        String path = bundle.getString("uriImage", "");
        imageUri = Uri.parse(path);
        imgComfirm.setImageURI(imageUri);
    }

    /**
     * onClick: Send image client choose -> Post to api
     * @param view
     */
    public void send(View view) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            String encode = encodeImageToBase64(bitmap);
            uploadingProgress.show();
            ////Post data connection and receive json
            sendPost(encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Encode image to base 64 and convert to string
     * @param bitmap input
     * @return base 64 image
     */
    private String encodeImageToBase64(Bitmap bitmap) {
        //Encode Image to Base 64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String img64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String img64Combine = "data:image/jpg;base64,"+ img64;

        return img64Combine;
    }

    /**
     * Send string image encoded to api mmlab use Retrofit
     * @param payload
     */
    private void sendPost(String payload) {
        mAPI.imagePost("base64",payload).enqueue(new Callback<UploadSuccess>() {
            @Override
            public void onResponse(Call<UploadSuccess> call, Response<UploadSuccess> response) {
                uploadSuccess = response.body();

                if(uploadSuccess == null)
                {
                    uploadingProgress.dismiss();
                    notifyFailure("Something wrong with your internet");
                }
                else{
                    Log.i("POST",uploadSuccess.getFileID());
                    getFaceDetection();
                }
            }

            @Override
            public void onFailure(Call<UploadSuccess> call, Throwable t) {
                uploadingProgress.dismiss();
                notifyFailure("Something wrong with your internet");
            }
        });
    }

    /**
     * After post string image, we GET id image in sever and GET json face location in this picture
     */
    private void getFaceDetection() {
        mAPI.getFaceDectection("out.jpg", uploadSuccess.getFileID(),"model1").enqueue(new Callback<FaceResult>() {
            @Override
            public void onResponse(Call<FaceResult> call, Response<FaceResult> response) {
                faceResult = response.body();

                uploadingProgress.dismiss();
                if(faceResult!=null)
                {
                    Toast.makeText(ConfirmActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
                else{
                    notifyFailure("Something wrong with your internet");
                }
            }

            @Override
            public void onFailure(Call<FaceResult> call, Throwable t) {
                uploadingProgress.dismiss();
                notifyFailure("Something wrong with your internet");
            }
        });
    }

    /**
     * Notification failure when we have error with internet or something
     * @param error String error
     */
    private void notifyFailure(String error) {
        new SweetAlertDialog(ConfirmActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Error")
                .setContentText(error)
                .setCancelText("Back to screen")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    /**
     * Back to MMLab Activity
     * @param view
     */
    public void cancel(View view) {
        Intent intent = new Intent(ConfirmActivity.this, MMLabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
