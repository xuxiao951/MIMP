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
 * ����ͼ��ת������
 * @author Administrator
 *
 */
public class ImageDispose {
	/**
     * @param ��ͼƬ���ݽ������ֽ�����
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
     * @param ���ֽ�����ת��ΪImageView�ɵ��õ�Bitmap����
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
     * @param ͼƬ����
     * @param bitmap ����
     * @param w Ҫ���ŵĿ��
     * @param h Ҫ���ŵĸ߶�
     * @return newBmp �� Bitmap����
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
 * ��BitmapתByte
 * @Author HEH
 * @EditTime 2010-07-19 ����11:45:56
 */
    public static byte[] Bitmap2Bytes(Bitmap bm){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
    }
    /**
 * ���ֽ����鱣��Ϊһ���ļ�
 * @Author HEH
 * @EditTime 2010-07-19 ����11:45:56
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
 * ����ָ����ͼ��·���ʹ�С����ȡ����ͼ 
 * �˷���������ô��� 
 *     1. ʹ�ý�С���ڴ�ռ䣬��һ�λ�ȡ��bitmapʵ����Ϊnull��ֻ��Ϊ�˶�ȡ��Ⱥ͸߶ȣ� 
 *        �ڶ��ζ�ȡ��bitmap�Ǹ��ݱ���ѹ������ͼ�񣬵����ζ�ȡ��bitmap����Ҫ������ͼ�� 
 *     2. ����ͼ����ԭͼ������û�����죬����ʹ����2.2�汾���¹���ThumbnailUtils��ʹ 
 *        ������������ɵ�ͼ�񲻻ᱻ���졣 
 * @param imagePath ͼ���·�� 
 * @param width ָ�����ͼ��Ŀ�� 
 * @param height ָ�����ͼ��ĸ߶� 
 * @return ���ɵ�����ͼ 
 */  
public Bitmap getImageThumbnail(String imagePath, int width, int height) {  
 Bitmap bitmap = null;  
 BitmapFactory.Options options = new BitmapFactory.Options();  
 options.inJustDecodeBounds = true;  
 // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull  
 bitmap = BitmapFactory.decodeFile(imagePath, options);  
 options.inJustDecodeBounds = false; // ��Ϊ false  
 // �������ű�  
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
 // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false  
 bitmap = BitmapFactory.decodeFile(imagePath, options);  
 // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����  
 bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
   ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
 return bitmap;  
}
/** 
 * ��ȡ��Ƶ������ͼ 
 * ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ�� 
 * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档 
 * @param videoPath ��Ƶ��·�� 
 * @param width ָ�������Ƶ����ͼ�Ŀ�� 
 * @param height ָ�������Ƶ����ͼ�ĸ߶ȶ� 
 * @param kind ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND�� 
 *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96 
 * @return ָ����С����Ƶ����ͼ 
 * videoThumbnail.setImageBitmap(getVideoThumbnail(videoPath, 60, 60,  
    MediaStore.Images.Thumbnails.MICRO_KIND));  
 */  
public Bitmap getVideoThumbnail(String videoPath, int width, int height,  
  int kind) {  
 Bitmap bitmap = null;  
 // ��ȡ��Ƶ������ͼ  
 bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
 System.out.println("w"+bitmap.getWidth());  
 System.out.println("h"+bitmap.getHeight());  
 bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
   ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
 return bitmap;  
}  
//��ת�����ַ���
public static String inputStream2String(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int i = -1;
    while ((i = is.read()) != -1) {
        baos.write(i);
    }
    return baos.toString();
}

// ��ת�����ļ�
public static void inputStream2File(InputStream is, String savePath) throws Exception {
    System.out.println("�ļ�����·��Ϊ:" + savePath);
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
 * �ļ�ת��Ϊ�ֽ�����

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
 * ���ֽ������ȡ����

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
 * �Ӷ����ȡһ���ֽ�����
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
 * ͼƬ����ѹ��
 * @param image
 * @return
 */
private Bitmap compressImage(Bitmap image) {  
	  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
    int options = 100;  
    while ( baos.toByteArray().length / 1024>100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
        baos.reset();//����baos�����baos  
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
        options -= 10;//ÿ�ζ�����10  
    }  
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
    return bitmap;  
}  
/**
 * ͼƬ��������Сѹ������������·����ȡͼƬ��ѹ������
 * @param srcPath
 * @return
 */
private Bitmap getimage(String srcPath) {  
    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
    newOpts.inJustDecodeBounds = true;  
    Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//��ʱ����bmΪ��  
      
    newOpts.inJustDecodeBounds = false;  
    int w = newOpts.outWidth;  
    int h = newOpts.outHeight;  
    //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
    float hh = 800f;//�������ø߶�Ϊ800f  
    float ww = 480f;//�������ÿ��Ϊ480f  
    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
    int be = 1;//be=1��ʾ������  
    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
        be = (int) (newOpts.outWidth / ww);  
    } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
        be = (int) (newOpts.outHeight / hh);  
    }  
    if (be <= 0)  
        be = 1;  
    newOpts.inSampleSize = be;//�������ű���  
    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
    bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
    return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
}  
/**
 * ͼƬ��������Сѹ������������BitmapͼƬѹ������
 * @param image
 * @return
 */
