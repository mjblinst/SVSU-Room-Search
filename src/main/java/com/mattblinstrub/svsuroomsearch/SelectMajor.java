package com.mattblinstrub.svsuroomsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectMajor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_major);
        initializeVariables();

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
    }

    private void initializeVariables() {
        spinner = (Spinner) findViewById(R.id.spinner_majors);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.majors_array, android.R.layout.simple_spinner_item);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String major = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /** Called when the user taps the Search Rooms button */
    public void searchRooms(View view) {
        Intent intent = new Intent(this, SearchRooms.class);
        intent.putExtra("selectedMajor",String.valueOf(spinner.getSelectedItem()));
        startActivity(intent);
    }
}
