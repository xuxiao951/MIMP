package com.mimp.android.po;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
/**
 * 关于图形转换的类
 * @author Administrator
 *
 */
public class ImageDispose {
	/**
     * @param 将图片内容解析成字节数组
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            inStream.close();
            return data;

    }
    /**
     * @param 将字节数组转换为ImageView可调用的Bitmap对象
     * @param bytes
     * @param opts
     * @return Bitmap
     */
    public static Bitmap getPicFromBytes(byte[] bytes,BitmapFactory.Options opts) {
            if (bytes != null)
                    if (opts != null)
                            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                                            opts);
                    else
                            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return null;
    }
    /**
     * @param 图片缩放
     * @param bitmap 对象
     * @param w 要缩放的宽度
     * @param h 要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                            matrix, true);
            return newBmp;
    }
    
    /**
 * 把Bitmap转Byte
 * @Author HEH
 * @EditTime 2010-07-19 上午11:45:56
 */
    public static byte[] Bitmap2Bytes(Bitmap bm){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
    }
    /**
 * 把字节数组保存为一个文件
 * @Author HEH
 * @EditTime 2010-07-19 上午11:45:56
 */
public static File getFileFromBytes(byte[] b, String outputFile) {
    BufferedOutputStream stream = null;
    File file = null;
    try {
        file = new File(outputFile);
        FileOutputStream fstream = new FileOutputStream(file);
        stream = new BufferedOutputStream(fstream);
        stream.write(b);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    return file;
}
/** 
 * 根据指定的图像路径和大小来获取缩略图 
 * 此方法有两点好处： 
 *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
 *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
 *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
 *        用这个工具生成的图像不会被拉伸。 
 * @param imagePath 图像的路径 
 * @param width 指定输出图像的宽度 
 * @param height 指定输出图像的高度 
 * @return 生成的缩略图 
 */  
public Bitmap getImageThumbnail(String imagePath, int width, int height) {  
 Bitmap bitmap = null;  
 BitmapFactory.Options options = new BitmapFactory.Options();  
 options.inJustDecodeBounds = true;  
 // 获取这个图片的宽和高，注意此处的bitmap为null  
 bitmap = BitmapFactory.decodeFile(imagePath, options);  
 options.inJustDecodeBounds = false; // 设为 false  
 // 计算缩放比  
 int h = options.outHeight;  
 int w = options.outWidth;  
 int beWidth = w / width;  
 int beHeight = h / height;  
 int be = 1;  
 if (beWidth < beHeight) {  
  be = beWidth;  
 } else {  
  be = beHeight;  
 }  
 if (be <= 0) {  
  be = 1;  
 }  
 options.inSampleSize = be;  
 // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
 bitmap = BitmapFactory.decodeFile(imagePath, options);  
 // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
 bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
   ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
 return bitmap;  
}
/** 
 * 获取视频的缩略图 
 * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
 * @param videoPath 视频的路径 
 * @param width 指定输出视频缩略图的宽度 
 * @param height 指定输出视频缩略图的高度度 
 * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
 * @return 指定大小的视频缩略图 
 * videoThumbnail.setImageBitmap(getVideoThumbnail(videoPath, 60, 60,  
    MediaStore.Images.Thumbnails.MICRO_KIND));  
 */  
public Bitmap getVideoThumbnail(String videoPath, int width, int height,  
  int kind) {  
 Bitmap bitmap = null;  
 // 获取视频的缩略图  
 bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
 System.out.println("w"+bitmap.getWidth());  
 System.out.println("h"+bitmap.getHeight());  
 bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
   ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
 return bitmap;  
}  
//流转化成字符串
public static String inputStream2String(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int i = -1;
    while ((i = is.read()) != -1) {
        baos.write(i);
    }
    return baos.toString();
}

// 流转化成文件
public static void inputStream2File(InputStream is, String savePath) throws Exception {
    System.out.println("文件保存路径为:" + savePath);
    File file = new File(savePath);
    InputStream inputSteam = is;
    BufferedInputStream fis = new BufferedInputStream(inputSteam);
    FileOutputStream fos = new FileOutputStream(file);
    int f;
    while ((f = fis.read()) != -1) {
        fos.write(f);
    }
    fos.flush();
    fos.close();
    fis.close();
    inputSteam.close();

}
/** *//**
 * 文件转化为字节数组

 */
public static byte[] getBytesFromFile(File f){
    if (f == null){
        return null;
    }
    try{
        FileInputStream stream = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1024];
        int n;
        while ((n = stream.read(b)) != -1)
            out.write(b, 0, n);
        stream.close();
        out.close();
        return out.toByteArray();
    } catch (IOException e){
    }
    return null;
}
/** *//**
 * 从字节数组获取对象

 */
