package com.mattblinstrub.svsuroomsearch;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DisplayTimes extends AppCompatActivity {

    String selectedMajor;
    int filename;
    String selectedRoom;
    String jsonCoursesData;
    String filteredJsonCoursesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_times);

        selectedMajor = getSelectedMajor();
        filename = getFilename(selectedMajor);

        selectedRoom = getSelectedRoom();
        setSelectedRoomText(selectedRoom);

        try {
            jsonCoursesData = getJsonCoursesDataFromFile(filename);
            filteredJsonCoursesData = getFilteredJsonCoursesData(jsonCoursesData, selectedRoom);
            List<String> timesList = getTimes(filteredJsonCoursesData);
            setTimesText(timesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSelectedMajor() {
        Bundle bundle = getIntent().getExtras();
        selectedMajor = bundle.get("selectedMajor").toString();
        return selectedMajor;
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

    /** Get selected room from Search Rooms activity */
    public String getSelectedRoom() {
        Bundle bundle = getIntent().getExtras();
        selectedRoom = bundle.get("selectedRoom").toString();
        return selectedRoom;
    }

    public void setSelectedRoomText(String selectedRoom) {
        TextView textView = (TextView) findViewById(R.id.txt_selected_room);
        String text = "Room: " + selectedRoom;
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setText(text);
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

    public String getFilteredJsonCoursesData(String jsonCoursesData, String selectedRoom) throws JSONException {
        String building = getBuilding(selectedRoom);
        String room = getRoom(selectedRoom);

        filteredJsonCoursesData = "{\"courses\": "
                + getCoursesFromRoomNumber(jsonCoursesData, building, room) + "}";

        return filteredJsonCoursesData;
    }

    /** Get the SVSU building abbreviation of the given room number */
    public String getBuilding(String roomNumber) {
        String building = "";

        if (roomNumber.equalsIgnoreCase("ONL")) {
            building = "ONL";
        } else {
            String[] part = roomNumber.split("(?<=\\D)(?=\\d)");
            building = part[0];
        }

        return building;
    }

    /** Get the SVSU building number of the given room number */
    public String getRoom(String roomNumber) {
        String room = "";

        if (roomNumber.equalsIgnoreCase("ONL")) {
            room = "ONL";
        } else {
            String[] part = roomNumber.split("(?<=\\D)(?=\\d)");
            room = part[1];
        }

        return room;
    }

    /** Get JSON courses data based on given room number */
    public String getCoursesFromRoomNumber(String jsonCoursesData, String building, String room) throws JSONException {
        JSONObject allCourseData = new JSONObject(jsonCoursesData);
        JSONArray allCourses = allCourseData.getJSONArray("courses");
        JSONArray coursesFromRoomNumber = new JSONArray();

        for (int i = 0; i < allCourses.length(); i++) {
            JSONObject individualCourseData = allCourses.getJSONObject(i);
            JSONArray roomNumbers = individualCourseData.getJSONArray("meetingTimes");
            JSONObject roomNumber = roomNumbers.getJSONObject(0);
            if (roomNumber.has("building")) {
                if (roomNumber.get("building").equals(building.toUpperCase())
                        && roomNumber.get("room").equals(room)) {
                    coursesFromRoomNumber.put(individualCourseData);
                }
            } else if (roomNumber.get("method").equals(building.toUpperCase())) {
                coursesFromRoomNumber.put(individualCourseData);
            }
        }

        return coursesFromRoomNumber.toString();
    }

    /** Get list of times matching given input */
    public List<String> getTimes(String jsonCoursesData) throws JSONException {
        JSONObject allCourseData = new JSONObject(jsonCoursesData);
        List<String> list = new ArrayList<>();
        JSONArray array = allCourseData.getJSONArray("courses");

        for (int i = 0; i < array.length(); i++) {
            JSONObject individualCourseData = array.getJSONObject(i);
            JSONArray times = individualCourseData.getJSONArray("meetingTimes");

            if (individualCourseData.get("location").equals("UC")) {
                list.add(times.getJSONObject(0).getString("days") + " "
                        + times.getJSONObject(0).getString("startTime") + " - "
                        + times.getJSONObject(0).getString("endTime"));
            } else if (individualCourseData.get("location").equals("ONL")) {
                list.add("ONL");
            }
        }

        return list;
    }

    public void setTimesText(List<String> timesList) {
        TextView textView = (TextView) findViewById(R.id.txt_times);

        for (int i = 0; i < timesList.size(); i++) {
            textView.append(timesList.get(i) + " \n");
        }
    }
}
