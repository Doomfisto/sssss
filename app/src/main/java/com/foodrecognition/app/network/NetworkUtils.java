package com.foodrecognition.app.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 网络工具类
 * 提供网络相关的工具方法
 */
public class NetworkUtils {

    private static final int MAX_IMAGE_SIZE = 1024; // 最大图片尺寸（KB）
    private static final String IMAGE_PREFIX = "FOOD_";
    private static final String IMAGE_SUFFIX = ".jpg";

    /**
     * 检查网络连接状态
     * @param context 上下文
     * @return 是否有网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * 将Bitmap转换为Base64字符串
     * @param bitmap 图片Bitmap
     * @return Base64编码的字符串
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * 创建图片的MultipartBody.Part用于上传
     * @param file 图片文件
     * @return MultipartBody.Part实例
     */
    public static MultipartBody.Part createImagePart(File file) {
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
        );
        return MultipartBody.Part.createFormData("image", file.getName(), requestFile);
    }

    /**
     * 压缩Bitmap
     * @param bitmap 原始Bitmap
     * @param maxSize 最大尺寸（KB）
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressBitmap(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int quality = 100;
        
        while (baos.toByteArray().length / 1024 > maxSize && quality > 10) {
            baos.reset();
            quality -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        
        return bitmap;
    }

    /**
     * 从Uri获取Bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // 计算缩放比例
        int scale = 1;
        while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) > MAX_IMAGE_SIZE * 1024) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();
        return bitmap;
    }

    /**
     * 创建临时图片文件
     */
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = IMAGE_PREFIX + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();
        return File.createTempFile(imageFileName, IMAGE_SUFFIX, storageDir);
    }

    /**
     * 保存Bitmap到文件
     */
    public static void saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
    }
} 