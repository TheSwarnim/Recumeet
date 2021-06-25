package com.hackathon.recumeet.Feed;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hackathon.recumeet.Adapter.UploadListAdapter;
import com.hackathon.recumeet.MainActivity;
import com.hackathon.recumeet.R;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    private LinearLayout chooseImage, cameraImg;
    private TextView post;
    private SocialAutoCompleteTextView description;
    private RecyclerView photoRecycler;
    private ImageButton closeBtn;

    private static final int IMAGE_PICK_CODE = 1;
    private static final int CAMERA_CODE = 2;
    private static final int PERMISSION_CODE_GALLERY = 1001;
    private static final int PERMISSION_CODE_CAMERA = 1002;
    private static final int totalItemsSelected = 0;

    private List<String> fileNameList, fileDoneList;
    private String downloadUri;

    private StorageReference mStorage;
    private FirebaseUser fUser;

    private UploadListAdapter uploadListAdapter;
    private String postId;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        
        Init();

        fileNameList.clear();
        fileDoneList.clear();
        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);

        photoRecycler.setLayoutManager(new LinearLayoutManager(this));
        photoRecycler.setHasFixedSize(true);
        photoRecycler.setAdapter(uploadListAdapter);

        mStorage = FirebaseStorage.getInstance().getReference().child("posts");

        closeBtn.setOnClickListener(view -> finish());

        post.setOnClickListener(v -> {
            if (description.getText().toString().isEmpty() && downloadUri == null) {
                Toast.makeText(AddPostActivity.this, "Post cannot be Empty", Toast.LENGTH_SHORT).show();
            } else {
                if (allUpload()) {
                    uploadPost();
                } else {
                    Toast.makeText(AddPostActivity.this, "photos are not uploaded till now", Toast.LENGTH_SHORT).show();
                }
            }
        });

        chooseImage.setOnClickListener(v -> {
            if (downloadUri == null) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE_GALLERY);

                } else {
                    pickImageFromGallery();
                }
            } else {
                Toast.makeText(AddPostActivity.this, "only one image can be selected", Toast.LENGTH_SHORT).show();
            }
        });

        cameraImg.setOnClickListener(v -> {
            if (downloadUri == null) {
                if (checkCameraHardware(AddPostActivity.this)) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.CAMERA};
                        requestPermissions(permissions, PERMISSION_CODE_CAMERA);

                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    Toast.makeText(AddPostActivity.this, "Device Do Not Have Camera", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddPostActivity.this, "only one image can be selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private boolean allUpload() {
        boolean isComplete = false;
        if (fileDoneList.size() == 1) {
            isComplete = true;
        } else if (totalItemsSelected == fileDoneList.size()) {
            isComplete = true;
        }
        return isComplete;
    }

    private void uploadPost() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding Post");
        pd.show();
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");

        postId = ref.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        if (description.getText().toString().trim().isEmpty()) {
            map.put("description", null);
        } else {
            map.put("description", description.getText().toString().trim());
        }
        map.put("publisher", fUser.getUid());
        map.put("postId", postId);
        map.put("ImageUri", downloadUri);

        ref.child(postId).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addHashTags();
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Post Added Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddPostActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(AddPostActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    private void addHashTags() {
        DatabaseReference Hashtagref = FirebaseDatabase.getInstance().getReference().child("HashTags");
        List<String> hashTags = description.getHashtags();
        if (!hashTags.isEmpty()) {
            for (String tag : hashTags) {
                HashMap<String, String> map = new HashMap<>();
                map.put("tag", tag.toLowerCase());
                map.put("postId", postId);

                Hashtagref.child(tag.toLowerCase()).setValue(map);
            }
        }

    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(AddPostActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            case PERMISSION_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(AddPostActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog pd2 = new ProgressDialog(AddPostActivity.this);
        pd2.setMessage("Loading");
        pd2.show();
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            assert data != null;
            if (data.getData() != null) {
                Uri fileUri = data.getData();
                uploadSingleImage(fileUri);
                pd2.dismiss();

            }
        }
        if (resultCode == RESULT_OK && requestCode == CAMERA_CODE) {
            File f = new File(currentPhotoPath);
            assert data != null;
            Uri ImageUri = Uri.fromFile(f);
            uploadSingleImage(ImageUri);
            pd2.dismiss();
        }
        if (data == null) {
            Toast.makeText(AddPostActivity.this, "Nothig is selected", Toast.LENGTH_SHORT).show();
            pd2.dismiss();
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(AddPostActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.fintech.clik.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
    }

    private void uploadSingleImage(Uri fileUri) {
        final ProgressDialog pd3 = new ProgressDialog(AddPostActivity.this);
        pd3.setMessage("Getting Image");
        pd3.show();
        final String fileName = System.currentTimeMillis() + "." + getFileExtension(fileUri);

        fileNameList.add(fileName);
        fileDoneList.add("uploading");

        final StorageReference fileToUpload = mStorage.child(fileName);

        fileToUpload.putFile(fileUri).addOnSuccessListener(taskSnapshot -> fileToUpload.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadUri = uri.toString();
            fileDoneList.clear();
            fileDoneList.add("done");

            uploadListAdapter.notifyDataSetChanged();
            pd3.dismiss();
        }));
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    private void Init() {
        chooseImage = findViewById(R.id.add_img);
        post = findViewById(R.id.post);
        photoRecycler = findViewById(R.id.post_images_recycler);
        description = findViewById(R.id.description);
        cameraImg = findViewById(R.id.add_img_camera);
        closeBtn = findViewById(R.id.close);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        downloadUri = null;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}