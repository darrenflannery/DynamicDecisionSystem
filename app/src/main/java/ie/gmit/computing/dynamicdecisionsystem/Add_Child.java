package ie.gmit.computing.dynamicdecisionsystem;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ie.gmit.computing.dynamicdecisionsystem.Model.Node;


public class Add_Child extends ActionBarActivity {

    private Node parent;
    private Node root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__child);

        //load parent and create copy of parent
        Intent intent = getIntent();
        parent = (Node)intent.getSerializableExtra(Decision_Menu.PARENT);
        String parentName = parent.getName().toString();

        //get the root
        root = parent.getRoot();

        //set header
        TextView textView = (TextView) findViewById(R.id.addChildTextView);
        textView.append(" to " + parentName);
    }

    public void addChild(View view){

        //get name of new child
        EditText editText = (EditText) findViewById(R.id.editTextName);
        String newChildName = editText.getText().toString();

        //Create New Node and add to tree
        Node newNode = new Node(newChildName, parent, root);
        parent.addChild(newNode);

        //save to file
        try
        {
            java.io.File file = new java.io.File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/DecisionTree.ser");

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(root);
            oos.flush();
            oos.close();
            Log.v("myApp", "Save Successful");
        }
        catch(Exception ex)
        {
            Log.v("Serialization Save Error : ", ex.getMessage());
            ex.printStackTrace();
        }

        //back to main activity
        Intent intent = new Intent(this, Decision_Menu.class);
        startActivity(intent);
    }

    public void cancelClick(View view){
        Intent intent = new Intent(this, Decision_Menu.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add__child, menu);
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
