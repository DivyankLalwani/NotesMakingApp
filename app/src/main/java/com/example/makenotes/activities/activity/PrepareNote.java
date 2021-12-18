package com.example.makenotes.activities.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.makenotes.R;
import com.example.makenotes.activities.database.Database;
import com.example.makenotes.activities.entity.note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PrepareNote extends AppCompatActivity {
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    public String selectNoteColor;
    ImageView saveN;
    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteDescription;
    private TextView textDateTime;
    private note alreadyAvailable;
    private ImageView imageN;
    private String selectedImagePath;
    private String webUrl;
    private LinearLayout layoutWebUrl;
    private TextView textWebUrl;
    protected AlertDialog dialogAddUrl;
    private AlertDialog dialogDeleteNote;
    private ConstraintLayout constraintLayout;
    public EditText inputUrl;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_note);

        ImageView image = findViewById(R.id.arrowback);
        image.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         onBackPressed();
                                     }
                                 }
        );
        imagePath="";
        selectNoteColor = "#333333";
        inputNoteSubtitle = findViewById(R.id.subtitle);
        inputNoteTitle = findViewById(R.id.title);
        saveN = findViewById(R.id.save);
        inputNoteDescription = findViewById(R.id.description);
        textDateTime = findViewById(R.id.datetime);
        imageN = findViewById(R.id.imageNote);
        textWebUrl = findViewById(R.id.textWebUrl);
        layoutWebUrl = findViewById(R.id.layoutWebUrl);
        constraintLayout=findViewById(R.id.layoutAddUrlContainer);
        inputUrl=findViewById(R.id.inputURL);
        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailable = (note) getIntent().getSerializableExtra("Note");
            setViewOrUpdateNote();
        }
        initMisceelleanous();
        saveN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailable.getTitle());
        inputNoteSubtitle.setText(alreadyAvailable.getSubtitle());
        inputNoteDescription.setText(alreadyAvailable.getNotetext());
        textDateTime.setText(alreadyAvailable.getDate_time());

        if(alreadyAvailable.getImagePath()!=null && alreadyAvailable.getImagePath().trim().isEmpty()==false){
            imageN.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailable.getImagePath()));
            selectedImagePath=alreadyAvailable.getImagePath();
            imageN.setVisibility(View.VISIBLE);
          //  alreadyAvailable.setImagePath(selectedImagePath);
        }
        if(alreadyAvailable.getWebLink()!=null && !alreadyAvailable.getWebLink().trim().isEmpty()){
            textWebUrl.setText(alreadyAvailable.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }
    }

    private void saveNote() {

        if (inputNoteTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Note Can't Have Empty Title", Toast.LENGTH_SHORT).show();
            return;
        }

        final note Note = new note();
        Note.setTitle(inputNoteTitle.getText().toString());
        Note.setNotetext(inputNoteDescription.getText().toString());
        Note.setSubtitle(inputNoteSubtitle.getText().toString());
        Note.setDate_time(textDateTime.getText().toString());
        Note.setColor(selectNoteColor);
        Note.setImagePath(selectedImagePath);

        if(layoutWebUrl.getVisibility()==View.VISIBLE){
            Note.setWebLink(textWebUrl.getText().toString());
        }
        if(alreadyAvailable!=null){
            Note.setId(alreadyAvailable.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            public SaveNoteTask() {
                super();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Database.getDatabase(getApplicationContext()).noteDao().insertNode(Note);
                List<note> abc = Database.getDatabase(getApplicationContext()).noteDao().getAllNotes();

                for (int i = 0; i < abc.size(); i++) {
                    Log.i("Database Check", abc.get(i).tostring());
                }
                return null;
            }
        }
        new SaveNoteTask().execute();
    }

    private void initMisceelleanous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscelleanous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscelleanous).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        } else {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    }
                }
        );
        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imagecolor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imagecolor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imagecolor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imagecolor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imagecolor5);

        layoutMiscellaneous.findViewById(R.id.viewcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#FF4842";
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNoteColor = "#3A52Fc";
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewcolor5).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                selectNoteColor = "#000000";
                imageColor5.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor1.setImageResource(0);
            }
        });

        if(alreadyAvailable!=null && alreadyAvailable.getColor()!=null && !alreadyAvailable.getColor().trim().isEmpty()){
            {
                switch (alreadyAvailable.getColor()){
                    case "FDBE3B":
                        layoutMiscellaneous.findViewById(R.id.viewcolor2);
                        break;
                    case "#FF4842":
                        layoutMiscellaneous.findViewById(R.id.viewcolor3);
                        break;
                    case "#3A52Fc":
                        layoutMiscellaneous.findViewById(R.id.viewcolor4);
                        break;
                    case "#000000":
                        layoutMiscellaneous.findViewById(R.id.viewcolor5);
                        break;
                }
            }
        }


        layoutMiscellaneous.findViewById(R.id.imageAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrepareNote.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
              //  showAddUrlDialogue();
            }
        });
        if(alreadyAvailable!=null){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                       showDeleteNoteDialog();
                }
            });
        }









    }
      private void showDeleteNoteDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(PrepareNote.this);
            View view=LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup)findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote=builder.create();
            if(dialogDeleteNote.getWindow()!=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            Database.getDatabase(getApplicationContext()).noteDao().delete(alreadyAvailable);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent=new Intent();
                            intent.putExtra("isNotDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }

                    }
                    new DeleteNoteTask().execute();
                }

            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
      }
    private void addImage() {
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageN.setImageBitmap(bitmap);
                        imageN.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUrl(selectedImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPathFromUrl(Uri contentUrl) {
        String filepath;
        Cursor cursor = getContentResolver().query(contentUrl, null, null, null);
        if (cursor == null) {
            filepath = contentUrl.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filepath = cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }

//    private void showAddUrlDialogue() {
//        // View view = null;
//        if (dialogAddUrl == null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(PrepareNote.this);
//            View view = LayoutInflater.from(this).inflate(
//                    R.layout.layout_add_url,
//                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer),
//                    false
//            );
//            builder.setView(view);
//            dialogAddUrl = builder.create();
//            if (dialogAddUrl.getWindow() != null) {
//                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//            }
//            inputUrl=findViewById(R.id.inputURL);
////                 inputUrl.setText("google.com");
//            //    final String input=inputUrl.getText().toString().trim();
//                Log.i("Error is here","344");
//                view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Log.i("Error is here","346");
//                        @NonNull
//                        final EditText inputUrl = findViewById(R.id.inputURL);
//                            inputUrl.requestFocus();
//                            Log.i("Error in this place", "348");
//
//                        if (inputUrl.getText().toString().trim().isEmpty()) {
//                            Log.i("Error in this place", "348");
//                            Toast.makeText(getApplicationContext(), "Enter Url", Toast.LENGTH_SHORT);
//                        } else {
//                            Log.i("Error in this place", "355");
//                            textWebUrl.setText(inputUrl.getText().toString().trim());
//                            Log.i("Error in this place", "356");
//                            layoutWebUrl.setVisibility(View.VISIBLE);
//                            dialogAddUrl.dismiss();
//                        }
//                    }
//
//                });
//
//
//            view.findViewById(R.id.cancelUrl).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogAddUrl.dismiss();
//                }
//            });
//
//        }
//        dialogAddUrl.show();
//
//    }
}