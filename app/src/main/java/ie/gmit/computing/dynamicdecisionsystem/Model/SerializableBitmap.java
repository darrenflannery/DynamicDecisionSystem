package ie.gmit.computing.dynamicdecisionsystem.Model;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Darren on 16/01/2015.
 */
public class SerializableBitmap implements Serializable {
    private static final long serialVersionUID = 777L;
    private Bitmap image;
     public byte[] bitmapBytes;

    public SerializableBitmap() {}
    public SerializableBitmap(Bitmap image) {
        this.image = image;
    }

    public byte[] serialize(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        return bitmapBytes;
    }

}
