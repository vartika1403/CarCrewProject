package com.example.vartikasharma.carcrew.app_1;


import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.vartikasharma.carcrew.DataObject;
import com.example.vartikasharma.carcrew.R;

import java.util.List;

public class EnquiryListAdapter extends RecyclerView.Adapter<EnquiryListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<DataObject> objects;

    public EnquiryListAdapter(Context context, List<DataObject> objects) {
        Context context1 = context;
        this.objects = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataObject dataObject = objects.get(position);
        holder.partName.setText(dataObject.getPart_Name());
        holder.brandName.setText(dataObject.getBrand_Name());
        holder.carName.setText(dataObject.getCar_Name());
        holder.price.setText(" " + dataObject.getPart_MRP());
        holder.inStock.setText(" " + dataObject.getQuantity_In_Stock());
        if (dataObject.getPart_Flag() == 2) {
            holder.oemStatus.setText("OES");
        } else {
            holder.oemStatus.setText("OEM");
        }
        if (dataObject.getEnquiry_Item_Status() == 0 || dataObject.getEnquiry_Item_Status() == 3) {
            holder.openStatus.setText("OPEN");
        } else {
            holder.openStatus.setText("CLOSE");
        }
        holder.enquiryId.setText("Enquiry ID: " + dataObject.getEnquiry_Item_ID());
        holder.garageName.setText("Garage Name: " + dataObject.getGarage_Name());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView enquiryId;
        TextView garageName;
        TextView partName;
        TextView brandName;
        TextView carName;
        TextView price;
        TextView inStock;
        Button oemStatus;
        Button openStatus;

        public ViewHolder(View view) {
            super(view);

            partName = (TextView) view.findViewById(R.id.text_part_name);
            brandName = (TextView) view.findViewById(R.id.text_brand_name);
            carName = (TextView) view.findViewById(R.id.text_car_name);
            price = (TextView) view.findViewById(R.id.value);
            inStock = (TextView) view.findViewById(R.id.text_in_stock);
            oemStatus = (Button) view.findViewById(R.id.oem_buttom);
            openStatus = (Button) view.findViewById(R.id.open_button);
            enquiryId = (TextView) view.findViewById(R.id.enquiry_id);
            garageName = (TextView) view.findViewById(R.id.garage_name);
        }
    }
}


