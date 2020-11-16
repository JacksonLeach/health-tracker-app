package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.healthapp.data.CategoryEntry;
import com.example.healthapp.data.Entry;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewEntry extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    TextInputEditText dateDisplay;
    TextInputLayout dateDisplayLayout;
    DatePickerFragment datePickerFragment;

    EntryFormAdapter adapter;
    RecyclerView recyclerView;

    Entry newEntry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        newEntry = new Entry();

        ArrayList<CategoryEntry> userCategories = new ArrayList<>();
        adapter = new EntryFormAdapter(userCategories, R.layout.category_input_section, newEntry);

        recyclerView = findViewById(R.id.newEntryRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        //Need to get the categories the user has set from the server
        getUserCategories(userCategories);



        dateDisplay = findViewById(R.id.dateEntryField);
        dateDisplayLayout = findViewById(R.id.dateEntryLayout);
        datePickerFragment = new DatePickerFragment(dateDisplay, newEntry);
        dateDisplay.setOnClickListener( v -> {
            datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        });

        findViewById(R.id.submitEntry).setOnClickListener( v -> {
            saveNewEntry();
        });



    }

    /**
     * Gets the categories the user has set from firebase
     *
     * These categories are then sent to the view adapter so the correct input fields can
     * be created.
     * @param categories
     */
    private void getUserCategories(List<CategoryEntry> categories) {
        CollectionReference usersRef = db.collection("users");

        String currentUserId = currentUser.getUid();


        usersRef.whereEqualTo("userId", currentUserId).get()
                .addOnCompleteListener( task -> {
                   if (task.isSuccessful()) {
                       List<DocumentSnapshot> documents = task.getResult().getDocuments();
                       if (documents.size() == 1) {
                           List<String> newCategories = (List<String>) documents.get(0).get("categories");

                           for ( String category : newCategories) {
                               //Create a new category entry and add it to both the entry object
                               //and adapter
                               CategoryEntry newCategoryEntry = new CategoryEntry(category);
                               newEntry.addCategoryEntry(newCategoryEntry);
                               adapter.addEntry(newCategoryEntry);
                           }
                           adapter.notifyDataSetChanged();
                           Log.d("firestore", categories.toString());
                       } else {
                           Log.e("firestore", "data too long");
                       }
                   } else {
                       Log.e("firestore", "failed to get user categories");
                   }
                });
    }

    /**
     * Sends new entry data to firestore
     */
    private void saveNewEntry() {
        CollectionReference entriesRef = db.collection("entries");
        String currentUserId = currentUser.getUid();

        newEntry.setUserId(currentUserId);

        entriesRef.add(newEntry)
                .addOnSuccessListener(documentReference -> {
                    Log.d("firestore", "document save successfully");
                })
                .addOnFailureListener( e -> {
                    Log.e("firestore", "error adding document", e);
                });
    }

    /**
     * Adapter that holds all of the user's category input sections
     */
    public class EntryFormAdapter extends RecyclerView.Adapter<EntryFormAdapter.EntryFormViewHolder> {

        List<CategoryEntry> categoryTitles;
        int layoutResourceId;
        Entry newEntry;

        public EntryFormAdapter(List<CategoryEntry> titles, int layoutResourceId, Entry newEntry) {
            this.categoryTitles = new ArrayList<>();
            this.layoutResourceId = layoutResourceId;
            this.newEntry = newEntry;
        }

        public void addEntry(CategoryEntry newEntry) {
            categoryTitles.add(newEntry);
        }

        @NonNull
        @Override
        public EntryFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
            return new EntryFormViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull EntryFormViewHolder holder, int position) {
            holder.setCategory(this.categoryTitles.get(position));
        }

        @Override
        public int getItemCount() {
            return categoryTitles.size();
        }


        /**
         * View holder for a category input section
         */
        public class EntryFormViewHolder extends RecyclerView.ViewHolder {

            private final TextView categoryTitleDisplay;

            private final RadioGroup moodSelector;

            private final TextInputEditText categoryTextInput;


            /**
             * Creates references to all necessary xml elements
             * @param itemView Passed from recycler view
             */
            public EntryFormViewHolder(@NonNull View itemView) {
                super(itemView);
                categoryTitleDisplay = itemView.findViewById(R.id.categoryHeader);
                moodSelector = itemView.findViewById(R.id.moodSelector);
                categoryTextInput = itemView.findViewById(R.id.categoryInput);
            }

            /**
             * Sets up text displays and listeners
             * @param category CategoryEntry for this section
             */
            public void setCategory(CategoryEntry category) {

                moodSelector.setOnCheckedChangeListener((group, checkedId) -> category.setMood((checkedId - 1) % 3));

                categoryTextInput.addTextChangedListener(new CategoryTextWatcher(category));


                categoryTitleDisplay.setText(getString(R.string.category_section_header, category.getCategoryTitle()));
            }
        }

        /**
         * Handles text changes in category text input sections
         * Needs a category entry object to update
         */
        public class CategoryTextWatcher implements TextWatcher {
            private final CategoryEntry entry;

            public CategoryTextWatcher(CategoryEntry entry) {
                this.entry = entry;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                entry.setEntryText(s.toString());
            }
        }
    }

    /**
     * Handles creating, showing, and getting the user input for the date picker
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private final TextInputEditText dateDisplay;
        private LocalDate dateValue;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        private final Entry newEntryRef;

        public DatePickerFragment(TextInputEditText dateDisplay, Entry newEntryRef) {
            this.dateDisplay = dateDisplay;
            this.newEntryRef = newEntryRef;
        }

        /**
         * Creates and shows date picker
         * @param savedInstanceState instance state
         * @return Date dialog
         */
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final LocalDate currentDate = LocalDate.now();
            int year, month, day;
            //The date will default to the current day if the
            //user has not set a date yet
            if (dateValue == null) {
                year = currentDate.getYear();
                //LocalDate objects use 1 for Jan and date picker uses 0 for Jan
                //so it needs to be converted
                month = currentDate.getMonthValue() - 1;
                day = currentDate.getDayOfMonth();
            } else {
                year = dateValue.getYear();
                //LocalDate objects use 1 for Jan and date picker uses 0 for Jan
                //so it needs to be converted
                month = dateValue.getMonthValue() - 1;
                day = dateValue.getDayOfMonth();
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * Gets the user input from the date picker
         * and sets the new entry object accordingly
         * @param view date picker view
         * @param year user selected year
         * @param month user selected month
         * @param dayOfMonth user selected day
         */

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //Convert back to LocalDate changing the month to match
            dateValue = LocalDate.of(year, month + 1, dayOfMonth);
            updateDisplay();
            newEntryRef.setEntryDate(Date.from(dateValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        /**
         * Updates the text view to display the currently selected date
         */
        public void updateDisplay() {
            dateDisplay.setText(formatter.format(dateValue));
        }

        public LocalDate getDateValue() {
            return this.dateValue;
        }
    }
}