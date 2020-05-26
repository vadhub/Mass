package com.example.testbutton;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LentAdapter extends RecyclerView.Adapter<LentAdapter.LentViewHolder>{

    private List<Page> audios;

    private final int AUTHOR = 0;
    private final int AUDIO = 1;
    private FirebaseFirestore db;
    private MediaPlayer player;
    private DocumentReference documentReference;

    public LentAdapter() {

        audios = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

    }

    private void onPlay(boolean start, String index) {
        if (start) {
            startPlaying(index);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String index) {
        player = new MediaPlayer();
        try {
            player.setDataSource(index);

            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        player.pause();
        player = null;
    }

    private void releaseMP() {
        if (player != null) {
            try {
                player.release();
                player = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Page> getAudios() {
        return audios;
    }

    public void setAudios(List<Page> audios) {
        this.audios =audios;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == AUDIO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_audio, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_lent, parent, false);
        }
        return new LentViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (audios.get(position).getType() == 0) {
            return AUTHOR;
        } else {
            return AUDIO;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final LentViewHolder holder, final int position) {

        int type = getItemViewType(position);

        switch (type) {
            case AUTHOR:
                documentReference = db.collection("avas").document(audios.get(position).getAuthor());

                if(documentReference != null) {
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                                Picasso.get().load(document.get("urlImage").toString()).resize(100, 100).centerCrop().into(holder.imageView);
                        }
                    });
                }
                documentReference = db.collection("img").document(audios.get(position).getAuthor());

                System.out.println(position + " ------"+ documentReference.getId());

                if(documentReference != null){
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists())
                            Picasso.get().load(document.get("urlImage").toString()).resize(800,800).centerCrop().into(holder.imagePhoto);
                    }
                });
                }
                holder.textView_authorName.setText(audios.get(position).getAuthor());

                break;
            case AUDIO:
                releaseMP();
                final boolean[] t = {true};

                holder.textView_audio.setText(audios.get(position).getText());

                player = new MediaPlayer();
                try {
                    player.setDataSource(audios.get(position).getUriAudio());

                    player.prepare();
                    int duration = player.getDuration();
                    String time = String.format("%02d :%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    );
                    holder.timeA.setText(time);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.release();

                holder.imageAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(t[0]){
                            holder.imageAudio.setImageResource(R.drawable.unnamed);
                            onPlay(t[0], audios.get(position).getUriAudio());
                            t[0] = false;

                        }else{
                            holder.imageAudio.setImageResource(R.drawable.pl);
                            onPlay(t[0], audios.get(position).getUriAudio());
                            onPlay(t[0], audios.get(position).getUriAudio());
                            t[0] = true;


                        }
                    }
                });
                break;
        }
        documentReference = null;

    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    class LentViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_authorName;
        private TextView textView_audio;
        private ImageView imageView;
        private ImageView imagePhoto;
        private ImageView imageAudio;
        private TextView timeA;

        @SuppressLint("InflateParams")
        public LentViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView_audio = itemView.findViewById(R.id.text_audio);
            textView_authorName = itemView.findViewById(R.id.nameUser);
            imageAudio = itemView.findViewById(R.id.imagePlay);
            timeA = itemView.findViewById(R.id.timeAudio);
            imagePhoto = itemView.findViewById(R.id.imageForPage);


        }
    }
}
