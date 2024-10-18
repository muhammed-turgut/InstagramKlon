package com.muhammedturgut.javainstagramcloneapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muhammedturgut.javainstagramcloneapp.R;
import com.muhammedturgut.javainstagramcloneapp.databinding.ActivityUplodeBinding;

import java.util.HashMap;
import java.util.UUID;

public class UplodeActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Uri imageData;
ActivityResultLauncher<Intent> activityResultLauncher;
ActivityResultLauncher<String> premissonLanucher;
private ActivityUplodeBinding binding;
Bitmap selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplode);

       binding=ActivityUplodeBinding.inflate(getLayoutInflater());
       View view=binding.getRoot();
       setContentView(view);

       registerLanuncher();

        firebaseStorage= FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference();

    }

    public void uplodButtonOnclicked(View view){

        if(imageData != null){
            //universal unique id = her seferinde daha önceden olmayan isim oluşturuyor.

            UUID uuid=UUID.randomUUID();
            String imageName="images/"+uuid+".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   //Dowland url

                    StorageReference newReferance=firebaseStorage.getReference(imageName);
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String dowlandUrl=uri.toString();

                            String comment=binding.commenText.getText().toString();

                            FirebaseUser user=auth.getCurrentUser();
                            String email= user.getEmail();

                            HashMap<String,Object> postData=new HashMap<>();
                            postData.put("useremail",email);
                            postData.put("downloadurl",dowlandUrl);
                            postData.put("comment",comment);
                            postData.put("date", FieldValue.serverTimestamp());//Saat ve tarih aldık

                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent intent=new Intent(UplodeActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UplodeActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(UplodeActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void selectImage(View view){
if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
        Snackbar.make(view,"Permisson needed for galery",Snackbar.LENGTH_INDEFINITE).setAction("Give permisson", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //ask Permission
                premissonLanucher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        }).show();

    }else{
        //ask Permission
        premissonLanucher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

    }
}
else{
    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
activityResultLauncher.launch(intentToGallery);
}

    }
    private void registerLanuncher(){
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== Activity.RESULT_OK){
                    Intent intentResult=result.getData();
                    if(intentResult != null){
                       imageData = intentResult.getData();
                       binding.imageView.setImageURI(imageData);


                       /*try {
                           if(Build.VERSION.SDK_INT>=28){
                               ImageDecoder.Source source=ImageDecoder.createSource(UplodeActivity.this.getContentResolver(),imageData);
                               selectedImage=ImageDecoder.decodeBitmap(source);
                               binding.imageView.setImageBitmap(selectedImage);
                           }
                           else{
                               selectedImage=MediaStore.Images.Media.getBitmap(UplodeActivity.this.getContentResolver(),imageData);
                               binding.imageView.setImageBitmap(selectedImage);
                           }
                       }catch (Exception e){
                           e.printStackTrace();
                       }*/

                    }
                }
            }
        });
        premissonLanucher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){

                    Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                }else{
                    Toast.makeText(UplodeActivity.this,"Permission needed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}