package ie.gmit.computing.dynamicdecisionsystem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera_Log extends ActionBarActivity {

    LinearLayout layout;
    private static String logtag = "CameraLog";
    private static int TAKE_PICTURE = 1;
    private Uri imageUri;
    ImageView imageView;
    String currTime;
    public final static String MESSAGE = "Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__log);

        Button cameraButton = (Button)findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(cameraListener);

        layout = (LinearLayout) findViewById(R.id.camera_layout);

        imageView = (ImageView)findViewById(R.id.image_camera);

        currTime = getDateCurrent();
    }

    private View.OnClickListener cameraListener = new View.OnClickListener(){
        public void onClick(View v){
         takePhoto(v);
        }
    };

    private void takePhoto(View v){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), currTime + ".jpg");
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 0);
    }

    public  String getDateCurrent() {
        try{
            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            Date date = new Date();
            String dateTime= dateFormat.format(date);
            return dateTime;
        }catch (Exception e) {
        }
        return "";
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent){
        super.onActivityResult(reqCode, resCode, intent);

        if(reqCode==0)
        {
            Bitmap theImage= (Bitmap) intent.getExtras().get("data");
            imageView.setImageBitmap(theImage);
            //Toast.makeText(Camera_Log.this, imageUri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void backButtonClick (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera__log, menu);
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

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    public void logButtonClick(View view){
        String photoName = (currTime.toString() + ".jpg");
        Intent intent = new Intent(this, Decision_Menu.class);
        intent.putExtra(MESSAGE, photoName);
        startActivity(intent);
    }

}