public static Object getObjectFromBytes(byte[] objBytes) throws Exception {
    if (objBytes == null || objBytes.length == 0){
        return null;
    }
    ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
    ObjectInputStream oi = new ObjectInputStream(bi);
    return oi.readObject();
}
/** *//**
 * 从对象获取一个字节数组
 */
public static byte[] getBytesFromObject(Serializable obj) throws Exception {
    if (obj == null){
        return null;
    }
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    ObjectOutputStream oo = new ObjectOutputStream(bo);
    oo.writeObject(obj);
    return bo.toByteArray();
}

/**
 * 图片质量压缩
 * @param image
 * @return
 */
private Bitmap compressImage(Bitmap image) {  
	  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
    int options = 100;  
    while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
        baos.reset();//重置baos即清空baos  
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
        options -= 10;//每次都减少10  
    }  
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
    return bitmap;  
}  
/**
 * 图片按比例大小压缩方法（根据路径获取图片并压缩）：
 * @param srcPath
 * @return
 */
private Bitmap getimage(String srcPath) {  
    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
    newOpts.inJustDecodeBounds = true;  
    Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
      
    newOpts.inJustDecodeBounds = false;  
    int w = newOpts.outWidth;  
    int h = newOpts.outHeight;  
    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
    float hh = 800f;//这里设置高度为800f  
    float ww = 480f;//这里设置宽度为480f  
    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
    int be = 1;//be=1表示不缩放  
    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
        be = (int) (newOpts.outWidth / ww);  
    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
        be = (int) (newOpts.outHeight / hh);  
    }  
    if (be <= 0)  
        be = 1;  
    newOpts.inSampleSize = be;//设置缩放比例  
    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
    bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
}  
/**
 * 图片按比例大小压缩方法（根据Bitmap图片压缩）：
 * @param image
 * @return
 */
private Bitmap comp(Bitmap image) {  
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
    if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
        baos.reset();//重置baos即清空baos  
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
    }  
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
    newOpts.inJustDecodeBounds = true;  
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
    newOpts.inJustDecodeBounds = false;  
    int w = newOpts.outWidth;  
    int h = newOpts.outHeight;  
    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
    float hh = 800f;//这里设置高度为800f  
    float ww = 480f;//这里设置宽度为480f  
    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
    int be = 1;//be=1表示不缩放  
    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
        be = (int) (newOpts.outWidth / ww);  
    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
        be = (int) (newOpts.outHeight / hh);  
    }  
    if (be <= 0)  
        be = 1;  
    newOpts.inSampleSize = be;//设置缩放比例  
    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
    isBm = new ByteArrayInputStream(baos.toByteArray());  
    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
}  
/**
 * 把bitmap转换成String
 * 
 * @param filePath
 * @return
 */
public static String bitmapToString(String filePath) {

	Bitmap bm = getSmallBitmap(filePath);
	
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//
	bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	byte[] b = baos.toByteArray();	
	return Base64.encodeToString(b, Base64.DEFAULT);
	
}

/**
 * 计算图片的缩放值
 * 
 * @param options
 * @param reqWidth
 * @param reqHeight
 * @return
 */
public static int calculateInSampleSize(BitmapFactory.Options options,
		int reqWidth, int reqHeight) {
	// Raw height and width of image
	final int height = options.outHeight;
	final int width = options.outWidth;
	int inSampleSize = 1;

	if (height > reqHeight || width > reqWidth) {

		// Calculate ratios of height and width to requested height and
		// width
		final int heightRatio = Math.round((float) height
				/ (float) reqHeight);
		final int widthRatio = Math.round((float) width / (float) reqWidth);

		// Choose the smallest ratio as inSampleSize value, this will
		// guarantee
		// a final image with both dimensions larger than or equal to the
		// requested height and width.
		inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	}

	return inSampleSize;
}

/**
 * 根据路径获得突破并压缩返回bitmap用于显示
 * 
 * @param imagesrc
 * @return
 */
