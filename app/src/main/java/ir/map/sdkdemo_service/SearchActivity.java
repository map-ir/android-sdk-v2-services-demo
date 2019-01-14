package ir.map.sdkdemo_service;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.wrapper.MaptexCameraUpdateFactory;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.MaptexMarkerOptions;
import ir.map.sdk_map.wrapper.OnMaptexReadyCallback;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;
import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.MaptexError;
import ir.map.sdk_services.models.MaptexSearchItem;
import ir.map.sdk_services.models.MaptexSearchResponse;
import ir.map.sdk_services.models.base.ResponseListener;

public class SearchActivity extends AppCompatActivity implements
        ResponseListener<MaptexSearchResponse>, View.OnClickListener {

    private EditText etSearch;
    private MaptexMap mMap;
    private ImageView imgSearch;
    private MaptexLatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        imgSearch = findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(this);

        setupMap();
    }

    private void setupMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map wrap the SupportMapFragment.
            ((SupportMaptexFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView))
                    .getMaptexAsync(new OnMaptexReadyCallback() {
                        @Override
                        public void onMaptexReady(MaptexMap var1) {
                            mMap = var1;
                            latLng = new MaptexLatLng(35.6964895, 51.279745);
                            mMap.moveCamera(MaptexCameraUpdateFactory.newLatLngZoom(latLng, 12));
                        }
                    });
        }
    }

    @Override
    public void onSuccess(MaptexSearchResponse response) {
        mMap.clear();
        for (MaptexSearchItem item : response.getValues()) {
            mMap.addMarker(new MaptexMarkerOptions().title(item.text)
                    .position(new MaptexLatLng(item.coordinate.latitude, item.coordinate.longitude)));
            latLng = new MaptexLatLng(item.coordinate.latitude, item.coordinate.longitude);//Zoom on last item in responses
        }
        mMap.moveCamera(MaptexCameraUpdateFactory.newLatLngZoom(latLng, 12));

    }

    @Override
    public void onError(MaptexError error) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgSearch:
                new ServiceHelper()
                        .search(etSearch.getText().toString(), latLng.latitude, latLng.longitude, null, this);
                break;
        }
    }
}
