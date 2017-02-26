package com.example.vartikasharma.carcrew.app_1;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
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
    private static final String SERVER_HOST = "https://carcrew-project.firebaseio.com/";
    private static final String URL = "https://firebasestorage.googleapis.com/v0/b/carcrew-project.appspot.com/o/download.json?alt=media&token=8666cc50-ec7e-4319-9208-c936d5e66fe5";
    private List<DataObject> dataObject = new ArrayList<>();
    private List<DataObject> listItem = new ArrayList<>();
    private List<DataObject> openListItem = new ArrayList<>();
    private EnquiryListAdapter enquiryListAdapter;


    @BindView(R.id.enquiry_list)
    public ListView enquiryList;
    @BindView(R.id.confirm_order)
    Button confirmOrder;
    @BindView(R.id.toolbar)
    Toolbar toolbarApp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarApp1);

        initToolBar();

        if (dataObject == null) {
            Log.i(LOG_TAG, "firebase data, " + FirebaseDatabase.getInstance().getReference().child("data"));
            try {
                fetchData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            fetchDataFromFirebase();
        }
    }

    private void initToolBar() {

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.confirm_order)
    void confirmOrder() {
        saveDataEnteredToFirebase();
        Intent intent = new Intent("com.example.vartikasharma.carcrew.app_2.intent.action.Launch");
        startActivity(intent);

    }

    private void saveDataEnteredToFirebase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Log.i(LOG_TAG, "ref, " + ref);
        DatabaseReference usersRef = ref.child("open");
        for (int i =0; i < openListItem.size(); i++) {
            DatabaseReference reference = usersRef.push();
            Log.i(LOG_TAG, "refernce, " + reference);
            reference.setValue(openListItem.get(i));
        }
    }

    private void fetchDataFromFirebase() {
     String firebaseDataUri = Conf.firebaseUserDataURI();
        Log.i(LOG_TAG, "firebaseDataUri, " + firebaseDataUri);
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(firebaseDataUri);
        Log.i(LOG_TAG, "dataRef, " + dataRef);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long dataCount = dataSnapshot.getChildrenCount();
                Log.i(LOG_TAG, "dataCount, " + dataCount);

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.i(LOG_TAG, "data value, " + data.getValue());
                    Log.i(LOG_TAG, "data value, " + data.getKey());

                    DataObject item = data.getValue(DataObject.class);
                    if (item != null) {
                        listItem.add(item);
                    }
                }

                enquiryListAdapter = new EnquiryListAdapter(MainActivity.this, listItem);
                enquiryList.setAdapter(enquiryListAdapter);
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
                        jsonArray  = json.getJSONArray("data");
                        Log.i(LOG_TAG, "jsonArray," + jsonArray);
                        Log.i(LOG_TAG, "length, " + jsonArray.length());
                        for (int i = 0 ; i < jsonArray.length(); i++) {
                            dataObject = Arrays.asList(gson.fromJson(jsonArray.toString(), DataObject[].class));
                            Log.i(LOG_TAG, "dataobject, " + dataObject);
                            Log.i(LOG_TAG, "first dataObject, " + dataObject.get(i).getCar_Name());
                            if (dataObject.get(i).getCar_Name() == null || dataObject.get(i).getBrand_Name() == null || dataObject.get(i).getFinal_Price() == 0.0 || dataObject.get(i).getPart_Name().isEmpty() || dataObject.get(i).getQuantity_In_Stock() == 0){
                                openListItem.add(dataObject.get(i));
                            }
                        }

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
        Log.i(LOG_TAG, "ref, " + ref);
        DatabaseReference usersRef = ref.child("data");
        for (int i =0; i < dataObject.size(); i++) {
            DatabaseReference reference = usersRef.push();
            Log.i(LOG_TAG, "refernce, " + reference);
            reference.setValue(dataObject.get(i));
            Log.i(LOG_TAG, "referncevalue, " + reference);

        }
    }
}
