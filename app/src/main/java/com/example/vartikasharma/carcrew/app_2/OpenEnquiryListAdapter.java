package com.example.vartikasharma.carcrew.app_2;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vartikasharma.carcrew.Conf;
import com.example.vartikasharma.carcrew.DataObject;
import com.example.vartikasharma.carcrew.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OpenEnquiryListAdapter extends RecyclerView.Adapter<OpenEnquiryListAdapter.MyViewHolder>{
    private static final String LOG_TAG = OpenEnquiryListAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private List<DataObject> objectList;

    public OpenEnquiryListAdapter(Context context, List<DataObject> objectList) {
        this.context = context;
        this.objectList = objectList;
        inflater = LayoutInflater.from(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView openEnquiryId;
        private TextView openGarageName;
        private TextView partName;
        private TextView carName;
        private TextView inStock;
        private Button textOesStatus;
        private Button textOpenStatus;
        private Button submitButton;
        private EditText textBrandName;
        private LinearLayout vendorDetails;

        public MyViewHolder(View view) {
            super(view);
            openEnquiryId = (TextView) view.findViewById(R.id.open_enquiry_id);
            openGarageName = (TextView) view.findViewById(R.id.open_garage_name);
            partName = (TextView) view.findViewById(R.id.part_name);
            carName = (TextView) view.findViewById(R.id.car_name);
            inStock = (TextView) view.findViewById(R.id.in_stock);
            textOesStatus = (Button) view.findViewById(R.id.text_oem_status_button);
            textOpenStatus = (Button) view.findViewById(R.id.text_open_status_button);
            textBrandName = (EditText) view.findViewById(R.id.brand_name_text);
            vendorDetails = (LinearLayout) view.findViewById(R.id.vendor_details);
            submitButton = (Button) view.findViewById(R.id.submit_button);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.open_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DataObject dataObject = objectList.get(position);
        holder.openEnquiryId.setText("Enquiry Id: " + dataObject.getEnquiry_Item_ID());
        holder.openGarageName.setText("Garage Name: " + dataObject.getGarage_Name());
        holder.partName.setText(dataObject.getPart_Name());
        holder.carName.setText(dataObject.getCar_Name());
        holder.inStock.setText(" " + dataObject.getQuantity_In_Stock());
        holder.textOesStatus.setText(" " + dataObject.getPart_Flag());
        if (dataObject.getEnquiry_Item_Status() == 0 || dataObject.getEnquiry_Item_Status() == 3) {
            holder.textOpenStatus.setText("OPEN");
        } else {
            holder.textOpenStatus.setText("CLOSE");
        }

        holder.vendorDetails.invalidate();
        holder.vendorDetails.removeAllViews();
        final List<EditText> mrpValue = new ArrayList<>();
        mrpValue.clear();
        Log.i(LOG_TAG, "mrp value, " + mrpValue.size());
        for (int i =0 ; i < 3; i++) {
            loadVendorList(holder.vendorDetails, mrpValue);
        }

        holder.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String brandname = holder.textBrandName.getText().toString();
                Log.i(LOG_TAG, "brand name" + brandname);
                Log.i(LOG_TAG, "mrp value size, " + mrpValue.size());
                Double minMrpValue = Double.parseDouble(mrpValue.get(0).getText().toString());
                if (!mrpValue.isEmpty()) {
                    for (int i = 1; i< mrpValue.size(); i++) {
                        Double value = Double.parseDouble(mrpValue.get(i).getText().toString());
                        Log.i(LOG_TAG, "value , " + value);
                        if (minMrpValue > value) {
                            minMrpValue = value;
                            Log.i(LOG_TAG, "minMrpValue , " + minMrpValue);
                        }
                    }
                    updateDataValueForEnquiries(minMrpValue, brandname, dataObject);
                }
            }
        });
    }

    private void updateDataValueForEnquiries(final Double min, final String brandname, final DataObject openListObject) {
        String firebaseDataUri = Conf.firebaseUserDataURI();
        Log.i(LOG_TAG, "firebaseDataUri, " + firebaseDataUri);
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(firebaseDataUri);

            if (min != 0.0) {
                Log.i(LOG_TAG, "min value, " + min);
                openListObject.setPart_MRP(min);
            }
            if (!brandname.isEmpty()) {
                Log.i(LOG_TAG, "brand value, " + brandname);
                openListObject.setBrand_Name(brandname);
            }
        Log.i(LOG_TAG, "enquiry id , " + openListObject.getEnquiry_Item_ID());
        final Query queryRef =  dataRef.orderByChild("enquiry_item_id").equalTo(openListObject.getEnquiry_Item_ID());
            Log.i(LOG_TAG, "queryRef, " + queryRef.getRef());
            queryRef.getRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        DataObject data = ds.getValue(DataObject.class);
                        int enquiryItemId = data.getEnquiry_Item_ID();
                        Log.i(LOG_TAG, "enquiry itemid, " + enquiryItemId);
                        String key = ds.getKey();
                        Log.i(LOG_TAG, "key, " + key);
                        if (enquiryItemId == openListObject.getEnquiry_Item_ID()){
                            Log.i(LOG_TAG, "query key, " +  queryRef.getRef().child(key));
                            queryRef.getRef().child(key).setValue(openListObject);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    private void loadVendorList(LinearLayout vendorDetails, final List<EditText> mrpValue) {
        View view = LayoutInflater.from(context).inflate(R.layout.vendor_detail_layout, vendorDetails, false);
        EditText vendor_name = (EditText)view.findViewById(R.id.vendor_name);
        final EditText mrp_value = (EditText) view.findViewById(R.id.mrp_value);
        EditText cp_value = (EditText) view.findViewById(R.id.cp_value);
        EditText sp_value = (EditText) view.findViewById(R.id.sp_value);
        mrpValue.add(mrp_value);
        vendorDetails.addView(view);
    }


    @Override
    public int getItemCount() {
        return objectList.size();
    }
}
