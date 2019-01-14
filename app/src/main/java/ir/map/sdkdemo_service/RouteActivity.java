package ir.map.sdkdemo_service;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.MaptexPolyUtil;
import ir.map.sdk_map.MaptexSphericalUtil;
import ir.map.sdk_map.wrapper.MaptexCameraUpdate;
import ir.map.sdk_map.wrapper.MaptexCameraUpdateFactory;
import ir.map.sdk_map.wrapper.MaptexJointType;
import ir.map.sdk_map.wrapper.MaptexLatLngBounds;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.MaptexPolyline;
import ir.map.sdk_map.wrapper.MaptexPolylineOptions;
import ir.map.sdk_map.wrapper.MaptexRoundCap;
import ir.map.sdk_map.wrapper.OnMaptexReadyCallback;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;
import ir.map.sdk_services.RouteMode;
import ir.map.sdk_services.ServiceHelper;
import ir.map.sdk_services.models.MaptexError;
import ir.map.sdk_services.models.MaptexManeuver;
import ir.map.sdk_services.models.MaptexRouteResponse;
import ir.map.sdk_services.models.MaptexStepsItem;
import ir.map.sdk_services.models.base.ResponseListener;

public class RouteActivity extends AppCompatActivity implements ResponseListener<MaptexRouteResponse> {

    private MaptexMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

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
                                MaptexLatLng latLng1 = new MaptexLatLng(35.7413024, 51.421966);
                                MaptexLatLng latLng2 = new MaptexLatLng(35.7411668, 51.4261612);
                                new ServiceHelper()
                                        .getRouteInfo(latLng1, latLng2, RouteMode.BASIC, RouteActivity.this);
                            }
                        }
                    });

        }
    }

    @Override
    public void onSuccess(MaptexRouteResponse response) {
        updateRoutingInfo(response);

    }

    @Override
    public void onError(MaptexError error) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();
    }

    public void updateRoutingInfo(MaptexRouteResponse routingInfo) {

        MaptexPolyline mainRouteLine = null;
        MaptexPolyline alternateRouteLine = null;
        List<MaptexLatLng> latLngsRouteListMain;
        List<MaptexLatLng> latLngsRouteListAlternative;
        List<MaptexManeuver> mainRouteManeuver = new ArrayList<>();
        List<MaptexManeuver> alternateRouteManeuver = new ArrayList<>();
        List<MaptexStepsItem> steps = new ArrayList<>();
        List<MaptexLatLng> mainIntersectionsPoints = new ArrayList<>();
        List<MaptexPolyline> fullMainIntersectionsLines = new ArrayList<>();

        if (mainRouteLine != null) {
            mainRouteLine.remove();
        }
        if (alternateRouteLine != null) {
            alternateRouteLine.remove();
        }

        latLngsRouteListMain = new ArrayList<>();
        latLngsRouteListAlternative = new ArrayList<>();
        //Check Route Info Null
        //Check And Init Main Route If Exists
        if (routingInfo != null && routingInfo.routes != null) {
            for (int i = 0; i < routingInfo.routes.size(); i++) {
                if (routingInfo.routes.get(i).legs != null) {
                    for (int j = 0; j < routingInfo.routes.get(i).legs.size(); j++) {
                        if (routingInfo.routes.get(i).legs.get(j).steps != null) {
                            for (int k = 0; k < routingInfo.routes.get(i).legs.get(j).steps.size(); k++) {
                                if (routingInfo.routes.get(i).legs.get(j).steps.get(k) != null) {
                                    if (i == 0) {
                                        steps.add(routingInfo.routes.get(i).legs.get(j).steps.get(k));
                                        mainRouteManeuver.add(routingInfo.routes.get(i).legs.get(j).steps.get(k).maneuver);
                                        latLngsRouteListMain.addAll(MaptexPolyUtil.decode(routingInfo.routes.get(i).legs.get(j).steps.get(k).geometry));
                                    } else if (i == 1) {
                                        alternateRouteManeuver.add(routingInfo.routes.get(i).legs.get(j).steps.get(k).maneuver);
                                        latLngsRouteListAlternative.addAll(MaptexPolyUtil.decode(routingInfo.routes.get(i).legs.get(j).steps.get(k).geometry));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Create Main Line And Show It On Map
            if (latLngsRouteListMain.size() > 0) {
                mainRouteLine = mMap.addPolyline(new MaptexPolylineOptions().jointType(MaptexJointType.ROUND).width(30).clickable(true).addAll(latLngsRouteListMain)
                        .visible(true).color(R.color.colorAccent).zIndex(4));
                mainRouteLine.setStartCap(new MaptexRoundCap());
                mainRouteLine.setEndCap(new MaptexRoundCap());

                MaptexLatLngBounds.Builder builder = new MaptexLatLngBounds.Builder();
                for (MaptexLatLng latLng : latLngsRouteListMain) {
                    builder.include(latLng);
                }
                MaptexLatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                MaptexCameraUpdate cu = MaptexCameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }

            //Create Alternate Line And Show It On Map
            if (latLngsRouteListAlternative.size() > 0) {
                alternateRouteLine = mMap.addPolyline(new MaptexPolylineOptions().jointType(MaptexJointType.ROUND).width(12).clickable(true)
                        .visible(true).color(R.color.colorPrimaryDark).zIndex(4).addAll(latLngsRouteListAlternative));
                alternateRouteLine.setStartCap(new MaptexRoundCap());
                alternateRouteLine.setEndCap(new MaptexRoundCap());

                MaptexLatLngBounds.Builder builder = new MaptexLatLngBounds.Builder();
                for (MaptexLatLng latLng : latLngsRouteListAlternative) {
                    builder.include(latLng);
                }
                MaptexLatLngBounds bounds = builder.build();
                int padding = 50; // offset from edges of the map in pixels
                MaptexCameraUpdate cu = MaptexCameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);

            }

            //Draw Intersections Lines
//            SphericalUtil.computeOffsetOrigin()
            if (mainRouteManeuver != null && mainRouteManeuver.size() > 0) {
                for (int i = 0; i < mainRouteManeuver.size(); i++) {
                    mainIntersectionsPoints.clear();
                    fullMainIntersectionsLines.clear();
                    MaptexLatLng base = new MaptexLatLng(mainRouteManeuver.get(i).location.get(1), mainRouteManeuver.get(i).location.get(0));
                    MaptexLatLng basePrevious = MaptexSphericalUtil.computeOffset(base, 10, mainRouteManeuver.get(i).bearingBefore + 180);
                    MaptexLatLng baseNext = MaptexSphericalUtil.computeOffset(base, 10, mainRouteManeuver.get(i).bearingAfter);

                    switch (mainRouteManeuver.get(i).type) {
                        case "depart":

                            break;
                        case "turn":
                            mainIntersectionsPoints.add(basePrevious);
                            mainIntersectionsPoints.add(base);
                            mainIntersectionsPoints.add(baseNext);
                            fullMainIntersectionsLines.add(mMap.addPolyline(
                                    new MaptexPolylineOptions().color(Color.YELLOW).startCap(new MaptexRoundCap())
                                            .endCap(new MaptexRoundCap()).width(15).jointType(MaptexJointType.ROUND)
                                            .addAll(mainIntersectionsPoints).zIndex(4)
                                            .visible(true)));
                            break;
                    }
                }
            }
        }

    }
}
