package com.mimp.android.ch0;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TsBillListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private ArrayList<Map<String,Object>> data=new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> itemData;
	public TsBillListAdapter(Context context,List<Map<String, Object>> listData) {
		//����context�����ļ��ز���  
        this.inflater = LayoutInflater.from(context);  
        //����������ݱ�����mData��  
        this.itemData=listData;  
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 //�ڴ�������������������ݼ��е���Ŀ��  
        return itemData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		 //��ȡ���б�����ָ��������Ӧ����id  
        return position;  
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	//��ȡһ�������ݼ���ָ����������ͼ����ʾ����  
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;  
        
        //�������convertViewΪ�գ�����Ҫ����View  
        if (convertView == null) {  
            //�Զ����һ������������convertview  
            holder=new ViewHolder();   
              
            //�����Զ����Item���ּ��ز���  
            convertView = inflater.inflate(R.layout.activity_fms_listitem, null);  
            
            holder.pic = (ImageView)convertView.findViewById(R.id.pic);  
            holder.BillID =  (TextView)convertView.findViewById(R.id.tvBillID); 
            holder.UserName =  (TextView)convertView.findViewById(R.id.tvUserName); 
            holder.ShootingDate =  (TextView)convertView.findViewById(R.id.tvShootingDate); 
            holder.TroubleArea =  (TextView)convertView.findViewById(R.id.tvTroubleArea); 
            holder.TroubleType =  (TextView)convertView.findViewById(R.id.tvTroubleType); 
            holder.TroubleDetail =  (TextView)convertView.findViewById(R.id.tvTroubleDetail); 
            holder.FlowStatus =  (TextView)convertView.findViewById(R.id.tvFlowStatus); 
            //�����úõĲ��ֱ��浽�����У�������������Tag��Ա���淽��ȡ��Tag  
            convertView.setTag(holder);  
              
        }else {  
              
            holder = (ViewHolder)convertView.getTag();  
        }  
          
          
//       holder.pic.setBackgroundResource((Integer)itemData.get(position).get("pic"));      
        holder.pic.setImageResource((Integer)itemData.get(position).get("pic"));
       holder.BillID.setText((String)itemData.get(position).get("BillID"));
       holder.UserName.setText((String)itemData.get(position).get("Reporter"));
       holder.ShootingDate.setText((String)itemData.get(position).get("ReportDate"));
       holder.TroubleArea.setText((String)itemData.get(position).get("TroubleAreaName"));
       holder.TroubleType.setText((String)itemData.get(position).get("TroubleTypeName"));
       holder.TroubleDetail.setText((String)itemData.get(position).get("ReportRemark"));
       holder.FlowStatus.setText((String)itemData.get(position).get("FlowTypeName"));

          
        return convertView;  
	}
	   //ViewHolder��̬��  
    public final class ViewHolder{  
        public ImageView pic;  
        public TextView BillID;  
        public TextView UserName;  
        public TextView ShootingDate;  
        public TextView TroubleArea;  
        public TextView TroubleType;  
        public TextView TroubleDetail;  
        public TextView FlowStatus;  
    } 
}
