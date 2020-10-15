package com.example.healthapp;

import android.app.Application;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.healthapp.data.CategoryEntry;
import com.example.healthapp.data.Entry;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryListActivity extends AppCompatActivity {

    private final int RC_LOGIN = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    private EntryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        checkLogin();
        setContentView(R.layout.activity_entry_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        RecyclerView entryListView = findViewById(R.id.entryListRecyclerView);
        entryListView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Entry> entries = new ArrayList<>();

        adapter = new EntryListAdapter(entries, R.layout.day_view_layout);

        entryListView.setAdapter(adapter);

        getData(entries);

    }

    private void checkLogin() {
        if (mAuth.getCurrentUser() == null) {
            loginActivity();
        } else {
            currentUser = mAuth.getCurrentUser();
        }
    }

    private void loginActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_LOGIN
                );

    }

    private void addData(Entry entry) {
        CollectionReference entriesRef = db.collection("entries");
        String currentUserId = currentUser.getUid();
        entry.setUserId(currentUserId);

        entriesRef.add(entry)
                .addOnSuccessListener(documentReference -> {
                    Log.d("firestore", "Document written with id: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("firestore", "Error adding document", e);
                });

    }

    private void getData(List<Entry> entries) {
        CollectionReference entriesRef = db.collection("entries");
        String currentUserId = currentUser.getUid();
        Log.d("firestore", currentUserId);
        entriesRef
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("firestore", "task successful");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            entries.add(document.toObject(Entry.class));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("firestore", "error getting data");
                        Log.e("firestore", "error: ", task.getException());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                this.finish();
                System.exit(0);
            }
        }
    }

    public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.MyViewHolder> {

        private final List<Entry> _entries;
        private final int _layout;

        public EntryListAdapter(List<Entry> entries, int layout) {
            _entries = entries;
            _layout = layout;
        }
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(_layout, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.setEntry(_entries.get(position));
        }

        @Override
        public int getItemCount() {
            return _entries.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView category1;
            TextView category2;
            TextView category3;

            ImageView category1Image;
            ImageView category2Image;
            ImageView category3Image;

            TextView monthDisplay;
            TextView dayDisplay;


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                category1 = itemView.findViewById(R.id.first_category_name);
                category1Image = itemView.findViewById(R.id.first_category_response);

                category2 = itemView.findViewById(R.id.second_category_name);
                category2Image = itemView.findViewById(R.id.second_category_response);

                category3 = itemView.findViewById(R.id.third_category_name);
                category3Image = itemView.findViewById(R.id.third_category_response);

                monthDisplay = itemView.findViewById(R.id.monthDisplay);
                dayDisplay = itemView.findViewById(R.id.dayDisplay);
            }

            public Drawable getImageFromInt(int index) {
                switch (index) {
                    case 0:
                        return getDrawable(R.drawable.negative_response);
                    case 1:
                        return getDrawable(R.drawable.neutral_response);
                    case 2:
                        return getDrawable(R.drawable.positive_response);
                }
                return null;
            }

            public void setEntry(Entry entry) {

                Log.d("entrydisplay", entry.getMood() + "");
                SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM", Locale.US);
                SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.US);

                monthDisplay.setText(monthFormatter.format(entry.getEntryDate()).toUpperCase());
                dayDisplay.setText(dayFormatter.format(entry.getEntryDate()));

                List<CategoryEntry> categoryEntries = entry.getCategories();
                category1.setText(categoryEntries.get(0).getCategoryTitle());
                category1Image.setImageDrawable(getImageFromInt(categoryEntries.get(0).getMood()));

                category2.setText(categoryEntries.get(1).getCategoryTitle());
                category2Image.setImageDrawable(getImageFromInt(categoryEntries.get(1).getMood()));

                category3.setText(categoryEntries.get(2).getCategoryTitle());
                category3Image.setImageDrawable(getImageFromInt(categoryEntries.get(2).getMood()));
            }
        }
    }
}