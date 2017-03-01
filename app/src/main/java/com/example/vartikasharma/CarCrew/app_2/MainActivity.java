package com.example.vartikasharma.carcrew.app_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.open_enquiry_list)
    RecyclerView openEnquiryRecyclerView;
    private List<DataObject> openEnquiryItemList = new ArrayList<>();
    private OpenEnquiryListAdapter openEnquiryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        openEnquiryRecyclerView.setLayoutManager(mLayoutManager);

        fetchItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refresh list
        fetchItems();
    }

    private void fetchItems() {
        String firebaseOpenDataUri = Conf.firebaseUserOpenQueries();
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(firebaseOpenDataUri);

        openEnquiryItemList.clear();
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    DataObject dataobject = data.getValue(DataObject.class);
                    if (dataobject != null) {
                        openEnquiryItemList.add(dataobject);
                    }
                }
                openEnquiryListAdapter = new OpenEnquiryListAdapter(MainActivity.this, openEnquiryItemList);
                openEnquiryRecyclerView.setAdapter(openEnquiryListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.toString());
            }
        });
    }
}
