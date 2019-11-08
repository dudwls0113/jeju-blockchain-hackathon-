package com.hackthon.jejuhackathon.src;

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
import com.skt.Tmap.TMapCircle;
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
        alTMapPoint.add( new TMapPoint(37.570841, 126.985302) ); // SKT타워
        alTMapPoint.add( new TMapPoint(37.551135, 126.988205) ); // N서울타워
        alTMapPoint.add( new TMapPoint(37.579600, 126.976998) ); // 경복궁

        TMapPolygon tMapPolygon = new TMapPolygon();
        tMapPolygon.setLineColor(R.color.colorPrimary);
        tMapPolygon.setPolygonWidth(2);
        tMapPolygon.setAreaColor(R.color.mapTransYellow);
        tMapPolygon.setAreaAlpha(100);
        for( int i=0; i<alTMapPoint.size(); i++ ) {
            tMapPolygon.addPolygonPoint( alTMapPoint.get(i) );
        }

        mTmapView.addTMapPolygon("Line1", tMapPolygon);

        mTmapView.setCenterPoint(126.985302, 37.570841);


    }

    @Override
    public void onLocationChange(Location location) {
        if(mTrackingMode){
            mTmapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}
