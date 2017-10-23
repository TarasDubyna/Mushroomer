package taras.mushroomer.model;

public class Mushroom{
    private int id;
    private String name;
    private String nameLat;

    private String description;
    private int descriptionDir;

    private String type;
    private int imageDir;

    public Mushroom() {
    }

    public Mushroom(String name, String type, int imageDir) {
        this.name = name;
        this.type = type;
        this.imageDir = imageDir;
    }

    public Mushroom(String name, String nameLat, String type, int descriptionDir, int imageDir) {
        this.name = name;
        this.nameLat = nameLat;
        this.descriptionDir = descriptionDir;
        this.type = type;
        this.imageDir = imageDir;
    }

    public int getDescriptionDir() {
        return descriptionDir;
    }
    public void setDescriptionDir(int descriptionDir) {
        this.descriptionDir = descriptionDir;
    }

    public String getNameLat() {
        return nameLat;
    }
    public void setNameLat(String nameLat) {
        this.nameLat = nameLat;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    public int getImageDir() {
        return imageDir;
    }
    public void setImageDir(int imageDir) {
        this.imageDir = imageDir;
    }

}
