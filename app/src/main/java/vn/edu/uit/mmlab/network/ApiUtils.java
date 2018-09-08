package vn.edu.uit.mmlab.network;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://api.mmlab.uit.edu.vn/";

    public static MMLabAPI getAPIService() {

        return RetrofitClientInstance.getRetrofitInstance(BASE_URL).create(MMLabAPI.class);
    }
}
