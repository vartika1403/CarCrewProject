package com.example.vartikasharma.carcrew.app_1;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.vartikasharma.carcrew.DataObject;
import com.example.vartikasharma.carcrew.R;

import java.util.List;

public class EnquiryListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<DataObject> objects;

    public EnquiryListAdapter(Context context, List<DataObject> objects) {
        this.context = context;
        this.objects = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_layout, null, false);
            holder = new ViewHolder();

            holder.partName = (TextView) convertView.findViewById(R.id.text_part_name);
            holder.brandName = (TextView) convertView.findViewById(R.id.text_brand_name);
            holder.carName = (TextView) convertView.findViewById(R.id.text_car_name);
            holder.price = (TextView) convertView.findViewById(R.id.value);
            holder.inStock = (TextView) convertView.findViewById(R.id.text_in_stock);
            holder.oemStatus = (Button) convertView.findViewById(R.id.oem_buttom);
            holder.openStatus = (Button) convertView.findViewById(R.id.open_button);
            holder.enquiryId = (TextView) convertView.findViewById(R.id.enquiry_id);
            holder.garageName = (TextView) convertView.findViewById(R.id.garage_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.partName.setText(objects.get(i).getPart_Name());
        holder.brandName.setText(objects.get(i).getBrand_Name());
        holder.carName.setText(objects.get(i).getCar_Name());
        holder.price.setText(" " + objects.get(i).getFinal_Price());
        holder.inStock.setText(" " + objects.get(i).getQuantity_In_Stock());
        holder.oemStatus.setText(" " + objects.get(i).getPart_Flag());
        if (objects.get(i).getEnquiry_Item_Status() == 0 || objects.get(i).getEnquiry_Item_Status() == 3) {
            holder.openStatus.setText("OPEN");
        } else {
            holder.openStatus.setText("CLOSE");
        }
       // holder.openStatus.setText(" " + objects.get(i).getEnquiry_Item_Status());
        holder.enquiryId.setText("Enquiry ID: " + objects.get(i).getEnquiry_Item_ID());
        holder.garageName.setText("Garage Name: " + objects.get(i).getGarage_Name());

        return convertView;
    }

    private class ViewHolder {
        TextView enquiryId;
        TextView garageName;
        TextView partName;
        TextView brandName;
        TextView carName;
        TextView price;
        TextView inStock;
        Button oemStatus;
        Button openStatus;
    }
}


