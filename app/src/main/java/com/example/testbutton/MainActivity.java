package com.example.testbutton;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Datable{

    //path for recorder
    String outputFile;
    Button btn;

    ArrayList<String> paths = new ArrayList<>();

    CountDownTimer countDownTimer;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView addImage;

    String imageUserURL;

    LayoutInflater li;
    SharedPreferences sharedPreferences;

    ImageView imageUser;

    long l;
    Date currentDate;
    SimpleDateFormat dateFormat;

    //recorder
    MediaRecorder mRecorder = null;


    RecyclerView recyclerViewAudio;
    MessegesAdapter adapter;
    TextView nameAuthor;

    String textPromt = "someText";
    DialogNameAudio dialogNameAudio;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);

        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        addImage = (ImageView) findViewById(R.id.addPhoto);

        recyclerViewAudio = (RecyclerView) findViewById(R.id.recyclerViewAudio);

        View page_user = (View)findViewById(R.id.main_page_user);

        nameAuthor = page_user.findViewById(R.id.user_name);
        imageUser = page_user.findViewById(R.id.user_image);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, 100);
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, 11);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        adapter = new MessegesAdapter();

        recyclerViewAudio.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAudio.setAdapter(adapter);

        saveDate();


//compare date
        currentDate = new Date();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String strFormat = dateFormat.format(currentDate);
        try {
            Date date = dateFormat.parse(strFormat);
            if(loadDate().before(date)){
                saveData();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //l=loadData();
        System.out.println(loadData()+"ccccccccccccccc");

        dialogNameAudio = new DialogNameAudio();

        if (mAuth.getCurrentUser()!=null){

            db.collection("users").whereEqualTo("email", mAuth.getCurrentUser().getEmail().toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    for(User u : users)
                    nameAuthor.setText(u.getNameUser());
                }
            });
        }else{
            regester();
        }

        //save file

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    if(loadData() > 0){
                        onRecord(true);
                        onResume_();
                        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Limit time", Toast.LENGTH_LONG).show();
                    }

                } else if(event.getAction() == MotionEvent.ACTION_UP) {

                    onRecord(false);

                    if(loadData() > 0){
                    //и отображаем dialog:
                    showDialog();
                    countDownTimer.cancel();
                    }
                }
                return false;
            }
        });



    }

    private void saveDate(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        currentDate = new Date();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String strFormat = dateFormat.format(currentDate);

        ed.putString("current_date", strFormat);
        ed.commit();
    }
    private void saveData(){
        sharedPreferences = getSharedPreferences("limit_times",MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong("limit_time", 10000);
        ed.commit();
    }

    private void saveDataOnLimit(long timeFinish){
        sharedPreferences = getSharedPreferences("limit_times",MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong("limit_time", timeFinish);
        ed.commit();
    }

    private long loadData(){
        sharedPreferences = getSharedPreferences("limit_times",MODE_PRIVATE);
        return sharedPreferences.getLong("limit_time", 0);
    }

    private Date loadDate(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String strFormat = sharedPreferences.getString("current_date", "");
        try {
            Date date = dateFormat.parse(strFormat);
            return date;
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "FailDate", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    //image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                assert uri != null;
                final StorageReference referenceToImage =storageRef.child("mailPhoto/"+mAuth.getCurrentUser().getEmail()+"/"+uri.getPathSegments());
                referenceToImage.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
                        }

                        // Continue with the task to get the download URL
                        return referenceToImage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final Uri downloadUri = task.getResult();


                            db.collection("avas").document(mAuth.getCurrentUser().getEmail()).set(new Ava(mAuth.getCurrentUser().getEmail(), downloadUri.toString(), System.currentTimeMillis()));
                            upDateImg();


                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        }

        if(requestCode == 11 && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                assert uri != null;
                final StorageReference referenceToImage =storageRef.child("images/"+mAuth.getCurrentUser().getEmail()+"/"+uri.getPathSegments());

                referenceToImage.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
                        }

                        // Continue with the task to get the download URL
                        return referenceToImage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final Uri downloadUr = task.getResult();


                            db.collection("img").document(mAuth.getCurrentUser().getEmail()).set(new ImageUser(mAuth.getCurrentUser().getEmail().toString(), downloadUr.toString(), System.currentTimeMillis()));
                            upDateUserImg();


                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        }
    }

       @RequiresApi(api = Build.VERSION_CODES.KITKAT)
       @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()!=null){
            upDateData();
            upDateImg();
            upDateUserImg();
        }

    }

    private  void upDateImg(){
        DocumentReference documentReference = db.collection("avas").document(mAuth.getCurrentUser().getEmail());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists())
                Picasso.get().load(document.get("urlImage").toString()).resize(100,100).centerCrop().into(imageUser);
            }
        });

    }

    private  void upDateUserImg(){
        DocumentReference documentReference = db.collection("img").document(mAuth.getCurrentUser().getEmail());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               DocumentSnapshot document = task.getResult();
               if(document.exists())
               Picasso.get().load(document.get("urlImage").toString()).resize(100,100).centerCrop().into(addImage);
           }
       });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void upDateData(){
        db.collection("text_audio").whereEqualTo("id_user", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail().toString()).orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    List<audioItem> itemList = queryDocumentSnapshots.toObjects(audioItem.class);
                    adapter.setAudio(itemList);
                    recyclerViewAudio.scrollToPosition(adapter.getItemCount()-1);

                }
            }
        });
    }

    private void uploadAudio(String audioFilePath, final String textAudio){
        if(audioFilePath != null){
            Uri uri = Uri.fromFile(new File(audioFilePath).getAbsoluteFile());
            assert uri != null;
            final StorageReference referenceToAudio =storageRef.child("audio/"+uri.getPathSegments());
            referenceToAudio.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task){
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_LONG).show();
                    }

                    // Continue with the task to get the download URL
                    return referenceToAudio.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();

                        db.collection("text_audio").add(new audioItem(textAudio, System.currentTimeMillis(), mAuth.getCurrentUser().getEmail().toString(), downloadUri.toString()));
                        upDateData();

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }

    public void showDialog(){

        dialogNameAudio.show(getSupportFragmentManager(), "audioDialog");
        //textPromt = userInput.getText().toString().trim();
    }

    public void onResume_(){
        countDownTimer = new CountDownTimer(loadData(), 1000) {

            public void onTick(long millisUntilFinished) {
                saveDataOnLimit(millisUntilFinished);
                System.out.println(loadData());
            }

            public void onFinish() {
                saveDataOnLimit(0);
                showDialog();
                onRecord(false);
            }

        }.start();
    }
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {

        if(mRecorder == null){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            Random random = new Random();
            String recordname = String.valueOf(random.nextInt());
            outputFile = getExternalCacheDir().getAbsolutePath()+"/record"+recordname+".3gp";

            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setOutputFile(outputFile);
            paths.add(outputFile);
        }
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            System.out.println("fail prepare");
        }
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
        } catch(RuntimeException stopException) {
            stopException.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void addText(String text) {

        String temp = text;
        if(temp.equals("")){
            temp = "someText";
        }

        uploadAudio(outputFile,temp);

        li = LayoutInflater.from(getApplicationContext());

    }

    public void exitAccount(View view) {
        mAuth.signOut();
        regester();
    }

    private void regester(){
        Intent intent = new Intent(this,RegesteryActivity.class);
        startActivity(intent);
    }

    public void onClickToLent(View view) {
        Intent intent = new Intent(this,LentActivity.class);
        startActivity(intent);
    }
}
