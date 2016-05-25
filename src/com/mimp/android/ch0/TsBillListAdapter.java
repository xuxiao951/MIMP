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
		//根据context上下文加载布局  
        this.inflater = LayoutInflater.from(context);  
        //将传入的数据保存在mData中  
        this.itemData=listData;  
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 //在此适配器中所代表的数据集中的条目数  
        return itemData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		 //获取在列表中与指定索引对应的行id  
        return position;  
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	//获取一个在数据集中指定索引的视图来显示数据  
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;  
        
        //如果缓存convertView为空，则需要创建View  
        if (convertView == null) {  
            //自定义的一个类用来缓存convertview  
            holder=new ViewHolder();   
              
            //根据自定义的Item布局加载布局  
            convertView = inflater.inflate(R.layout.activity_fms_listitem, null);  
            
            holder.pic = (ImageView)convertView.findViewById(R.id.pic);  
            holder.BillID =  (TextView)convertView.findViewById(R.id.tvBillID); 
            holder.UserName =  (TextView)convertView.findViewById(R.id.tvUserName); 
            holder.ShootingDate =  (TextView)convertView.findViewById(R.id.tvShootingDate); 
            holder.TroubleArea =  (TextView)convertView.findViewById(R.id.tvTroubleArea); 
            holder.TroubleType =  (TextView)convertView.findViewById(R.id.tvTroubleType); 
            holder.TroubleDetail =  (TextView)convertView.findViewById(R.id.tvTroubleDetail); 
            holder.FlowStatus =  (TextView)convertView.findViewById(R.id.tvFlowStatus); 
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag  
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
	   //ViewHolder静态类  
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
