package taras.mushroomer.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Mushroom{
    private int id;
    private String name;
    private String type;
    private Bitmap image;
    private String imageDir;

    public Mushroom() {
    }

    public Mushroom(String name, String type, Bitmap image, String imageDir) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.imageDir = imageDir;
    }

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getImageDir() {
        return imageDir;
    }
    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

}