private Bitmap comp(Bitmap image) {  
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
    if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
        baos.reset();//����baos�����baos  
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
    }  
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
    newOpts.inJustDecodeBounds = true;  
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
    newOpts.inJustDecodeBounds = false;  
    int w = newOpts.outWidth;  
    int h = newOpts.outHeight;  
    //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ  
    float hh = 800f;//�������ø߶�Ϊ800f  
    float ww = 480f;//�������ÿ��Ϊ480f  
    //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��  
    int be = 1;//be=1��ʾ������  
    if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
        be = (int) (newOpts.outWidth / ww);  
    } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
        be = (int) (newOpts.outHeight / hh);  
    }  
    if (be <= 0)  
        be = 1;  
    newOpts.inSampleSize = be;//�������ű���  
    //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
    isBm = new ByteArrayInputStream(baos.toByteArray());  
    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
    return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
}  
/**
 * ��bitmapת����String
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
 * ����ͼƬ������ֵ
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
 * ����·�����ͻ�Ʋ�ѹ������bitmap������ʾ
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
 * ����·��ɾ��ͼƬ
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
 * ��ӵ�ͼ��
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
 * ��ȡ����ͼƬ��Ŀ¼
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
 * ��ȡ���� ��������ͼƬ�ļ�������
 * 
 * @return
 */
public static String getAlbumName() {
	return "sheguantong";
}
/*
ѹ��ͼƬ������ĳЩ�ֻ����սǶ���ת������
*/
//public static String compressImage(Context context,String filePath,String fileName,int q) throws FileNotFoundException {
//
//        Bitmap bm = getSmallBitmap(filePath);
//
//        int degree = readPictureDegree(filePath);
//
//        if(degree!=0){//��ת��Ƭ�Ƕ�
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
 * ѹ��ͼƬ 
 * @param bitmap ԴͼƬ 
 * @param width ��Ҫ�Ŀ�� 
 * @param height ��Ҫ�ĸ߶� 
 * @param isAdjust �Ƿ��Զ������ߴ�, trueͼƬ�Ͳ������죬false�ϸ�����ĳߴ�ѹ�� 
 * @return Bitmap 
 */  
public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {  
    // �����Ҫ�Ŀ�Ⱥ͸߶ȶ���ԴͼƬС���Ͳ�ѹ���ˣ�ֱ�ӷ���ԭͼ  
    if (bitmap.getWidth() < width && bitmap.getHeight() < height) {return bitmap;}  
    // ������Ҫ�ĳߴ羫ȷ����ѹ������, ������⣺public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode);  
    // scale��ʾҪ������С��λ, roundingMode��ʾ��δ�������С��λ��BigDecimal.ROUND_DOWN��ʾ�Զ�����  
    float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();  
    float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();  
    if (isAdjust) {// ������Զ�����������������ͼƬ������  
        sx = (sx < sy ? sx : sy);sy = sx;// �ĸ�����Сһ�㣬�����ĸ�����  
    }  
    Matrix matrix = new Matrix();  
    matrix.postScale(sx, sy);// ����api�еķ�������ѹ�����ʹ󹦸����  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * ��תͼƬ 
 * @param bitmap ԴͼƬ 
 * @param angle ��ת�Ƕ�(90Ϊ˳ʱ����ת,-90Ϊ��ʱ����ת) 
 * @return Bitmap 
 */  
public Bitmap rotate(Bitmap bitmap, float angle) {  
    Matrix matrix = new Matrix();    
    matrix.postRotate(angle);  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * �Ŵ����СͼƬ 
 * @param bitmap ԴͼƬ 
 * @param ratio �Ŵ����С�ı���������1��ʾ�Ŵ�С��1��ʾ��С 
 * @return Bitmap 
 */  
public Bitmap zoom(Bitmap bitmap, float ratio) {  
    if (ratio < 0f) {return bitmap;}  
    Matrix matrix = new Matrix();  
    matrix.postScale(ratio, ratio);  
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
}  
/** 
 * ��ͼƬ��ӡ�� 
 * @param bitmap ԴͼƬ 
 * @param text ӡ��ȥ���� 
 * @param param ��������ֱ�Ϊ����ɫ,��С,�Ƿ�Ӵ�,���x,���y; ���磺{color : 0xFF000000, size : 30, bold : true, x : 20, y : 20} 
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
 * ����logo(��ͼƬ��ˮӡ),  
 * @param bitmaps ԭͼƬ��ˮӡͼƬ 
 * @param left ���������� 
 * @param top �����������t 
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
 * ����һ��4λ������ֵ�ͼƬ��֤�� 
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
 * ���һ���������ɫ 
 * @param rate 
 * @return  
 */  
public int randomColor(int rate) {  
    int red = random.nextInt(256) / rate, green = random.nextInt(256) / rate, blue = random.nextInt(256) / rate;  
    return Color.rgb(red, green, blue);  
}  

/** 
 * ��������� 
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
 * ��������ŵ� 
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
 * ������ʵ��֤���ַ��� 
 * @return String 
 */  
public String getCheckCode() {  
    return checkCode;  
}  
}
