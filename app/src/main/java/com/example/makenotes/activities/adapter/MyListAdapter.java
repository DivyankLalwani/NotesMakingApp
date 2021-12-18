package com.example.makenotes.activities.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makenotes.R;
import com.example.makenotes.activities.entity.note;
import com.example.makenotes.activities.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

      Context context;
      private ArrayList<note> notes;
      private NotesListener notesListener;

    public MyListAdapter(ArrayList<note> notes,NotesListener notesListener) {
        this.notes = notes;
        this.notesListener=notesListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View list_items = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(list_items);

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyListAdapter.ViewHolder holder, final int position) {
        holder.setNote(notes.get(position));
        holder.layoutNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClicked(notes.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (notes.size());
    }
   // @SuppressWarnings("deprecated")
    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView Listtitle, Listsubtitle, ListDateTime;
         LinearLayout layoutNotes;
        //LinearLayout linearLayout;
         RoundedImageView imageNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Listtitle = itemView.findViewById(R.id.titleInList);
            ListDateTime = itemView.findViewById(R.id.datetimeInList);
            Listsubtitle = itemView.findViewById(R.id.subtitleInList);
            layoutNotes = itemView.findViewById(R.id.layoutNote);
            imageNote=itemView.findViewById(R.id.imageMainActivity);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void setNote(note Note) {
            Listtitle.setText(Note.getTitle());
            Listsubtitle.setText(Note.getSubtitle());
            ListDateTime.setText(Note.getDate_time());
            Note.setColor("#33333");
            Log.i("problem is here", "79");
          //GradientDrawable gradientDrawable=(GradientDrawable) layoutNotes.getBackground();

          //if(gradientDrawable!=null){
             // gradientDrawable.setColor(Color.parseColor(Note.getColor()));
          //}

           // Note.setColor("#FDFDFD");
            Log.i("problem is here", "82");


            if(Note.getImagePath()!=null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(Note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }
            else{
                imageNote.setVisibility(View.GONE);
            }






               //   gradientDrawable.setColor(Color.parseColor(Note.getColor()));
            //              Log.i("problem is here","83");
        }
    }
}