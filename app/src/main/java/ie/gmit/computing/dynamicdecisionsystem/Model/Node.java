package ie.gmit.computing.dynamicdecisionsystem.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.*;

/**
 * Created by Darren on 16/01/2015.
 */
public class Node  implements Serializable {
    private static final long serialVersionUID = 777L;
    private Node parent;
    private Node root;
    private List<Node> children = new ArrayList<Node>();
    private String name;
    private byte[] bitmapBytes;

    public Node(String name, Node parent, Node root) {
        super();
        this.name = name;
        this.parent = parent;
        this.root=root;
    }

    public void setParent(Node parent){
        this.parent = parent;
    }

    public Node getParent(){
        return this.parent;
    }

    public Node getRoot(){
        if (root==null){
            return this;
        }
        else{
            return this.root;
        }
    }


    public void addChild(Node node){
        this.children.add(node);
    }

    public void insertChild(Node parent){
        int size = parent.childrenSize();
        for(int i=0;i<size;i++){
            Node n = parent.children.get(0);
            this.children.add(n);//add children to new node
            n.setParent(this);
            parent.children.remove(n);//remove children from previous node
        }
        this.parent = parent;
        parent.addChild(this);
    }

    public void removeChildFromParent(Node node){
        this.children.remove(node);
    }

    public int childrenSize(){
        return children.size();
    }

    public Node getChild(int i){
        return children.get(i);
    }

    public String getName() {
        return name;
    }

    public void setImage(Bitmap image) {
        bitmapBytes =  new SerializableBitmap(image).serialize();
    }

    public Bitmap getImage(){
        Bitmap image;
        if(bitmapBytes!=null){
            image = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            return image;
        }
        else return null;
    }

    public void delete(){
        this.getParent().removeChildFromParent(this);
    }

    public void remove(){
        Node parent = getParent();
        if(this.childrenSize()>0) {
            for (int i = 0; i < this.childrenSize(); i++) {
                Node n = this.children.get(i);
                parent.children.add(n);
                n.children.remove(n);
                n.parent = parent;
            }
        }
        this.delete();
    }

    public void placeNode(Node newParent){
        Node oldParent = this.getParent();
        oldParent.removeChildFromParent(this);
        newParent.addChild(this);
        this.setParent(newParent);
    }

    public boolean isLeaf(){
        return children.size()==0;
    }

    public boolean isRoot(){
        return this.parent == null;
    }
}
