package com.mimp.android.po;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {
	private final static int CacheSize = 1024;
	//压缩保存string new sun.misc.BASE64Encoder().encodeBuffer(byte[] b)
	//解压缩new BASE64Decoder().decodeBuffer 方法将字符串转换为字节，然后解压就可以了。
	/**
	 * 字符串压缩
	 * @param str
	 * @return
	 */
	public static final byte[] compress(String str)  
	{  
		 if(str == null)  
		 return null;  
		    
		 byte[] compressed;  
		 ByteArrayOutputStream out = null;  
		 ZipOutputStream zout = null;  
		    
		 try  
		 {  
			  out = new ByteArrayOutputStream();  
			  zout = new ZipOutputStream(out);  
			  zout.putNextEntry(new ZipEntry("0"));  
			  zout.write(str.getBytes());  
			  zout.closeEntry();  
			  compressed = out.toByteArray();  
		 }  
		 catch(IOException e)  
		 {  
			 compressed = null;  
		 }  
		 finally  
		 {  
			  if(zout != null)  
			  {  
				  try{zout.close();  
			  }  
			catch(IOException e){}  
		 }  
		 }  
		 if(out != null)  
		 {  
		  try  
		  {  
		   out.close();  
		  }  
		  catch(IOException e){}  
		 }  
		 return compressed;  
	}  
	/**
	 * 字符串解压缩
	 * @param compressed
	 * @return
	 */
	public static final String decompress(byte[] compressed) {  
		if(compressed == null)  
		return null;  
		   
		ByteArrayOutputStream out = null;  
		ByteArrayInputStream in = null;  
		ZipInputStream zin = null;  
		String decompressed;  
		try {  
		out = new ByteArrayOutputStream();  
		in = new ByteArrayInputStream(compressed);  
		zin = new ZipInputStream(in);  
		ZipEntry entry = zin.getNextEntry();  
		byte[] buffer = new byte[1024];  
		int offset = -1;  
		while((offset = zin.read(buffer)) != -1) {  
		out.write(buffer, 0, offset);  
		}  
		decompressed = out.toString();  
		} catch (IOException e) {  
		decompressed = null;  
		} finally {  
		if(zin != null) {  
		try {zin.close();} catch(IOException e) {}  
		}  
		if(in != null) {  
		try {in.close();} catch(IOException e) {}  
		}  
		if(out != null) {  
		try {out.close();} catch(IOException e) {}  
		}  
		}  
		   
		return decompressed;  
	}  


	/***
	 * 压缩Zip
	 *
	 * @param data
	 * @return
	 */
	public static byte[] zipByte(byte[] data) {
	    Deflater compresser = new Deflater();
	    compresser.reset();
	    compresser.setInput(data);
	    compresser.finish();
	    byte result[] = new byte[0];
	    ByteArrayOutputStream o = new ByteArrayOutputStream(1);
	    try {
	        byte[] buf = new byte[CacheSize];
	        int got = 0;
	        while (!compresser.finished()) {
	            got = compresser.deflate(buf);
	            o.write(buf, 0, got);
	        }

	        result = o.toByteArray();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            o.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        compresser.end();
	    }
	    return result;
	}

	/***
	 * 压缩String
	 *
	 * @param data
	 * @return
	 */
	public static byte[] zipString(String data) {
	    byte[] input = new byte[0];
	    try {
	        input = data.getBytes("UTF-8");
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	        return null;
	    }

	    byte[] result = zipByte(input);
	    return result;
	}

	/***
	 * 解压Zip
	 *
	 * @param data
	 * @return
	 */
	public static byte[] unZipByte(byte[] data) {
	    Inflater decompresser = new Inflater();
	    decompresser.setInput(data);
	    byte result[] = new byte[0];
	    ByteArrayOutputStream o = new ByteArrayOutputStream(1);
	    try {
	        byte[] buf = new byte[CacheSize];
	        int got = 0;
	        while (!decompresser.finished()) {
	            got = decompresser.inflate(buf);
	            o.write(buf, 0, got);
	        }
	        result = o.toByteArray();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            o.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        decompresser.end();
	    }
	    return result;
	}

	/***
	 * 解压Zip数据为String
	 *
	 * @param data
	 * @return
	 */
	public static String unZipByteToString(byte[] data) {
	    byte[] result = unZipByte(data);
	    String outputString = null;
	    try {
	        outputString = new String(result, 0, result.length, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }
	    return outputString;
	}
}
