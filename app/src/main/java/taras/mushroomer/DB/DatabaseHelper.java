package taras.mushroomer.DB;


import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import taras.mushroomer.model.Mushroom;
import taras.mushroomer.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Mushroom.db";

    private static final String TABLE_MUSHROOM = "mushroom_table";

    private static final String COL_MUSHROOM_ID = "mushroom_id";
    private static final String COL_MUSHROOM_NAME = "mushroom_name";
    private static final String COL_MUSHROOM_LAT_NAME = "mushroom_lat_name";
    private static final String COL_MUSHROOM_DESCRIPTION_DIR = "mushroom_description_dir";
    private static final String COL_MUSHROOM_TYPE = "mushroom_type";
    private static final String COL_MUSHROOM_PHOTO_DIR = "mushroom_photo_dir";

    private long result;
    private Context context;

    private static final String IMAGE_DIR = "imageDir";

    private String saveToInternalStorage(Bitmap bitmapImage, String name){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        // Create imageDir
        String fileNameDir = name + ".jpg";
        File mypath = new File(directory, fileNameDir);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Bitmap loadImageFromStorage(String path, String name){
        Bitmap bitmap = null;
        String fileName = name + ".jpg";
        try {
            File f = new File(path,fileName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void createStartData(SQLiteDatabase db){
        ArrayList<Mushroom> mushrooms = new ArrayList<Mushroom>(){{

            add(new Mushroom("Польский гриб", "Tricholoma equestre", "Съедобные", R.string.polsky_grib, R.drawable.polsky_grib));
            add(new Mushroom("Дождевик съедобный", "Lactarius resimus", "Съедобные" ,R.string.dojdevik_syedobni, R.drawable.dojdevik_syedobni));

            add(new Mushroom("Рядовка зелёная", "Tricholoma equestre", "Условно-съедобные", R.string.ryadovka_zelenya, R.drawable.ryadovka_zelenya));
            add(new Mushroom("Груздь настоящий", "Lactarius resimus", "Условно-съедобные" ,R.string.gruzd_nastoyashy, R.drawable.gruzd_nastoyashy));

            add(new Mushroom("Бледная поганка", "Amanita phalloides", "Несъедобные", R.string.poganka_blednaya, R.drawable.poganka_blednya));
            add(new Mushroom("Мухомор красный", "Amanita muscaria", "Несъедобные" ,R.string.muhomor_red, R.drawable.muhomor_red));

        }};

        for (Mushroom mushroom: mushrooms){
            ContentValues cv = new ContentValues();
            cv.put(COL_MUSHROOM_NAME, mushroom.getName());
            cv.put(COL_MUSHROOM_LAT_NAME, mushroom.getNameLat());
            cv.put(COL_MUSHROOM_TYPE, mushroom.getType());
            cv.put(COL_MUSHROOM_DESCRIPTION_DIR, mushroom.getDescriptionDir());
            cv.put(COL_MUSHROOM_PHOTO_DIR, mushroom.getImageDir());
            result = db.insertWithOnConflict(TABLE_MUSHROOM, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            cv.clear();
        }
    }

    private String getDescriptionFromResource(int resource){
        String text = context.getResources().getString(resource);
        return text;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        this.context = context;
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MUSHROOM + " ("
                + COL_MUSHROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " //0
                + COL_MUSHROOM_NAME + " TEXT, " //1
                + COL_MUSHROOM_LAT_NAME + " TEXT, " //2
                + COL_MUSHROOM_TYPE + " TEXT, "// 3
                + COL_MUSHROOM_DESCRIPTION_DIR + " INTEGER, "//4
                + COL_MUSHROOM_PHOTO_DIR + " INTEGER);");// 5

        createStartData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSHROOM);
        onCreate(db);
    }

    public boolean addListMushrooms(ArrayList<Mushroom> mushrooms){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Mushroom mushroom: mushrooms){
            ContentValues cv = new ContentValues();
            cv.put(COL_MUSHROOM_NAME, mushroom.getName());
            cv.put(COL_MUSHROOM_TYPE, mushroom.getType());
            //cv.put(COL_MUSHROOM_PHOTO, setImageToDB(mushroom.getPhoto()));
            result = db.insertWithOnConflict(TABLE_MUSHROOM, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            cv.clear();
        }
        if (result == -1) return false;
        else return true;
    }

    public ArrayList<Mushroom> getMushroomsByType(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_MUSHROOM + " where "
                + COL_MUSHROOM_TYPE + " =?;", new String[] {type});
        ArrayList<Mushroom> mushroomsList = new ArrayList<>();
        if (res.moveToFirst()){
            while (!res.isAfterLast()){
                Mushroom mushroom = new Mushroom();
                mushroom.setId(res.getInt(0));
                mushroom.setName(res.getString(1));
                mushroom.setNameLat(res.getString(2));
                mushroom.setType(res.getString(3));
                mushroom.setDescription(getDescriptionFromResource(res.getInt(4)));
                mushroom.setImageDir(res.getInt(5));
                mushroomsList.add(mushroom);
                res.moveToNext();
            }
        }
        return mushroomsList;
    }

    public ArrayList<ArrayList<Mushroom>> getAllMushrooms(){
        String[] mushroomsTypes = {"Съедобные", "Условно-съедобные", "Несъедобные"};
        ArrayList<ArrayList<Mushroom>> mushroomsList = new ArrayList<>();
        for (int i = 0; i < mushroomsTypes.length; i++){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_MUSHROOM + " where "
                    + COL_MUSHROOM_TYPE + " =?;", new String[] {mushroomsTypes[i]});
            ArrayList<Mushroom> mushroomsListByType = new ArrayList<>();
            if (res.moveToFirst()){
                while (!res.isAfterLast()){
                    Mushroom mushroom = new Mushroom();
                    mushroom.setId(res.getInt(0));
                    mushroom.setName(res.getString(1));
                    mushroom.setType(res.getString(3));
                    mushroom.setImageDir(res.getInt(5));
                    mushroomsListByType.add(mushroom);
                    res.moveToNext();
                }
            }
            mushroomsList.add(mushroomsListByType);
        }
        return mushroomsList;
    }

    public int getMushroomImageDir(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_MUSHROOM + " where "
                + COL_MUSHROOM_NAME + " =?;", new String[] {name});
        if (res.moveToFirst()){
            while (!res.isAfterLast()){
                return res.getInt(5);
            }
        }
        return 0;
    }


    private byte[] setImageToDB (Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    private Bitmap getImage(byte[] dataImage){
        return BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
    }
}
