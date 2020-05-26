package com.example.testbutton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LentActivity extends AppCompatActivity {

    private RecyclerView itemLent;
    private FirebaseFirestore db;
    private LentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lent);

        itemLent = (RecyclerView) findViewById(R.id.recycleLenta);
        db = FirebaseFirestore.getInstance();

        adapter = new LentAdapter();
        updateLent();

        itemLent.setLayoutManager(new LinearLayoutManager(this));
        itemLent.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void updateLent(){
        db.collection("text_audio").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots != null){
                    List<audioItem>  messeg = queryDocumentSnapshots.toObjects(audioItem.class);
                    List<Page> p= new ArrayList<Page>();
                    String a = "";
                    for(audioItem m: messeg){

                        if(!a.equals(m.getId_user())){
                            a=m.getId_user();
                            p.add(new Page(0, m.getId_user(), m.getId_user()));
                        }
                        p.add(new Page(1, m.getText_Audio(), m.getId_user(), m.getUriAudio()));
                    }

                    adapter.setAudios(p);

                    itemLent.scrollToPosition(adapter.getItemCount()-1);
                }else{
                    Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        });
    }



}
