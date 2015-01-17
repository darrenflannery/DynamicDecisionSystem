package ie.gmit.computing.dynamicdecisionsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ie.gmit.computing.dynamicdecisionsystem.Model.Node;

public class Decision_Menu extends ActionBarActivity implements DialogInterface.OnClickListener{

    LinearLayout layout;
    TextView headerText;
    ImageView imageView;

    public final static String PARENT = "Parent";
    public final static String PHOTO = "Photo";

    private Node root;
    private Node tempParent;
    private Node moveNode;
    private Node deleteNode;
    private String photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision__menu);

        Intent intent = getIntent();
        photoName = (String)intent.getSerializableExtra(Camera_Log.MESSAGE);

        //load the tree
        try {
            java.io.File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DecisionTree.ser");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream((file)));
            root = (Node) in.readObject();
            if(root==null){ root = new Node("Start", null, null);}
            Log.v("myApp", "Load Successful");
        } catch (Exception ex) {
            Log.v("Serialization Read Error : ", ex.getMessage());
            root = new Node("Start", null, null);
        }

        //set temporary Node as parent
        tempParent = root;

        //set the header
        headerText = (TextView) findViewById(R.id.headerTextView);
        headerText.setText("Start");

        //populate menu
        layout = (LinearLayout) findViewById(R.id.dyn_layout);
        populateMenu(tempParent, layout);
    }

    public void populateMenu(Node node, LinearLayout layout){
        //set header of parent
        headerText.setText(node.getName().toString());

        //if root and empty
        if((node.isRoot()==true) && (node.childrenSize()==0))
        {
            TextView txt = new TextView(this);
            txt.setText("Add Menu Items");
            layout.addView(txt);
        }

        //if node is not a leaf (has children) - show menu
        if(node.isLeaf()==false) {
            for (int i = 0; i < node.childrenSize(); i++) {
                Button button = new Button(this);
                button.setId(i);
                button.setText(node.getChild(i).getName());
                //if menu item is a leaf, change color to green
                if (node.getChild(i).isLeaf() == true) {
                    button.setTextColor(Color.GREEN);
                }
                layout.addView(button);
                btnClick(button, layout);
            }
        }

        //if it is a leaf and not the root - show image
        if(node.isRoot()==false && node.isLeaf()==true) {
            imageView = new ImageView(this);
            Bitmap image = tempParent.getImage();
            if (image != null) {
                imageView.setImageBitmap(image);
            } else {
                imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.defaultimage));
            }
            layout.addView(imageView);
            //click event to add image
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
                }
            });

            Button btnLog = new Button(this);
            btnLog.setText("Log Similar Item");
            btnLog.setTextColor(Color.BLUE);
            layout.addView(btnLog);
            btnLogClick(btnLog);
        }

        //if not the root (no parent) - show back button
        if (node.isRoot()==false) {
            Button btnBack = new Button(this);
            btnBack.setText("Back");
            btnBack.setTextColor(Color.RED);
            layout.addView(btnBack);
            btnBackClick(btnBack, layout);
        }

    }

    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode==1) {
                imageView.setImageURI(data.getData());
                Bitmap bitmap = scaleDownBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), 100, this);
               // bitmap = scaleDownBitmap(bitmap,100,this);
                tempParent.setImage(bitmap);
                saveObject(root);
            }
        }
    }

    public void btnClick(final Button button, final LinearLayout layout) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                layout.removeAllViews();
                tempParent = findNode(button, tempParent);
                populateMenu(tempParent, layout);
            }
        });
    }

    public void btnBackClick(final Button button, final LinearLayout layout) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                layout.removeAllViews();
                tempParent = tempParent.getParent();
                populateMenu(tempParent, layout);
            }
        });
    }

    public Node findNode(Button button, Node parent) {
        int i = button.getId();
        return parent.getChild(i);
    }

    public void addClick (View view) {
        Intent intent = new Intent(this, Add_Child.class);
        intent.putExtra(PARENT, tempParent);
        startActivity(intent);
    }

    public void insertClick(View view) {
        showInsertDialogMessage();
    }

    public void deleteClick(View view) {
        //header
        headerText.setText("Choose item to delete");

        layout.removeAllViews();

        //menu items
        for (int i = 0; i < tempParent.childrenSize(); i++) {
            Button button = new Button(this);
            button.setText(tempParent.getChild(i).getName());
            button.setId(i);
            button.setTextColor(Color.RED);
            layout.addView(button);
            deleteNodeClick(button);
        }

        visibleButtons(0);
        showCancelButton();
    }

    public void deleteNodeClick(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                deleteNode = findNode(button, tempParent);
                showDeleteDialogMessage();
            }
        });
    }

    public void showDeleteDialogMessage() {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage("Do you also want to delete all the sub-menu items of this item?")
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Delete Menu Item")
                .setPositiveButton("Yes", this)
                .setNegativeButton("No, just this", this)
                .setNeutralButton("Cancel", this)
                .setCancelable(false)
                .create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setTag(
                "DIALOG_DELETE");
    }

    public void showInsertDialogMessage() {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage("All the items on this page will be sub menu items of your new item.\n\nDo you want to continue?")
                .setIcon(R.drawable.ic_launcher)
                .setTitle("Insert Menu Item")
                .setPositiveButton("Yes", this)
                .setNeutralButton("Cancel", this)
                .setCancelable(false)
                .create();
        ad.show();
        ad.getButton(DialogInterface.BUTTON_POSITIVE).setTag(
                "DIALOG_INSERT");
    }

    public void moveClick(View view){
        //header
        headerText.setText("Choose item to move");

        layout.removeAllViews();

        //menu items
        for (int i = 0; i < tempParent.childrenSize(); i++) {
            Button button = new Button(this);
            button.setText(tempParent.getChild(i).getName());
            button.setId(i);
            button.setTextColor(Color.RED);
            layout.addView(button);
            moveNodeClick(button);
        }

        visibleButtons(0);

        showCancelButton();
    }

    public void moveNodeClick(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                moveNode = findNode(button, tempParent);
                layout.removeAllViews();
                tempParent.removeChildFromParent(moveNode);
                populateMenu(tempParent, layout);
                showPlaceButton();
                Toast.makeText(getApplicationContext(), "Choose where you want to place the item: " + moveNode.getName().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void placeHere(final Button button, final Node move){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                move.placeNode(tempParent);
                moveNode=null;
                layout.removeAllViews();
                populateMenu(tempParent,layout);
                visibleButtons(1);
                saveObject(root);
            }
        });
    }

    public void onClick(DialogInterface dialog, int which) {
        AlertDialog alertDialog = (AlertDialog) dialog;
        String s = (String) alertDialog.getButton(
                DialogInterface.BUTTON_POSITIVE).getTag();
        boolean isDialogDelete = s.equals("DIALOG_DELETE");

        switch(which){
            case DialogInterface.BUTTON_POSITIVE: // yes
                if(isDialogDelete) {        //delete node
                    layout.removeAllViews();
                    deleteNode.delete();
                    populateMenu(tempParent, layout);
                    visibleButtons(1);
                    saveObject(root);
                }
                else{   //insert node
                    Intent intent = new Intent(this, Insert_Child.class);
                    intent.putExtra(PARENT, tempParent);
                    startActivity(intent);
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE: // no
                if(isDialogDelete) {        //remove node but keep children
                    layout.removeAllViews();
                    deleteNode.remove();
                    populateMenu(tempParent, layout);
                    visibleButtons(1);
                    saveObject(root);
                    Toast.makeText(getApplicationContext(), "All the sub menu items of the deleted item have been added here", Toast.LENGTH_SHORT).show();
                }
                else{   //insert node
                    break;
                }
                break;
            case DialogInterface.BUTTON_NEUTRAL: // neutral
                break;
            default:
                break;
        }
    }

    public void cancelClick(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                headerText.setText(tempParent.getName().toString());
                layout.removeAllViews();
                if(moveNode!=null){
                    moveNode.addChild(tempParent);
                }
                moveNode=null;
                populateMenu(tempParent, layout);
                visibleButtons(1);
            }
        });
    }

    public void showCancelButton(){
        //cancel button
        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        if(cancelBtn.getVisibility()==View.GONE) {
            cancelBtn.setVisibility(View.VISIBLE);
        }
        cancelClick(cancelBtn);
    }

    public void showPlaceButton(){
        //place button
        Button placeBtn = (Button) findViewById(R.id.placeBtn);
        if(placeBtn.getVisibility()==View.GONE) {
            placeBtn.setVisibility(View.VISIBLE);
        }
        placeHere(placeBtn, moveNode);
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    public void saveObject(Node root){
        try
        {
            java.io.File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DecisionTree.ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file)); //Select where you wish to save the file...
            oos.writeObject(root); // write the class as an 'object'
            oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
            oos.close();// close the stream
            Log.v("myApp","Save Successful");
        }
        catch(Exception ex)
        {
            Log.v("Serialization Save Error : ",ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void visibleButtons(int i){

        Button deleteBtn = (Button) findViewById(R.id.deleteBtn);
        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button insertBtn = (Button) findViewById(R.id.insertBtn);
        Button moveBtn = (Button) findViewById(R.id.moveBtn);

        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        Button placeBtn = (Button) findViewById(R.id.placeBtn);

        if(i==1) {
            deleteBtn.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.VISIBLE);
            insertBtn.setVisibility(View.VISIBLE);
            moveBtn.setVisibility(View.VISIBLE);

            if(placeBtn.getVisibility()==View.VISIBLE){
                placeBtn.setVisibility(View.GONE);
            }

            if(cancelBtn.getVisibility()==View.VISIBLE){
                cancelBtn.setVisibility(View.GONE);
            }
        }
        else {
            deleteBtn.setVisibility(View.GONE);
            addBtn.setVisibility(View.GONE);
            insertBtn.setVisibility(View.GONE);
            moveBtn.setVisibility(View.GONE);
        }
    }

    public void btnLogClick(final Button button) {
        final Intent intent = new Intent(this, Log_Data.class);
        intent.putExtra(PARENT, tempParent);
        intent.putExtra(PHOTO, photoName);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    public void homeClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_decision__menu, menu);
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