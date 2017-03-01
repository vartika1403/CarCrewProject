package com.example.vartikasharma.carcrew.app_2;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vartikasharma.carcrew.Conf;
import com.example.vartikasharma.carcrew.DataObject;
import com.example.vartikasharma.carcrew.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OpenEnquiryListAdapter extends RecyclerView.Adapter<OpenEnquiryListAdapter.MyViewHolder> {
    private static final String LOG_TAG = OpenEnquiryListAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private List<DataObject> objectList;

    public OpenEnquiryListAdapter(Context context, List<DataObject> objectList) {
        this.context = context;
        this.objectList = objectList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.open_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final DataObject dataObject = objectList.get(position);
        // refresh data for recycler view
        refreshDataForRecyclerView(holder);
        // set data
        holder.openEnquiryId.setText("Enquiry Id: " + dataObject.getEnquiry_Item_ID());
        holder.openGarageName.setText("Garage Name: " + dataObject.getGarage_Name());
        holder.partName.setText(dataObject.getPart_Name());
        holder.carName.setText(dataObject.getCar_Name());
        holder.inStock.setText(" " + dataObject.getQuantity_In_Stock());
        if (dataObject.getPart_Flag() == 2) {
            holder.textOesStatus.setText("OES");
        } else {
            holder.textOesStatus.setText("OEM");
        }
        if (dataObject.getEnquiry_Item_Status() == 0 || dataObject.getEnquiry_Item_Status() == 3) {
            holder.textOpenStatus.setText("OPEN");
        } else {
            holder.textOpenStatus.setText("CLOSE");
        }

        holder.textBrandName.getBackground().setColorFilter(Color.parseColor("#979797"), PorterDuff.Mode.SRC_IN);
        if (dataObject.getBrand_Name() != null && !dataObject.getBrand_Name().isEmpty()) {
            holder.textBrandName.setText(dataObject.getBrand_Name());
            holder.textBrandName.setEnabled(false);
        }
        final List<EditText> mrpValue = new ArrayList<>();
        mrpValue.clear();
        for (int i = 0; i < 3; i++) {
            loadVendorList(holder.vendorDetails, mrpValue);
        }


        holder.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(holder.textBrandName.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                String brandname = holder.textBrandName.getText().toString();
                String mrpValueText = mrpValue.get(0).getText().toString();
                Double minMrpValue = 0.0;
                if (!mrpValueText.isEmpty()) {
                    minMrpValue = Double.parseDouble(mrpValueText);
                }
                for (int i = 1; i < mrpValue.size(); i++) {
                    String valueText = mrpValue.get(i).getText().toString();
                    Double value = 0.0;
                    if (!valueText.isEmpty()) {
                        value = Double.parseDouble(valueText);
                    }
                    if (minMrpValue != 0.0 && value != 0.0 && minMrpValue > value) {
                        minMrpValue = value;
                    } else if (minMrpValue == 0.0 && value != 0.0) {
                        minMrpValue = value;
                    }
                }
                //this call will update the existing data list with new data
                if (minMrpValue != 0.0 && !brandname.isEmpty()) {
                    dataObject.setBrand_Name(brandname);
                    dataObject.setPart_MRP(minMrpValue);
                    holder.submitButton.setText(R.string.text_submit_successfully);
                    dataObject.setEnquiry_Item_Status(1);
                    holder.textOpenStatus.setText("CLOSE");
                    updateDataValueForEnquiries(dataObject, position);
                } else {
                    Log.e(LOG_TAG, "please fill the values");
                    Toast.makeText(context, R.string.toast_error_msg_fill_values, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }

    private void refreshDataForRecyclerView(MyViewHolder holder) {
        holder.openEnquiryId.invalidate();
        holder.openEnquiryId.setText("");
        holder.openGarageName.invalidate();
        holder.openGarageName.setText("");
        holder.partName.invalidate();
        holder.partName.setText("");
        holder.carName.invalidate();
        holder.carName.setText("");
        holder.inStock.invalidate();
        holder.inStock.setText("");
        holder.textOesStatus.invalidate();
        holder.textOesStatus.setText("");
        holder.textOpenStatus.invalidate();
        holder.textOpenStatus.setText("");
        holder.textBrandName.invalidate();
        holder.textBrandName.setText("");
        holder.textBrandName.setEnabled(true);
        holder.vendorDetails.invalidate();
        holder.vendorDetails.removeAllViews();
        holder.submitButton.invalidate();
        holder.submitButton.setText(R.string.text_submit);
    }

    private void removeDataFromOpenList(final DataObject dataObject, final int position) {
        String firebaseOpenDataURi = Conf.firebaseUserOpenQueries();
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(firebaseOpenDataURi);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        DataObject data = ds.getValue(DataObject.class);
                        int enquiryItemId = data.getEnquiry_Item_ID();
                        String key = ds.getKey();
                        if (enquiryItemId == dataObject.getEnquiry_Item_ID()) {
                            databaseRef.child(key).removeValue();
                            objectList.remove(dataObject);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "error in getting data");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.toString());
            }
        });
    }

    private void updateDataValueForEnquiries(final DataObject openListObject, final int position) {
        String firebaseDataUri = Conf.firebaseUserDataURI();
        final DatabaseReference dataRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(firebaseDataUri);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        DataObject data = ds.getValue(DataObject.class);
                        int enquiryItemId = data.getEnquiry_Item_ID();
                        String key = ds.getKey();
                        if (enquiryItemId == openListObject.getEnquiry_Item_ID()) {
                            dataRef.child(key).setValue(openListObject);
                            removeDataFromOpenList(openListObject, position);
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "error in getting data");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.toString());
            }
        });
    }

    private void loadVendorList(LinearLayout vendorDetails, final List<EditText> mrpValue) {
        View view = LayoutInflater.from(context).inflate(R.layout.vendor_detail_layout, vendorDetails, false);
        final EditText mrp_value = (EditText) view.findViewById(R.id.mrp_value);
        mrpValue.add(mrp_value);
        vendorDetails.addView(view);
    }

    @Override
    public int getItemCount() {
        return objectList.size();
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
}