public static Bitmap getSmallBitmap(String filePath) {
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeFile(filePath, options);

	// Calculate inSampleSize
	options.inSampleSize = calculateInSampleSize(options, 480, 800);

	// Decode bitmap with inSampleSize set
	options.inJustDecodeBounds = false;

	return BitmapFactory.decodeFile(filePath, options);
}

/**
 * 根据路径删除图片
 * 
 * @param path
 */
public static void deleteTempFile(String path) {
	File file = new File(path);
	if (file.exists()) {
		file.delete();
	}
}

/**
 * 添加到图库
 */
public static void galleryAddPic(Context context, String path) {
	Intent mediaScanIntent = new Intent(
			Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	File f = new File(path);
	Uri contentUri = Uri.fromFile(f);
	mediaScanIntent.setData(contentUri);
	context.sendBroadcast(mediaScanIntent);
}

/**
 * 获取保存图片的目录
 * 
 * @return
 */
public static File getAlbumDir() {
	File dir = new File(
			Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			getAlbumName());
	if (!dir.exists()) {
		dir.mkdirs();
	}
	return dir;
}

/**
 * 获取保存 隐患检查的图片文件夹名称
 * 
 * @return
 */
public static String getAlbumName() {
	return "sheguantong";
}
/*
压缩图片，处理某些手机拍照角度旋转的问题
*/
//public static String compressImage(Context context,String filePath,String fileName,int q) throws FileNotFoundException {
//
//        Bitmap bm = getSmallBitmap(filePath);
//
//        int degree = readPictureDegree(filePath);
//
//        if(degree!=0){//旋转照片角度
//            bm=rotateBitmap(bm,degree);
//        }
//
//        File imageDir = SDCardUtils.getImageDir(context);
//
//        File outputFile=new File(imageDir,fileName);
//
//        FileOutputStream out = new FileOutputStream(outputFile);
//
//        bm.compress(Bitmap.CompressFormat.JPEG, q, out);
//
//        return outputFile.getPath();
//    }
public static int readPictureDegree(String path) {
    int degree = 0;
    try {
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
            degree = 90;
            break;
        case ExifInterface.ORIENTATION_ROTATE_180:
            degree = 180;
            break;
        case ExifInterface.ORIENTATION_ROTATE_270:
            degree = 270;
            break;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return degree;
}
public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {
    if (bitmap != null) {
        Matrix m = new Matrix();
        m.postRotate(degress); 
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), m, true);
        return bitmap;
    }
    return bitmap;
}
/** 
 * 压缩图片 
 * @param bitmap 源图片 
 * @param width 想要的宽度 
 * @param height 想要的高度 
 * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩 
 * @return Bitmap 
 */  
public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {  
    // 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图  
    if (bitmap.getWidth() < width && bitmap.getHeight() < height) {return bitmap;}  
    // 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);  
    // scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃  
    float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();  
    float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();  
    if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸  
        sx = (sx < sy ? sx : sy);sy = sx;// 哪个比例小一点，就用哪个比例  
    }  
    Matrix matrix = new Matrix();  
    matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * 旋转图片 
 * @param bitmap 源图片 
 * @param angle 旋转角度(90为顺时针旋转,-90为逆时针旋转) 
 * @return Bitmap 
 */  
