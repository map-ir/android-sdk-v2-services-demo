package ir.map.sdkdemo_service;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.wrapper.MaptexCameraUpdateFactory;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.MaptexMarkerOptions;
import ir.map.sdk_map.wrapper.OnMaptexReadyCallback;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;
import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.MaptexError;
import ir.map.sdk_services.models.MaptexReverse;
import ir.map.sdk_services.models.base.ResponseListener;

public class ReverseActivity extends AppCompatActivity implements ResponseListener<MaptexReverse> {

    private TextView tvInstruction;
    private MaptexMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse);

        tvInstruction = findViewById(R.id.tvInstruction);
        tvInstruction.setText("Click on the map to show Reverse Info");

        setUpMap();
    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map wrap the SupportMapFragment.
            ((SupportMaptexFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView))
                    .getMaptexAsync(new OnMaptexReadyCallback() {
                        @Override
                        public void onMaptexReady(MaptexMap var1) {
                            mMap = var1;
                            // Check if we were successful in obtaining the map.
                            if (mMap != null) {
                                mMap.moveCamera(MaptexCameraUpdateFactory.newLatLngZoom(new MaptexLatLng(35.6964895, 51.279745), 12));
                                mMap.setOnMapClickListener(new MaptexMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(MaptexLatLng paramLatLng) {
                                        mMap.addMarker(new MaptexMarkerOptions().position(paramLatLng).title("point bookmark"));
                                        new ServiceHelper().getReverseGeoInfo(
                                                paramLatLng.latitude, paramLatLng.longitude, ReverseActivity.this);
                                    }
                                });
                            }
                        }
                    });

        }
    }

    @Override
    public void onSuccess(MaptexReverse response) {
        Toast.makeText(this, response.addressCompact, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(MaptexError error) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();
    }
}
