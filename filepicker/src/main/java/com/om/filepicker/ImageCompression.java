package com.om.filepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompression {
    private float maxHeight = 1280.0f;
    private float maxWidth = 1280.0f;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    public ImageCompression(Context context) {
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public File compressToFile(File imageFile, int quality) throws IOException {
        String destinationPath = FilePickerController.createImageFile().getPath();
        return compressImage(imageFile, maxWidth, maxHeight, compressFormat, quality, destinationPath);
    }

    public Bitmap compressToBitmap(File imageFile) throws IOException {
        return decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight);
    }

    private static File compressImage(File imageFile, float reqWidth, float reqHeight, Bitmap.CompressFormat compressFormat,
                                      int quality, String destinationPath) throws IOException {

        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();

        if (!file.exists()) {
            file.mkdirs();
        }

        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationPath);
    }

    private static Bitmap decodeSampledBitmapFromFile(File imageFile, float reqWidth, float reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Matrix matrix = new Matrix();

        if (orientation == 6) {
            matrix.postRotate(90);
        } else if (orientation == 3) {
            matrix.postRotate(180);
        } else if (orientation == 8) {
            matrix.postRotate(270);
        }

        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return scaledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
