package com.example.healthapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.healthapp.data.CategoryEntry;
import com.example.healthapp.data.Entry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        RecyclerView entryListView = findViewById(R.id.entryListRecyclerView);
        entryListView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Entry> entries = new ArrayList<>();
        Entry newEntry = new Entry();
        CategoryEntry newCat1 = new CategoryEntry();
        newCat1.setCategoryTitle("Spiritual");
        newCat1.setEntryText("Today was a good day");
        newCat1.setMood(2);

        CategoryEntry newCat2 = new CategoryEntry();
        newCat2.setCategoryTitle("Physical");
        newCat2.setEntryText("Didn't work out at all");
        newCat2.setMood(0);

        CategoryEntry newCat3 = new CategoryEntry();
        newCat3.setCategoryTitle("Mental");
        newCat3.setEntryText("Eh");
        newCat3.setMood(1);

        newEntry.setCategories(new CategoryEntry[]{newCat1, newCat2, newCat3});
        entries.add(newEntry);

        entryListView.setAdapter(new EntryListAdapter(entries, R.layout.day_view_layout));

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

                CategoryEntry[] categoryEntries = entry.getCategories();
                category1.setText(categoryEntries[0].getCategoryTitle());
                category1Image.setImageDrawable(getImageFromInt(categoryEntries[0].getMood()));

                category2.setText(categoryEntries[1].getCategoryTitle());
                category2Image.setImageDrawable(getImageFromInt(categoryEntries[1].getMood()));

                category3.setText(categoryEntries[2].getCategoryTitle());
                category3Image.setImageDrawable(getImageFromInt(categoryEntries[2].getMood()));
            }
        }
    }
}