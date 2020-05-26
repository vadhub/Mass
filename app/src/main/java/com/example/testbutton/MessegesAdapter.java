package com.example.testbutton;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessegesAdapter extends RecyclerView.Adapter<MessegesAdapter.MessegesViewHolder> {

    private List<audioItem> audio;

    private  MediaPlayer player;
    public MessegesAdapter(){
        audio = new ArrayList<>();

        player = new MediaPlayer();
    }

    @NonNull
    @Override
    public MessegesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_audio, parent, false);
        return new MessegesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessegesViewHolder holder, final int position) {
        final boolean[] t = {true};

        holder.textView_audio.setText(audio.get(position).getText_Audio());

        player = new MediaPlayer();
        try {
            player.setDataSource(audio.get(position).getUriAudio());

            player.prepare();
            int duration = player.getDuration();
            System.out.println(duration);
            String time = String.format("%02d :%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            );
            holder.times.setText(time);

        } catch (IOException e) {
            e.printStackTrace();
        }



        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(t[0]){
                    holder.imageView.setImageResource(R.drawable.unnamed);

                    player = new MediaPlayer();
                    if(!player.isPlaying()){
                        try {
                            player.setDataSource(audio.get(position).getUriAudio());

                            player.prepare();
                            player.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        t[0] = false;
                        }

                }else{
                    if(player!=null){
                        holder.imageView.setImageResource(R.drawable.pl);
                        player.release();
                        player = null;
                        t[0] = true;
                    }

                }
                if(player!= null)
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        holder.imageView.setImageResource(R.drawable.pl);
                        player.release();
                        player = null;
                        t[0] = true;
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return audio.size();
    }

    public List<audioItem> getAudio() {
        return audio;
    }

    public void setAudio(List<audioItem> audio) {
        this.audio = audio;
        notifyDataSetChanged();
    }

    class MessegesViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_audio;
        private ImageView imageView;
        private TextView times;

        public MessegesViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_audio = itemView.findViewById(R.id.text_audio);
            imageView = itemView.findViewById(R.id.imagePlay);
            times = itemView.findViewById(R.id.timeAudio);

        }
    }
}
