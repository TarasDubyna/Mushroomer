package taras.mushroomer.DB;


import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import taras.mushroomer.Model.Mushroom;
import taras.mushroomer.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Mushroom.db";

    private static final String TABLE_MUSHROOM = "mushroom_table";

    private static final String COL_MUSHROOM_ID = "mushroom_id";
    private static final String COL_MUSHROOM_NAME = "mushroom_name";
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
            add(new Mushroom("Польский гриб", "Съедобные", getBitmapImageFromResource(R.drawable.polsky), String.valueOf(R.drawable.polsky)));
            add(new Mushroom("Дождевик съедобный", "Съедобные", getBitmapImageFromResource(R.drawable.dojdevik), String.valueOf(R.drawable.dojdevik)));

            add(new Mushroom("Рядовка зелёная", "Условно-съедобные", getBitmapImageFromResource(R.drawable.tricholoma_equestre), String.valueOf(R.drawable.tricholoma_equestre)));
            add(new Mushroom("Груздь настоящий", "Условно-съедобные",getBitmapImageFromResource(R.drawable.lactarius_resimus), String.valueOf(R.drawable.lactarius_resimus)));

            add(new Mushroom("Мухомор красный", "Несъедобные", getBitmapImageFromResource(R.drawable.amanita_muscaria), String.valueOf(R.drawable.amanita_muscaria)));
            add(new Mushroom("Бледная поганка", "Несъедобные", getBitmapImageFromResource(R.drawable.amanita_phalloides), String.valueOf(R.drawable.amanita_phalloides)));
        }};

        for (Mushroom mushroom: mushrooms){
            ContentValues cv = new ContentValues();
            cv.put(COL_MUSHROOM_NAME, mushroom.getName());
            cv.put(COL_MUSHROOM_TYPE, mushroom.getType());
            cv.put(COL_MUSHROOM_PHOTO_DIR, mushroom.getImageDir());
            result = db.insertWithOnConflict(TABLE_MUSHROOM, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            cv.clear();
        }
    }

    private Bitmap getBitmapImageFromResource(int resource){
        Drawable drawable = context.getResources().getDrawable(resource);
        return ((BitmapDrawable) drawable).getBitmap();
    }



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        this.context = context;
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MUSHROOM + " ("
                + COL_MUSHROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MUSHROOM_NAME + " TEXT, "
                + COL_MUSHROOM_TYPE + " TEXT, "
                + COL_MUSHROOM_PHOTO_DIR + " TEXT);");

        createStartData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSHROOM);
        onCreate(db);
    }

    public boolean addMushroom(Mushroom mushroom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MUSHROOM_NAME, mushroom.getName());
        cv.put(COL_MUSHROOM_TYPE, mushroom.getType());
        mushroom.setImageDir(saveToInternalStorage(mushroom.getImage(), mushroom.getName()));
        cv.put(COL_MUSHROOM_PHOTO_DIR, mushroom.getImageDir());
        result = db.insertWithOnConflict(TABLE_MUSHROOM, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        cv.clear();
        if (result == -1) return false;
        else return true;
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

    public ArrayList<Mushroom> getAllMushroomsByType(String type){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_MUSHROOM + " where "
                + COL_MUSHROOM_TYPE + " =?;", new String[] {type});
        ArrayList<Mushroom> mushroomsList = new ArrayList<>();

        if (res.moveToFirst()){
            while (!res.isAfterLast()){
                Mushroom mushroom = new Mushroom();
                mushroom.setId(res.getInt(0));
                mushroom.setName(res.getString(1));
                mushroom.setType(res.getString(2));
                mushroom.setImageDir(res.getString(3));
                try {
                    mushroom.setImage(getBitmapImageFromResource(Integer.parseInt(mushroom.getImageDir())));
                } catch (Exception e){
                    mushroom.setImage(loadImageFromStorage(mushroom.getImageDir(), mushroom.getName()));
                }
                mushroomsList.add(mushroom);
                res.moveToNext();
            }
        }


        /*
        if (res.getCount() == 0){
            System.out.println("Currency table is empty");
        } else {
            res.moveToPrevious();
            if (res.moveToFirst()){
                do{
                    Mushroom mushroom = new Mushroom();
                    mushroom.setId(res.getInt(0));
                    mushroom.setName(res.getString(1));
                    mushroom.setType(res.getString(2));
                    mushroom.setPhoto(getImage(res.getBlob(3)));
                    mushroomsList.add(mushroom);
                } while (res.moveToNext());
            }
        }*/
        return mushroomsList;
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
