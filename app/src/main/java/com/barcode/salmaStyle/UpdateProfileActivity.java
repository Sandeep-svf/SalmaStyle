package com.barcode.salmaStyle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.barcode.salmaStyle.R;
import com.barcode.salmaStyle.RetroifitApi.APIService;
import com.barcode.salmaStyle.RetroifitApi.ApiClient;
import com.barcode.salmaStyle.RetroifitApi.RetroifitErrorResponse.ApiClientNoToken;
import com.barcode.salmaStyle.imagepicker.ImagePickerActivity;
import com.barcode.salmaStyle.model.CountryCodeMOdel;
import com.barcode.salmaStyle.model.ProfileModel;
import com.barcode.salmaStyle.model.UpdateProfileModel;
import com.barcode.salmaStyle.response.CountryCodeResponse;
import com.barcode.salmaStyle.response.ProfileResponse;
import com.barcode.salmaStyle.utol.CustomProgressbar;
import com.barcode.salmaStyle.utol.Originator;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class UpdateProfileActivity extends Originator {

    EditText edt_name, edt_phone;
    TextView edt_email;
    CardView update_profile;
    ImageView profile_image, imageView;
    String str_name, str_phone, str_email;
    File photo1 = null;
    ProfileResponse profileResponse;
    TextView toolbar_text;
    TextView upload_image;
    ConstraintLayout back;
    TextView text;
    Spinner sp_country;
    String countr_code = "";
    List<String> countryList = new ArrayList<>();
    List<CountryCodeResponse> countryCodeResponseList = new ArrayList<>();
    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        edt_name = findViewById(R.id.edt_name_update);
        edt_phone = findViewById(R.id.edt_phone_update);
        edt_email = findViewById(R.id.edt_email_update);
        profile_image = findViewById(R.id.profile_images);
        update_profile = findViewById(R.id.btn_update_profile1);
        toolbar_text = findViewById(R.id.toolbar_text);
        back = findViewById(R.id.cons_tool_img);
        upload_image = findViewById(R.id.upload_image_button);
        sp_country = findViewById(R.id.spinner_country);
        toolbar_text.setText(getString(R.string.update_profile));
        Glide.with(this).load(R.drawable.profile_front_image).into(profile_image);
        // toolbar_text.setText(R.string.update_profile);
        countryApi();
        profileApi();
        backListener();
        imageListener();
        updateprofileListener();
    }

    private void countryApi() {
        //  CustomProgressbar.showProgressBar(this, false);
        APIService service = ApiClientNoToken.getClient().create(APIService.class);
        retrofit2.Call<CountryCodeMOdel> call = service.countryList();
        call.enqueue(new Callback<CountryCodeMOdel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<CountryCodeMOdel> call, @NonNull retrofit2.Response<CountryCodeMOdel> response) {
                //   CustomProgressbar.hideProgressBar();
                try {
                    if (response.isSuccessful()) {
                        String message = response.body().getMessage();
                        String success = response.body().getStatus();

                        if (success.equalsIgnoreCase("True")) {
                            //   Toast.makeText(UpdateProfileActivity.this,R.string.user_login_successful, Toast.LENGTH_SHORT).show();
                            countryCodeResponseList = response.body().getCountryCodeResponseList();

                            for (int i = 0; i < countryCodeResponseList.size(); i++) {
                                countryList.add(countryCodeResponseList.get(i).getCountryCode());
                            }

                            sp_country.setAdapter(new ArrayAdapter<String>(UpdateProfileActivity.this, R.layout.simple_spinner_row, R.id.rowTextView, countryList));
                            sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    countr_code = countryList.get(position).toString();
                                    Log.e("prov_check", "    " + countr_code);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    //  Toasty.error(EditProfileActivity.this, "002255555", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            Toast.makeText(UpdateProfileActivity.this, R.string.failed_error, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        try {
                            //  CustomProgressbar.hideProgressBar();
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            switch (response.code()) {
                                case 400:
                                    Toast.makeText(UpdateProfileActivity.this, "The server did not understand the request.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 401:
                                    Toast.makeText(UpdateProfileActivity.this, "Unauthorized The requested page needs a username and a password.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 404:
                                    Toast.makeText(UpdateProfileActivity.this, "The server can not find the requested page.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(UpdateProfileActivity.this, "Internal Server Error..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 503:
                                    Toast.makeText(UpdateProfileActivity.this, "Service Unavailable..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 504:
                                    Toast.makeText(UpdateProfileActivity.this, "Gateway Timeout..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 511:
                                    Toast.makeText(UpdateProfileActivity.this, "Network Authentication Required ..", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(UpdateProfileActivity.this, "unknown error", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException exception) {
                    exception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //  CustomProgressbar.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<CountryCodeMOdel> call, Throwable t) {
                //   CustomProgressbar.hideProgressBar();
                if (t instanceof IOException) {
                    Toast.makeText(UpdateProfileActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("conversion issue", t.getMessage());
                    Toast.makeText(UpdateProfileActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void backListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void imageListener() {
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = profile_image;
                proceedAfterPermission();
            }
        });
    }

    private void proceedAfterPermission() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {

                    cameraIntent();

                } else if (options[item].equals("Choose from Gallery")) {

                    galleryIntent();

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void galleryIntent() {

        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);

    }

    private void cameraIntent() {

        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);

    }

    private void updateprofileListener() {

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                str_name = edt_name.getText().toString();
                str_email = edt_email.getText().toString();
                str_phone = edt_phone.getText().toString();
                String phone_with_country = countr_code + "" + str_phone;

                if (str_name.equals("")) {
                    Toast toast = Toast.makeText(UpdateProfileActivity.this, getString(R.string.enter_name), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (str_phone.equals("")) {
                    Toast toast = Toast.makeText(UpdateProfileActivity.this, getString(R.string.enter_mobileno), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    updatprofileApi(phone_with_country);
                }
            }
        });

    }

    private void updatprofileApi(String phone_with_country) {

        MultipartBody.Part profileImage = null;

        try {
            try {
                RequestBody requestfaceFile = RequestBody.create(photo1, MediaType.parse("image/*"));
                profileImage = MultipartBody.Part.createFormData("profile", photo1.getName(), requestfaceFile);

            } catch (Exception e) {
                Log.e("conversionException", "errr" + e.getMessage());
            }
        } catch (Exception e) {
            Log.v("dahgsdhjgdfhjs", "***********************************************" + e);
            //Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }

        CustomProgressbar.showProgressBar(this, false);

        APIService service = ApiClient.getClient().create(APIService.class);
        retrofit2.Call<UpdateProfileModel> call = service.getupdateprofile(str_name, phone_with_country, str_email, profileImage);
        call.enqueue(new Callback<UpdateProfileModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<UpdateProfileModel> call, @NonNull retrofit2.Response<UpdateProfileModel> response) {
                CustomProgressbar.hideProgressBar();

                try {
                    if (response.isSuccessful()) {
                        String message = response.body().getMessage();
                        String success = response.body().getStatus();

                        if (success.equalsIgnoreCase("True")) {
                            Toast.makeText(UpdateProfileActivity.this, R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                            intent.putExtra("login_key", "login_value");
                            startActivity(intent);

                        } else {
                            Toast.makeText(UpdateProfileActivity.this, R.string.failed_error, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            CustomProgressbar.hideProgressBar();
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            switch (response.code()) {
                                case 400:
                                    Toast.makeText(UpdateProfileActivity.this, "The server did not understand the request.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 401:
                                    Toast.makeText(UpdateProfileActivity.this, "Unauthorized The requested page needs a username and a password.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 404:
                                    Toast.makeText(UpdateProfileActivity.this, "The server can not find the requested page.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(UpdateProfileActivity.this, "Internal Server Error..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 503:
                                    Toast.makeText(UpdateProfileActivity.this, "Service Unavailable..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 504:
                                    Toast.makeText(UpdateProfileActivity.this, "Gateway Timeout..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 511:
                                    Toast.makeText(UpdateProfileActivity.this, "Network Authentication Required ..", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(UpdateProfileActivity.this, "unknown error", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException exception) {
                    exception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CustomProgressbar.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<UpdateProfileModel> call, Throwable t) {
                CustomProgressbar.hideProgressBar();
                if (t instanceof IOException) {
                    Toast.makeText(UpdateProfileActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("conversion issue", t.getMessage());
                    Toast.makeText(UpdateProfileActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                // String sel_path = getpath(uri);
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    //loadProfile(uri.toString());

                    photo1 = new File(uri.getPath());
                    Log.e("file ", "path " + photo1.getAbsolutePath());
                    // addImage_file_list_models.add(file);

                    Glide.with(this).load(uri.toString())
                            .into(profile_image);
                } catch (IOException e) {
                    Log.e("file_exception ", "path " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    private void profileApi() {

        CustomProgressbar.showProgressBar(this, false);

        APIService service = ApiClient.getClient().create(APIService.class);
        retrofit2.Call<ProfileModel> call = service.getprofile();
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ProfileModel> call, @NonNull retrofit2.Response<ProfileModel> response) {
                CustomProgressbar.hideProgressBar();
                try {
                    if (response.isSuccessful()) {
                        String message = response.body().getMessage();
                        String success = response.body().getStatus();
                        if (success.equalsIgnoreCase("True")) {
                            // sendVerificationCode(newPhoneNumber);
                            profileResponse = response.body().getProfileResponse();
                            String profile_url = profileResponse.getProfile();
                            edt_name.setText(profileResponse.getName());
                            edt_phone.setText("" + profileResponse.getPhone());
                            edt_email.setText(profileResponse.getEmail());
                            if (profile_url.equals("null")) {
                                Glide.with(UpdateProfileActivity.this).load(R.drawable.profile_front_image).into(profile_image);
                            } else {
                                String image_url = "http://69.49.235.253:8000" + profile_url;
                                Glide.with(UpdateProfileActivity.this).load(image_url).into(profile_image);
                                Log.e("success_my_profile", "               " + image_url);
                            }

                        } else {
                            Toast.makeText(UpdateProfileActivity.this, R.string.failed_error, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            CustomProgressbar.hideProgressBar();
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            switch (response.code()) {
                                case 400:
                                    Toast.makeText(UpdateProfileActivity.this, "The server did not understand the request.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 401:
                                    Toast.makeText(UpdateProfileActivity.this, "Unauthorized The requested page needs a username and a password.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 404:
                                    Toast.makeText(UpdateProfileActivity.this, "The server can not find the requested page.", Toast.LENGTH_SHORT).show();
                                    break;
                                case 500:
                                    Toast.makeText(UpdateProfileActivity.this, "Internal Server Error..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 503:
                                    Toast.makeText(UpdateProfileActivity.this, "Service Unavailable..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 504:
                                    Toast.makeText(UpdateProfileActivity.this, "Gateway Timeout..", Toast.LENGTH_SHORT).show();
                                    break;
                                case 511:
                                    Toast.makeText(UpdateProfileActivity.this, "Network Authentication Required ..", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(UpdateProfileActivity.this, "unknown error", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException exception) {
                    exception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CustomProgressbar.hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, Throwable t) {
                CustomProgressbar.hideProgressBar();
                if (t instanceof IOException) {
                    Toast.makeText(UpdateProfileActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("conversion issue", t.getMessage());
                    Toast.makeText(UpdateProfileActivity.this, "Please Check your Internet Connection....", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}