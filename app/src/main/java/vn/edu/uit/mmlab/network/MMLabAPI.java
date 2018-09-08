package vn.edu.uit.mmlab.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.edu.uit.mmlab.modal.FaceResult;
import vn.edu.uit.mmlab.modal.UploadSuccess;

public interface MMLabAPI {

    @GET("/api/v1/vision/face-detection")
    Call<FaceResult> getFaceDectection(@Query ("fileName") String fileName,
                                              @Query ("fileID") String fileID,
                                              @Query ("method") String method);

    @POST("/api/v1/file/image")
    @FormUrlEncoded
    Call<UploadSuccess> imagePost(@Query ("method") String method, @Field("base64")  String base64);
}
