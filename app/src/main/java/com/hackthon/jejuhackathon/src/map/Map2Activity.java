package com.hackthon.jejuhackathon.src.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.hackthon.jejuhackathon.R;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class Map2Activity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    TMapView mTmapView;
    private Context mContext;
    private boolean mTrackingMode = true;
    private TMapGpsManager mTmapGps=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        mContext = this;

        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        mTmapView = new TMapView(this);

        mTmapView.setSKTMapApiKey( "220db9a1-476e-4e2e-8691-794c9b0cd38e" );
        linearLayoutTmap.addView( mTmapView );

        mTmapView.setCompassMode(true);
        mTmapView.setIconVisibility(true);

        mTmapView.setZoomLevel(15);
        mTmapView.setMapType(TMapView.MAPTYPE_STANDARD);
        mTmapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        mTmapGps = new TMapGpsManager(Map2Activity.this);
        mTmapGps.setMinTime(1000);
        mTmapGps.setMinDistance(5);
        mTmapGps.setProvider(mTmapGps.NETWORK_PROVIDER);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        mTmapGps.OpenGps();

        mTmapView.setTrackingMode(true);
        mTmapView.setSightVisible(true);
//        TMapPoint tMapPoint = new TMapPoint(37.570841, 126.985302);
//
//        TMapCircle tMapCircle = new TMapCircle();
//        tMapCircle.setCenterPoint( tMapPoint );
//        tMapCircle.setRadius(300);
//        tMapCircle.setCircleWidth(2);
//        tMapCircle.setLineColor(Color.BLUE);
//        tMapCircle.setAreaColor(Color.GRAY);
//        tMapCircle.setAreaAlpha(100);
//        tMapView.addTMapCircle("circle1", tMapCircle);

        ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
        alTMapPoint.add( new TMapPoint(33.514578, 126.529531) );
        alTMapPoint.add( new TMapPoint(33.514674, 126.530126) );
        alTMapPoint.add( new TMapPoint(33.514459, 126.530105) );
        alTMapPoint.add(new TMapPoint(33.514468, 126.529547));

        TMapPolygon tMapPolygon = new TMapPolygon();
        tMapPolygon.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon.setPolygonWidth(2);
        tMapPolygon.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon.setAreaAlpha(100);
        for( int i=0; i<alTMapPoint.size(); i++ ) {
            tMapPolygon.addPolygonPoint( alTMapPoint.get(i) );
        }

        mTmapView.addTMapPolygon("Line1", tMapPolygon);

        ArrayList<TMapPoint> alTMapPoint2 = new ArrayList<TMapPoint>();
        alTMapPoint2.add( new TMapPoint(33.514808, 126.526544) );
        alTMapPoint2.add( new TMapPoint(33.514806, 126.526740) );
        alTMapPoint2.add( new TMapPoint(33.514446, 126.526689) );
        alTMapPoint2.add(new TMapPoint(33.514466, 126.526536));

        TMapPolygon tMapPolygon2 = new TMapPolygon();
        tMapPolygon2.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon2.setPolygonWidth(2);
        tMapPolygon2.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon2.setAreaAlpha(100);
        for( int i=0; i<alTMapPoint2.size(); i++ ) {
            tMapPolygon2.addPolygonPoint( alTMapPoint2.get(i) );
        }

        mTmapView.addTMapPolygon("Line2", tMapPolygon2);

        ArrayList<TMapPoint> alTMapPoint3 = new ArrayList<TMapPoint>();
        alTMapPoint3.add( new TMapPoint(33.513805, 126.528364) );
        alTMapPoint3.add( new TMapPoint(33.513670, 126.528400) );
        alTMapPoint3.add( new TMapPoint(33.513750, 126.528690) );
        alTMapPoint3.add(new TMapPoint(33.513956, 126.528513));

        TMapPolygon tMapPolygon3 = new TMapPolygon();
        tMapPolygon3.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon3.setPolygonWidth(2);
        tMapPolygon3.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon3.setAreaAlpha(100);
        for( int i=0; i<alTMapPoint3.size(); i++ ) {
            tMapPolygon3.addPolygonPoint( alTMapPoint3.get(i) );
        }

        mTmapView.addTMapPolygon("Line3", tMapPolygon3);

        ArrayList<TMapPoint> alTMapPoint4 = new ArrayList<TMapPoint>();
        alTMapPoint4.add( new TMapPoint(33.514792, 126.528223) );
        alTMapPoint4.add( new TMapPoint(33.514779, 126.528432) );
        alTMapPoint4.add( new TMapPoint(33.514913, 126.528523) );
        alTMapPoint4.add(new TMapPoint(33.514904, 126.528266));

        TMapPolygon tMapPolygon4 = new TMapPolygon();
        tMapPolygon4.setLineColor(Color.rgb(247, 181, 0));
        tMapPolygon4.setPolygonWidth(2);
        tMapPolygon4.setAreaColor(Color.rgb(247, 181, 0));
        tMapPolygon4.setAreaAlpha(100);
        for( int i=0; i<alTMapPoint4.size(); i++ ) {
            tMapPolygon4.addPolygonPoint( alTMapPoint4.get(i) );
        }

        mTmapView.addTMapPolygon("Line4", tMapPolygon4);

//        mTmapView.setCenterPoint(126.985302, 37.570841);


    }

    @Override
    public void onLocationChange(Location location) {
        if(mTrackingMode){
            mTmapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}
