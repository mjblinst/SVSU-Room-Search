package com.mattblinstrub.svsuroomsearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Select Major button */
    public void selectMajor(View view) {
        Intent selectMajor = new Intent(this, SelectMajor.class);
        startActivity(selectMajor);
    }

    /** Called when the user taps the Search Rooms button */
    public void searchRooms(View view) {
        Intent searchRooms = new Intent(this, SearchRooms.class);
        searchRooms.putExtra("selectedMajor","All");
        startActivity(searchRooms);
    }
}
