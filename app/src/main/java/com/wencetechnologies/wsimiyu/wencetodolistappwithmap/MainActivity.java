package com.wencetechnologies.wsimiyu.wencetodolistappwithmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText wenceEditText;
    private Button wenceButton;
    private ListView wenceListView;
    private MapView wenceMapView;
    private ArrayList<String> wenceItems;
    private ArrayAdapter<String> wenceAdapter;
    private LocationDisplay mLocationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wenceEditText = findViewById(R.id.wenceEditText);

        wenceListView = findViewById(R.id.wenceListView);

        wenceMapView = findViewById(R.id.wenceMapView);

        wenceButton = findViewById(R.id.wenceButton);

        wenceItems = new ArrayList<>();

        wenceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,wenceItems);

        wenceListView.setAdapter(wenceAdapter);

        wenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String taskdata = wenceEditText.getText().toString();

                if (taskdata.equals(""))
                {
                    Toast.makeText(MainActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    wenceAdapter.add(taskdata);
                }

            }
        });

        deleteTask();

        ArcGISMap wenceMap = new ArcGISMap();

        wenceMap.setBasemap(Basemap.createOpenStreetMap());

        wenceMapView.setMap(wenceMap);

        setupLocationDisplay();

    }

    private void deleteTask() {

        wenceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(MainActivity.this, "You have deleted a task", Toast.LENGTH_SHORT).show();
                wenceItems.remove(position);
                wenceAdapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    private void setupLocationDisplay() {
        mLocationDisplay = wenceMapView.getLocationDisplay();

        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.startAsync();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause(){
        wenceMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        wenceMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wenceMapView.dispose();
    }

}