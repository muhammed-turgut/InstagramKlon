package com.muhammedturgut.javainstagramcloneapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.muhammedturgut.javainstagramcloneapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
     private ActivityMainBinding binding;
     private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();

        //Kullanıcı Önceden giriş yapmış mı yapmamıimı  kontrolü. Kullanıcı uygulamaya bir kez giriş yaptıyasa yeniden giriş ekranına göndermiyor.
       FirebaseUser user=auth.getCurrentUser();
       if(user != null){
           Intent intent=new Intent(MainActivity.this,FeedActivity.class);
           startActivity(intent);
           finish();
       }


    }
    public void signinClicked(View view){
        String email=binding.emailText.getText().toString();
        String password=binding.paswordText.getText().toString();

        if (email.equals("")|| password.equals("")){
         Toast.makeText(MainActivity.this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{

            //addOnSuccess methodu giriş başarılı ise ne yapılacağını söylüyor.
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();

                }

                // addOnFailureListener methodu bize email veya şifre hatalı girerse ne yapcağımızı söylüyor.
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this,"Hatalı Giriş",Toast.LENGTH_LONG).show();
                }
            });
        }



    }
    public void signupClicked(View view){
        String email=binding.emailText.getText().toString();
        String pasword=binding.paswordText.getText().toString();
        if (email.equals("")||pasword.equals("")){
            Toast.makeText(this, "Enter email or paswsword", Toast.LENGTH_LONG).show();
        }
        else {

            auth.createUserWithEmailAndPassword(email,pasword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //Burada onSuccsess Metodunu amacı ise kayıt başarılı olursa ne yapılacağını bildirmek.

                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }

                //Burada addOnFailurel kullanama sebebimiz kayıt sırasında bir hata olursa onu kullanıcıya bildirelim
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                 Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }



    }

}