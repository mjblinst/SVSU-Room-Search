package com.mattblinstrub.svsuroomsearch;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchRooms extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String selectedMajor;
    int filename;
    String jsonCoursesData;
    String filteredJsonCoursesData;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    List<String> roomNumbersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_rooms);

        selectedMajor = getSelectedMajor();
        setSelectedMajorText(selectedMajor);
        filename = getFilename(selectedMajor);

        try {
            long time1 = System.nanoTime();

            jsonCoursesData = getJsonCoursesDataFromFile(filename);
            filteredJsonCoursesData = getFilteredJsonCoursesData(jsonCoursesData, selectedMajor);

            if (selectedMajor.equalsIgnoreCase("all")) {
                filteredJsonCoursesData = jsonCoursesData;
            }

            roomNumbersList = getRoomNumbers(filteredJsonCoursesData);
            initializeVariables(roomNumbersList);

            long time2 = System.nanoTime();
            System.out.println("Time: " + (time2 - time1));
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public String getSelectedMajor() {
        Bundle bundle = getIntent().getExtras();
        selectedMajor = bundle.get("selectedMajor").toString();
        return selectedMajor;
    }

    public void setSelectedMajorText(String selectedMajor) {
        TextView textView = (TextView) findViewById(R.id.txt_major);
        String text = "Major: " + selectedMajor;
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setText(text);
    }

    public int getFilename(String selectedMajor) {
        int filename = 0;

        if (selectedMajor.equalsIgnoreCase("all")) {
            filename = R.raw.all_courses;
        } else if (selectedMajor.equalsIgnoreCase("cis")) {
            filename = R.raw.cis_courses;
        } else if (selectedMajor.equalsIgnoreCase("cs")) {
            filename = R.raw.cs_courses;
        } else if (selectedMajor.equalsIgnoreCase("csis")) {
            filename = R.raw.csis_courses;
        }

        return filename;
    }

    /** Read in file with JSON object, convert to String */
    public String getJsonCoursesDataFromFile(int filename) throws IOException {
        InputStream inputStream = getResources().openRawResource(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String jsonCoursesData = "";

        // Read data from file line by line, append to String
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = reader.readLine();
            }
            jsonCoursesData = stringBuilder.toString();
        } finally {
            reader.close();
        }

        return jsonCoursesData;
    }

    public String getFilteredJsonCoursesData(String jsonCoursesData, String selectedMajor) throws JSONException {
        filteredJsonCoursesData = "{\"courses\": " + getDataFromMajor(jsonCoursesData, selectedMajor) + "}";
        return filteredJsonCoursesData;
    }

    /** Get JSON courses data based on given major */
    public String getDataFromMajor(String jsonCoursesData, String prefix) throws JSONException {
        JSONObject allCourseData = new JSONObject(jsonCoursesData);
        JSONArray allCourses = allCourseData.getJSONArray("courses");
        JSONArray dataFromMajor = new JSONArray();

        for (int i = 0; i < allCourses.length(); i++) {
            JSONObject individualCourseData = allCourses.getJSONObject(i);

            if (individualCourseData.get("prefix").equals(prefix.toUpperCase())) {
                dataFromMajor.put(individualCourseData);
            }
        }

        return dataFromMajor.toString();
    }

    /** Get list of room numbers matching given input */
    public List<String> getRoomNumbers(String jsonCoursesData) throws JSONException {
        JSONObject allCourseData = new JSONObject(jsonCoursesData);
        List<String> list = new ArrayList<>();
        JSONArray array = allCourseData.getJSONArray("courses");

        for (int i = 0; i < array.length(); i++) {
            JSONObject individualCourseData = array.getJSONObject(i);
            JSONArray roomNumbers = individualCourseData.getJSONArray("meetingTimes");

            if (individualCourseData.get("location").equals("UC")) {
                if (roomNumbers.getJSONObject(0).has("building")) {
                    if (!list.contains(roomNumbers.getJSONObject(0).getString("building")
                            + roomNumbers.getJSONObject(0).getString("room"))) {
                        list.add(roomNumbers.getJSONObject(0).getString("building")
                                + roomNumbers.getJSONObject(0).getString("room"));
                    }
                } else {
                    continue;
                }
            } else if (individualCourseData.get("location").equals("ONL")) {
                continue;
            }
        }

        return list;
    }

    private void initializeVariables(List<String> roomNumbersList) {
        spinner = (Spinner) findViewById(R.id.spinner_rooms);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomNumbersList);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String room = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    /** Called when the user taps the Display Times button */
    public void displayTimes(View view) {
        Intent intent = new Intent(this, DisplayTimes.class);
        intent.putExtra("selectedMajor", selectedMajor);
        intent.putExtra("selectedRoom",String.valueOf(spinner.getSelectedItem()));
        startActivity(intent);
    }
}