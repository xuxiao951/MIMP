package com.mimp.android.po;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class UpdateInfoParser {
	/* 
	 * ��pull�������������������ص�xml�ļ� (xml��װ�˰汾��) 
	 */  
	public static UpdateInfo getUpdataInfo(InputStream is) throws Exception{  
	    XmlPullParser  parser = Xml.newPullParser();    
	    parser.setInput(is, "utf-8");//���ý���������Դ   
	    int type = parser.getEventType();  
	    UpdateInfo info = new UpdateInfo();//ʵ��  
	    while(type != XmlPullParser.END_DOCUMENT ){  
	        switch (type) {  
	        case XmlPullParser.START_TAG:  
	            if("version".equals(parser.getName())){  
	                info.setVersion(parser.nextText()); //��ȡ�汾��  
	            }else if ("url".equals(parser.getName())){  
	                info.setUrl(parser.nextText()); //��ȡҪ������APK�ļ�  
	            }else if ("description".equals(parser.getName())){  
	                info.setDescription(parser.nextText()); //��ȡ���ļ�����Ϣ  
	            }else if ("UpdateLog".equals(parser.getName())){  
	                info.setUpdateLog(parser.nextText()); //��ȡ�ø���˵��  
	            }    
	            break;  
	        }  
	        type = parser.next();  
	    }  
	    return info;  
	}  
}
