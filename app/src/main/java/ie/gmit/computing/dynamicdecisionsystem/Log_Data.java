package ie.gmit.computing.dynamicdecisionsystem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ie.gmit.computing.dynamicdecisionsystem.Model.Node;


public class Log_Data extends ActionBarActivity {

    String dateTime;
    TextView dateText;
    TextView gpsText;
    TextView nameText;
    TextView imageText;

    EditText notesText, colourText, volumeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log__data);

        Intent intent = getIntent();
        String parentName = ((Node)intent.getSerializableExtra(Decision_Menu.PARENT)).getName().toString();
        String photoName = (String)intent.getSerializableExtra(Decision_Menu.PHOTO);
        nameText = (TextView) findViewById(R.id.nameTextView);
        nameText.setText("Log: " + parentName);

        dateText = (TextView) findViewById(R.id.dateTextView);
        dateText.setText(getDateCurrent());

        gpsText = (TextView) findViewById(R.id.gpsTextView);
        gpsText.setText(getGPSLocation());

        imageText = (TextView) findViewById(R.id.imageTextView);
        imageText.setText("PhotoName: " + photoName);

        notesText = (EditText) findViewById(R.id.editTextNotes);
        colourText = (EditText) findViewById(R.id.editTextColour);
        volumeText = (EditText) findViewById(R.id.editTextVolume);
    }

    public void homeClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public String  getGPSLocation(){
        try{
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            String currentLocation = "Longitude: " + longitude + ", \nLatitude: " + latitude ;
            return currentLocation;
        }catch (Exception e) {
        }
        return "";
    }

    public  String getDateCurrent() {
        try{
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dateTime= dateFormat.format(date);
            return dateTime;
        }catch (Exception e) {
        }
        return "";
    }

    public void saveLogClick(View view){
        String FILENAME = Environment.getExternalStorageDirectory() + File.separator + "marine_log.csv";
        String entry = nameText.getText().toString() + "," + dateText.getText().toString() + "," + gpsText.getText().toString() + ","
                + imageText.getText().toString() + "," + notesText.getText().toString() + ","
                + colourText.getText().toString() + "," + volumeText.getText().toString() + "\n";
        try{
            FileOutputStream out = openFileOutput(FILENAME, Context.MODE_APPEND);
            out.write(entry.getBytes());
            out.close();
            Toast.makeText(getApplicationContext(), "CSV File Saved", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            e.printStackTrace();
        }

        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        // sendIntent.setType("text/html");
        sendIntent.setType("application/csv");
        sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
        String temp_path = FILENAME + "/" + "Filename.csv";
        File F = new File(temp_path);
        Uri U = Uri.fromFile(F);
        sendIntent.putExtra(Intent.EXTRA_STREAM, U);
        startActivity(Intent.createChooser(sendIntent, "Send Mail"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log__data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
