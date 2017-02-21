package com.example.vartikasharma.carcrew.app_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.vartikasharma.carcrew.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SERVER_HOST = "https://carcrew-project.firebaseio.com/";
    private static final String URL = "http://ira-199298721.ap-south-1.elb.amazonaws.com/IRA/api/v1/enquiry_itemApi/?Enquiry_Source_ID=1";
    private List<DataObject> dataObject = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://carcrew-project.appspot.com").child("download.json");
        Log.i("storageRef," , storageRef.toString());

        try {
            fetchData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                            Log.i(LOG_TAG, "first dataObject, " + dataObject.get(i).getCar_Name());
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
       // DatabaseReference ref = database.getReference(SERVER_HOST);
        Log.i(LOG_TAG, "ref, " + ref);
        DatabaseReference usersRef = ref.child("data");
        for (int i =0; i < dataObject.size(); i++) {
            DatabaseReference reference = usersRef.push();
            Log.i(LOG_TAG, "refernce, " + reference);
            reference.setValue(dataObject.get(i));
        }
        usersRef.setValue(dataObject);
    }
}
