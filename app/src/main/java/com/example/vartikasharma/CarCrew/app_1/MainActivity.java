package com.example.vartikasharma.carcrew.app_1;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vartikasharma.carcrew.Conf;
import com.example.vartikasharma.carcrew.DataObject;
import com.example.vartikasharma.carcrew.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String URL = "https://firebasestorage.googleapis.com/v0/b/carcrew-project.appspot.com/o/download.json?alt=media&token=8666cc50-ec7e-4319-9208-c936d5e66fe5";
    @BindView(R.id.enquiry_list)
    public RecyclerView enquiryList;
    @BindView(R.id.confirm_order)
    Button confirmOrder;
    @BindView(R.id.toolbar)
    Toolbar toolbarApp1;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private List<DataObject> listItem = new ArrayList<>();
    private List<DataObject> openListItem = new ArrayList<>();
    private EnquiryListAdapter enquiryListAdapter;
    private boolean isOpenListItemPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarApp1);

        fetchDataFromFirebase();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.confirm_order)
    void confirmOrder() {
        // save data for the first time only
        Log.i(LOG_TAG, " item data value, " + FirebaseDatabase.getInstance().getReference().child("open"));
        FirebaseDatabase.getInstance().getReference().child("open").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    saveDataEnteredToFirebase();
                } else {
                    Toast.makeText(MainActivity.this, "Fill the remaining data for enquieries", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveDataEnteredToFirebase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = ref.child("open");
        for (int i = 0; i < openListItem.size(); i++) {
            DatabaseReference reference = usersRef.push();
            reference.setValue(openListItem.get(i));
        }
        Toast.makeText(MainActivity.this, "Fill the remaining data for enquieries", Toast.LENGTH_LONG).show();
    }

    private void fetchDataFromFirebase() {
        String firebaseDataUri = Conf.firebaseUserDataURI();
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(firebaseDataUri);

        openListItem.clear();
        listItem.clear();
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        DataObject item = data.getValue(DataObject.class);
                        if (item != null) {
                            listItem.add(item);
                        }
                        if (item != null) {
                            if (item.getCar_Name() == null || item.getBrand_Name() == null ||
                                    item.getPart_Name().isEmpty()
                                    || item.getQuantity_In_Stock() == 0) {
                                openListItem.add(item);
                            }
                        }
                    }

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    enquiryList.setLayoutManager(layoutManager);
                    enquiryListAdapter = new EnquiryListAdapter(MainActivity.this, listItem);
                    enquiryList.setAdapter(enquiryListAdapter);
                } else {
                    // api call to fetch data from URL, if data nt present in firebase
                    try {
                        fetchData();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e(LOG_TAG, "error in getting data");
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),
                            "Sorry could' nt get the data", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchData() throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(), "Can't fetch data", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String jsonData = response.body().string();
                Log.i(LOG_TAG, jsonData);
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    JSONObject json = null;
                    JSONArray jsonArray;
                    try {
                        json = new JSONObject(jsonData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert json != null;
                        jsonArray = json.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            listItem = Arrays.asList(gson.fromJson(jsonArray.toString(), DataObject[].class));
                            if (listItem.get(i).getCar_Name() == null ||
                                    listItem.get(i).getBrand_Name() == null ||
                                    listItem.get(i).getPart_Name().isEmpty() ||
                                    listItem.get(i).getQuantity_In_Stock() == 0) {
                                openListItem.add(listItem.get(i));
                            }
                        }
                        // write value to the firbase in data uri
                        addValueToFirebase();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.i(LOG_TAG, "response is not successful");
                }

            }
        });
    }

    private void addValueToFirebase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = ref.child("data");
        for (int i = 0; i < listItem.size(); i++) {
            DatabaseReference reference = usersRef.push();
            reference.setValue(listItem.get(i));
        }
    }
}

