package ericli.foodforfriends.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by ericli on 11/29/2017.
 */

public class ImageRefactor {

    public static final String IMAGE_FILE_NAME_PREFIX = "chat-";
    public static final double LINEAR_DIMENSION_MAX = 500.0;


    public static Uri savePhotoImage(Context context, Bitmap imageBitmap) {
        File file_Photo = null;
        try {
            file_Photo = createImageFile(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file_Photo == null) {
            return null;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file_Photo);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }
        return Uri.fromFile(file_Photo);
    }


    public static Bitmap getBitmapForUri(Context context, Uri imageUri) {
        Bitmap b = null;
        try {
            b = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }


    public static Bitmap scaleImage(Bitmap bitmap) {
        int height_Original = bitmap.getHeight();
        int width_Original = bitmap.getWidth();
        double scaleFactor = LINEAR_DIMENSION_MAX / (double) (height_Original + width_Original);
        if (scaleFactor < 1.0) {
            return Bitmap.createScaledBitmap(bitmap, (int) Math.round(width_Original * scaleFactor), (int) Math.round(height_Original * scaleFactor), true);
        } else {
            return bitmap;
        }
    }


    protected static File createImageFile(Context context) throws IOException {


        return File.createTempFile(
                IMAGE_FILE_NAME_PREFIX + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()), ".jpg", context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        );
    }

}
