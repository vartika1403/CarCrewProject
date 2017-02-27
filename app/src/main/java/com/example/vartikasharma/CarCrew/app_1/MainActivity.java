package com.example.vartikasharma.carcrew.app_1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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
    private List<DataObject> dataObject = new ArrayList<>();
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
                    Log.i(LOG_TAG, " item data value ok , " + FirebaseDatabase.getInstance().getReference().child("open"));
                    saveDataEnteredToFirebase();

                } else {
                    openDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openDialog() {
        Log.i(LOG_TAG, " no clicked , " + "yes");

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Enquiers...");

        // Setting Dialog Message
        alertDialog.setMessage("Do you want to fill the open enquieries");

        // Setting Icon to Dialog
      //  alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                // Write your code here to invoke YES event
                dialog.dismiss();
                dialog.cancel();
                //Intent intent = new Intent("com.example.vartikasharma.carcrew.app_2.intent.action.Launch");
                Intent intent = new Intent(MainActivity.this, com.example.vartikasharma.carcrew.app_2.MainActivity.class);
                startActivity(intent);
                //finish();
                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private void saveDataEnteredToFirebase() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Log.i(LOG_TAG, "ref, " + ref);
        DatabaseReference usersRef = ref.child("open");
        Log.i(LOG_TAG, "open list item count, " + openListItem.size());
        for (int i = 0; i < openListItem.size(); i++) {
            DatabaseReference reference = usersRef.push();
            Log.i(LOG_TAG, "refernce, " + reference);
            reference.setValue(openListItem.get(i));
        }
        openDialog();
    }

    private void fetchDataFromFirebase() {
        String firebaseDataUri = Conf.firebaseUserDataURI();
        Log.i(LOG_TAG, "firebaseDataUri, " + firebaseDataUri);
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().
                getReferenceFromUrl(firebaseDataUri);
        Log.i(LOG_TAG, "dataRef, " + dataRef);

        openListItem.clear();
        listItem.clear();
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    long dataCount = dataSnapshot.getChildrenCount();
                    Log.i(LOG_TAG, "dataCount, " + dataCount);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.i(LOG_TAG, "data value, " + data.getValue());
                        Log.i(LOG_TAG, "data value, " + data.getKey());

                        DataObject item = data.getValue(DataObject.class);
                        if (item != null) {
                            listItem.add(item);
                        }
                        if (item != null) {
                            if (item.getCar_Name() == null || item.getBrand_Name() == null ||
                                    item.getPart_Name().isEmpty()
                                    || item.getQuantity_In_Stock() == 0) {
                                Log.i(LOG_TAG, "open item, " + item.getEnquiry_Item_ID());
                                openListItem.add(item);
                            }
                        }
                    }

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    enquiryList.setLayoutManager(layoutManager);
                    enquiryListAdapter = new EnquiryListAdapter(MainActivity.this, listItem);
                    enquiryList.setAdapter(enquiryListAdapter);
                } else {
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
                        Log.i(LOG_TAG, "jsonArray," + jsonArray);
                        Log.i(LOG_TAG, "length, " + jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            listItem = Arrays.asList(gson.fromJson(jsonArray.toString(), DataObject[].class));
                            Log.i(LOG_TAG, "dataobject, " + listItem);
                            Log.i(LOG_TAG, "first dataObject, " + listItem.get(i).getCar_Name());
                            if (listItem.get(i).getCar_Name() == null ||
                                    listItem.get(i).getBrand_Name() == null ||
                                    listItem.get(i).getPart_Name().isEmpty() ||
                                    listItem.get(i).getQuantity_In_Stock() == 0) {
                                openListItem.add(listItem.get(i));
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
        for (int i = 0; i < listItem.size(); i++) {
            DatabaseReference reference = usersRef.push();
            Log.i(LOG_TAG, "refernce, " + reference);
            reference.setValue(listItem.get(i));
            Log.i(LOG_TAG, "referncevalue, " + reference);

        }

    }

    @Override
    public void onStop () {
//do your stuff here

        super.onStop();
    }

}