public Bitmap rotate(Bitmap bitmap, float angle) {  
    Matrix matrix = new Matrix();    
    matrix.postRotate(angle);  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * 放大或缩小图片 
 * @param bitmap 源图片 
 * @param ratio 放大或缩小的倍数，大于1表示放大，小于1表示缩小 
 * @return Bitmap 
 */  
public Bitmap zoom(Bitmap bitmap, float ratio) {  
    if (ratio < 0f) {return bitmap;}  
    Matrix matrix = new Matrix();  
    matrix.postScale(ratio, ratio);  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * 在图片上印字 
 * @param bitmap 源图片 
 * @param text 印上去的字 
 * @param param 字体参数分别为：颜色,大小,是否加粗,起点x,起点y; 比如：{color : 0xFF000000, size : 30, bold : true, x : 20, y : 20} 
 * @return Bitmap 
 */  
public Bitmap printWord(Bitmap bitmap, String text, Map<String, Object> param) {  
    if (text.isEmpty() || null == param) {return bitmap;}  
    Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
    Canvas canvas = new Canvas(newBitmap);  
    canvas.drawBitmap(bitmap, 0, 0, null);canvas.save(Canvas.ALL_SAVE_FLAG);canvas.restore();  
    Paint paint = new Paint();  
    paint.setColor(null != param.get("color") ? (Integer) param.get("color") : Color.BLACK);  
    paint.setTextSize(null != param.get("size") ? (Integer) param.get("size") : 20);  
    paint.setFakeBoldText(null != param.get("bold") ? (Boolean) param.get("bold") : false);  
    canvas.drawText(text, null != param.get("x") ? (Integer) param.get("x") : 0, null != param.get("y") ? (Integer) param.get("y") : 0, paint);  
    canvas.save(Canvas.ALL_SAVE_FLAG);canvas.restore();  
    return newBitmap;  
}  
/** 
 * 创建logo(给图片加水印),  
 * @param bitmaps 原图片和水印图片 
 * @param left 左边起点坐标 
 * @param top 顶部起点坐标t 
 * @return Bitmap 
 */  
public Bitmap createLogo(Bitmap[] bitmaps, int left, int top) {  
    Bitmap newBitmap = Bitmap.createBitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight(), Config.ARGB_8888);  
    Canvas canvas = new Canvas(newBitmap);  
    for (int i = 0; i < bitmaps.length; i++) {  
        if (i == 0) {  
            canvas.drawBitmap(bitmaps[0], 0, 0, null);  
        } else {  
            canvas.drawBitmap(bitmaps[i], left, top, null);  
        }  
        canvas.save(Canvas.ALL_SAVE_FLAG);canvas.restore();  
    }  
    return newBitmap;  
}  
private int width = 140, height = 40, codeLen = 4;  
private String checkCode = "";  
private Random random = new Random();  
  
/** 
 * 产生一个4位随机数字的图片验证码 
 * @return Bitmap 
 */  
public Bitmap createCode() {  
    checkCode = "";  
    String[] chars = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };  
    for (int i = 0; i < codeLen; i++) {checkCode += chars[random.nextInt(chars.length)];}  
    Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);  
    Canvas canvas = new Canvas(bitmap);canvas.drawColor(Color.WHITE);  
    Paint paint = new Paint();paint.setTextSize(30);paint.setColor(Color.BLUE);  
    for (int i = 0; i < checkCode.length(); i++) {  
        paint.setColor(randomColor(1));paint.setFakeBoldText(random.nextBoolean());  
        float skewX = random.nextInt(11) / 10;  
        paint.setTextSkewX(random.nextBoolean() ? skewX : -skewX);  
        int x = width / codeLen * i + random.nextInt(10);  
        canvas.drawText(String.valueOf(checkCode.charAt(i)), x, 28, paint);  
    }  
    for (int i = 0; i < 3; i++) {drawLine(canvas, paint);}  
    for (int i = 0; i < 255; i++) {drawPoints(canvas, paint);}  
    canvas.save(Canvas.ALL_SAVE_FLAG);canvas.restore();  
    return bitmap;  
}  
  
/** 
 * 获得一个随机的颜色 
 * @param rate 
 * @return  
 */  
public int randomColor(int rate) {  
    int red = random.nextInt(256) / rate, green = random.nextInt(256) / rate, blue = random.nextInt(256) / rate;  
    return Color.rgb(red, green, blue);  
}  

/** 
 * 画随机线条 
 * @param canvas 
 * @param paint 
 */  
public void drawLine(Canvas canvas, Paint paint) {  
    int startX = random.nextInt(width), startY = random.nextInt(height);  
    int stopX = random.nextInt(width), stopY = random.nextInt(height);  
    paint.setStrokeWidth(1);paint.setColor(randomColor(1));  
    canvas.drawLine(startX, startY, stopX, stopY, paint);  
}  

/** 
 * 画随机干扰点 
 * @param canvas 
 * @param paint 
 */  
public void drawPoints(Canvas canvas, Paint paint) {  
    int stopX = random.nextInt(width), stopY = random.nextInt(height);  
    paint.setStrokeWidth(1);  
    paint.setColor(randomColor(1));  
    canvas.drawPoint(stopX, stopY, paint);  
}  
  
/** 
 * 返回真实验证码字符串 
 * @return String 
 */  
public String getCheckCode() {  
    return checkCode;  
}  
}
