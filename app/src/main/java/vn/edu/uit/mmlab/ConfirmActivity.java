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
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.uit.mmlab.ViewCustom.DrawImageView;
import vn.edu.uit.mmlab.modal.PostResult;
import vn.edu.uit.mmlab.modal.RecognitionResult;
import vn.edu.uit.mmlab.modal.UploadSuccess;
import vn.edu.uit.mmlab.network.ApiUtils;
import vn.edu.uit.mmlab.network.MMLabAPI;

public class ConfirmActivity extends AppCompatActivity {
    private final String[] ALGORITHM = {"Face Detection", "Face Recognition", "Image Search", "Object Detection", "Personal attribute", "Upload Gallery"};
    private int algorithm;

    private final int FACE_DETECTION = 0;
    private final int FACE_RECOGNITION = 1;
    private final int IMAGE_SEARCH = 2;
    private final int OBJECT_DETECTION = 3;
    private final int PERSONAL_ATTRIBUTE = 4;

    String check;
    Bitmap yourPicture;

    DrawImageView imgComfirm;
    Button btnSend;

    MMLabAPI mAPI;
    UploadSuccess uploadSuccess;
    PostResult postResult;
    RecognitionResult recognitionResult;

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
        postResult = new PostResult();

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
        btnSend = findViewById(R.id.btnSend);
    }

    /**
     * Get data from MMLab Activity
     */
    private void getData() {

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("data");
        check = bundle.getString("imageFrom", "");

        String path = bundle.getString("uriImage", "");

        algorithm = bundle.getInt("algorithm", FACE_DETECTION);

        imgComfirm.mUri = Uri.parse(path);
        imgComfirm.setImageURI(imgComfirm.mUri);
    }

    /**
     * onClick: Send image client choose -> Post to api
     *
     * @param view
     */
    public void send(View view) {
        try {
            //in progress
            uploadingProgress.show();
            btnSend.setEnabled(false);


            yourPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgComfirm.mUri);
            imgComfirm.yourPicture = yourPicture;

            String encode = encodeImageToBase64(yourPicture);

            ////Post data connection and receive json
            sendPost(encode);
        } catch (IOException e) {
            btnSend.setEnabled(true);
            uploadingProgress.dismiss();
            e.printStackTrace();
        }
    }

    /**
     * Encode image to base 64 and convert to string
     *
     * @param bitmap input
     * @return base 64 image
     */
    private String encodeImageToBase64(Bitmap bitmap) {
        //Encode Image to Base 64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String img64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String img64Combine = "data:image/jpg;base64," + img64;

        return img64Combine;
    }

    /**
     * Send string image encoded to api mmlab use Retrofit
     *
     * @param payload
     */
    private void sendPost(String payload) {
        mAPI.imagePost("base64", payload).enqueue(new Callback<UploadSuccess>() {
            @Override
            public void onResponse(Call<UploadSuccess> call, Response<UploadSuccess> response) {
                uploadSuccess = response.body();

                if (uploadSuccess == null) {
                    btnSend.setEnabled(true);
                    uploadingProgress.dismiss();
                    notifyFailure("Something wrong with your internet");
                } else {
                    Log.i("POST", uploadSuccess.getFileID());
                    Toast.makeText(ConfirmActivity.this, "Upload sucessfull", Toast.LENGTH_SHORT).show();

                    //After we sent image to sever, we choose algorithm
                    chooseAlgorithm();
                }
            }

            @Override
            public void onFailure(Call<UploadSuccess> call, Throwable t) {
                btnSend.setEnabled(true);
                uploadingProgress.dismiss();
                notifyFailure("Something wrong with your internet");
            }
        });
    }

    private void chooseAlgorithm() {
        switch (algorithm) {
            case FACE_DETECTION: {
                getFaceDetection();
                break;
            }
            case FACE_RECOGNITION: {
                getFaceDetection();
                break;
            }
            case IMAGE_SEARCH: {
                //TODO
                break;
            }
            case OBJECT_DETECTION: {
                //TODO
                break;
            }
            case PERSONAL_ATTRIBUTE: {
                //TODO
                break;
            }
            default: {
                Toast.makeText(this, "Hello man", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * After post string image, we GET id image in sever and GET json face location in this picture
     */
    private void getFaceDetection() {
        mAPI.getFaceDectection("out.jpg", uploadSuccess.getFileID(), "model1").enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                postResult = response.body();

                if (postResult != null) {
                    if (algorithm == FACE_RECOGNITION) {
                        Gson g = new Gson();
                        String option = "{%22range%22:" + g.toJson(postResult.getFaceInfor()) + ",%22folder%22:%22%22}";
                        Log.i("object", option);

                        imgComfirm.listRec = postResult.toListRectangle();
                        imgComfirm.invalidate();

                        //face_recognition
                        getFaceRecognition(option);
                    } else {
                        imgComfirm.listRec = postResult.toListRectangle();

                        imgComfirm.invalidate();

                        uploadingProgress.dismiss();
                        btnSend.setEnabled(true);
                        Toast.makeText(ConfirmActivity.this, "Detection Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    uploadingProgress.dismiss();
                    btnSend.setEnabled(true);
                    notifyFailure("Something wrong with your internet");
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                uploadingProgress.dismiss();
                notifyFailure("Something wrong with your internet");
                btnSend.setEnabled(true);
            }
        });
    }

    /**
     * We must getFaceDetection before getFaceRecognition, because range list
     */
    private void getFaceRecognition(String option) {
        mAPI.getFaceRecognition("out.jpg", uploadSuccess.getFileID(), "modelface1", option).enqueue(new Callback<RecognitionResult>() {
            @Override
            public void onResponse(Call<RecognitionResult> call, Response<RecognitionResult> response) {
                recognitionResult = response.body();
                uploadingProgress.dismiss();
                btnSend.setEnabled(true);

                if (postResult != null) {
                    //imgComfirm.listRec = postResult.toListRectangle();

                    //imgComfirm.invalidate();

                    Toast.makeText(ConfirmActivity.this, "Recognition Success", Toast.LENGTH_SHORT).show();
                } else {
                    notifyFailure("Something wrong with your internet");
                    btnSend.setEnabled(true);
                    notifyFailure("Something wrong with your internet");
                }
            }

            @Override
            public void onFailure(Call<RecognitionResult> call, Throwable t) {
                uploadingProgress.dismiss();
                notifyFailure("Something wrong with your internet");
                btnSend.setEnabled(true);
            }
        });
    }

    /**
     * Notification failure when we have error with internet or something
     *
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
     *
     * @param view
     */
    public void cancel(View view) {
        Intent intent = new Intent(ConfirmActivity.this, MMLabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
