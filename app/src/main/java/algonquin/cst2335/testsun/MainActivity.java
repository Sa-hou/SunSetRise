package algonquin.cst2335.testsun;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button lookupButton;
    private TextView dateTextView;
    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private TextView cityNameTextView; // Class member variable for the city name TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.dateTextView);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        lookupButton = findViewById(R.id.lookupButton);
        sunriseTextView = findViewById(R.id.sunriseTimeTextView);
        sunsetTextView = findViewById(R.id.sunsetTimeTextView);
        cityNameTextView = findViewById(R.id.cityNameTextView); // Initialize here

        setCurrentDate(); // Set the current date

        lookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = latitudeEditText.getText().toString();
                String longitude = longitudeEditText.getText().toString();

                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    try {
                        double lat = Double.parseDouble(latitude);
                        double lng = Double.parseDouble(longitude);
                        performLookup(lat, lng); // Lookup sunrise and sunset times
                        getCityName(lat, lng); // Lookup city name
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Please enter valid latitude and longitude values", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Latitude and longitude cannot be empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setCurrentDate() {
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
        dateTextView.setText(currentDate);
    }

    private void getCityName(double latitude, double longitude) {
        String apiKey = "pk.f452ae0b3686ec8c6a3e78209466d1d7"; // Replace with your actual API key
        String url = "https://us1.locationiq.com/v1/reverse.php?key=" + apiKey + "&lat=" + latitude + "&lon=" + longitude + "&format=json";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject address = response.getJSONObject("address");
                        String city = address.optString("city", "Not found");
                        cityNameTextView.setText(city);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error parsing JSON for city name", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error fetching city name: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
        queue.add(jsonObjectRequest);
    }

    private void performLookup(double latitude, double longitude) {
        String url = "https://api.sunrisesunset.io/json?lat=" + latitude + "&lng=" + longitude + "&date=today";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject results = response.getJSONObject("results");
                        String sunrise = results.getString("sunrise");
                        String sunset = results.getString("sunset");

                        sunriseTextView.setText(getString(R.string.sunrise_time, sunrise));
                        sunsetTextView.setText(getString(R.string.sunset_time, sunset));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonObjectRequest);
    }
}
