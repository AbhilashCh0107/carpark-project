package com.service.parking.theparker.Controller.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.service.parking.theparker.R;
import com.service.parking.theparker.Services.NetworkServices;
import com.service.parking.theparker.Theparker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends Activity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private final int PICK_IMAGE_REQUEST = 71;
    @BindView(R.id.myProfile_name_et)
    EditText mProfileName;
    @BindView(R.id.myProfile_phone_et)
    EditText mProfileMobileNo;
    @BindView(R.id.myProfile_edit_btn)
    ImageButton mProfileEditbtn;
    @BindView(R.id.myProfile_email_et)
    EditText mProfileEmail;
    @BindView(R.id.car_number_et)
    EditText carNumberEmail;
    @BindView(R.id.myProfile_back_btn)
    ImageButton mProfileBackbtn;
    @BindView(R.id.myProfile_image_iv)
    CircleImageView myProfileImageIv;
    @BindView(R.id.myProfile_save_btn)
    Button myProfileSaveBtn;
    Boolean fromLogin;
    FirebaseUser mCurrentUser;
    //PackageConstants
    private Boolean isEditEnable = true;
    private DatabaseReference newStudent;
    private Uri filePath;

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Theparker.animate(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        newStudent = FirebaseDatabase.getInstance().getReference().child(mCurrentUser.getUid());
        NetworkServices.ProfileData.setData(mProfileName, mProfileEmail, carNumberEmail, mProfileMobileNo);
        NetworkServices.ProfileData.getProfileData();

        Log.d("Name Mobile_No : ", Theparker.Person_name + Theparker.Mobile_no);


        if (mCurrentUser != null) {
            Glide.with(this).load(mCurrentUser.getPhotoUrl()).placeholder(R.drawable.icon_profile)
                    .error(R.drawable.icon_profile)
                    .fallback(R.drawable.icon_profile).into(myProfileImageIv);
        }


        mProfileEditbtn.setOnClickListener(v -> {

            //checkPermission();

            //NetworkServices.ProfileData.init(mProfileName.getText().toString(), mProfileEmail.getText().toString(), getApplicationContext());

            Log.d("Name : ", mProfileName.getText().toString());
            UI_Update();


        });
        myProfileImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImg();
            }
        });

        fromLogin = getIntent().getBooleanExtra("from", false);

        if (fromLogin) {
            mProfileBackbtn.setVisibility(View.INVISIBLE);
        } else {
            mProfileBackbtn.setVisibility(View.VISIBLE);
        }

        mProfileBackbtn.setOnClickListener(v -> {
            onBackPressed();
            Theparker.animate(this);
        });

        myProfileSaveBtn.setOnClickListener(v -> {
            isEditEnable = false;
            UI_Update();
        });

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Objects.requireNonNull(data.getData())));
                    putFile(bitmap);
                    Log.i("chrys", "GET FROM LOCAL.: OK");
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.i("chrys", "GET FROM LOCAL.: RUIM");
                }
            }
        }
    }

    @SuppressLint("IntentReset")
    public void getImg() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        @SuppressLint("IntentReset") Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    public void putFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final StorageReference mountainsRef = storageRef.child("images/" + mCurrentUser.getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("chrys", "Img not set");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(Uri uri) {
                        setProfilePic(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i("chrys", "Img set but whereeee");
                        // Handle any errors
                    }
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setProfilePic(Uri uri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        if (mCurrentUser != null) {
            mCurrentUser.updateProfile(profileUpdates);
            Glide.with(this).load(mCurrentUser.getPhotoUrl()).placeholder(R.drawable.icon_profile)
                    .error(R.drawable.icon_profile)
                    .fallback(R.drawable.icon_profile).into(myProfileImageIv);
        }
    }

    void UI_Update() {

        if (isEditEnable) {

            isEditEnable = false;
            mProfileEmail.setEnabled(true);
            mProfileEmail.setFocusable(true);
            mProfileName.setEnabled(true);
            mProfileName.setFocusable(true);
            carNumberEmail.setEnabled(true);
            carNumberEmail.setFocusable(true);

            mProfileEditbtn.setBackgroundResource(R.drawable.icon_save);

            //NetworkServices.ProfileData.init(mProfileName.getText().toString(), mProfileEmail.getText().toString(), this);

        } else {

            isEditEnable = true;
            mProfileEmail.setEnabled(false);
            mProfileEmail.setFocusable(false);
            mProfileName.setEnabled(false);
            mProfileName.setFocusable(false);
            carNumberEmail.setEnabled(false);
            carNumberEmail.setFocusable(false);

            NetworkServices.ProfileData.updateData(mProfileName.getText().toString(), mProfileEmail.getText().toString(), carNumberEmail.getText().toString().toLowerCase(), this);

            mProfileEditbtn.setBackgroundResource(R.drawable.icon_edit);

            if (fromLogin) {

                SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
                if (sharedPreferences.getString(Theparker.Role, "user").equalsIgnoreCase("user")) {
                    startActivity(new Intent(this, StartActivity.class));
                } else {
                    startActivity(new Intent(this, StartAdminActivity.class));
                }
                finish();
                Theparker.animate(this);
            }

        }

    }

    class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView imageView;

        DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}