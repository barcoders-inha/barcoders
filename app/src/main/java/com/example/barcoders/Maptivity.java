package com.example.barcoders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Maptivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{


    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_maptivity);

        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();



        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( Maptivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(new LatLng(37.52951256578456, 126.83299873558195))
                .title("쓰레기통")
                .snippet("서서울호수공원 삼거리 횡단보도앞");
        mMap.addMarker(makerOptions);

        MarkerOptions makerOptions1 = new MarkerOptions();
        makerOptions1.position(new LatLng(37.531105746980515, 126.83193316441802))
                .title("쓰레기통")
                .snippet("신월 보건지소 버스정류장");
        mMap.addMarker(makerOptions1);

        MarkerOptions makerOptions2 = new MarkerOptions();
        makerOptions2.position(new LatLng(37.53529657574819, 126.82904180674593))
                .title("쓰레기통")
                .snippet("서부여성발전센터 버스정류장앞");
        mMap.addMarker(makerOptions2);

        MarkerOptions makerOptions3 = new MarkerOptions();
        makerOptions3.position(new LatLng(37.539128730485466, 126.82743722944778))
                .title("쓰레기통")
                .snippet("신월5동주민센터 버스정류장앞");
        mMap.addMarker(makerOptions3);

        MarkerOptions makerOptions4 = new MarkerOptions();
        makerOptions4.position(new LatLng(37.53100652205303, 126.83102227055224))
                .title("쓰레기통")
                .snippet("현대 오일뱅크 주유소 앞");
        mMap.addMarker(makerOptions4);

        MarkerOptions makerOptions5 = new MarkerOptions();
        makerOptions5.position(new LatLng(37.51912615089133, 126.84297687055223))
                .title("쓰레기통")
                .snippet("삼성가구점 매장앞");
        mMap.addMarker(makerOptions5);

        MarkerOptions makerOptions6 = new MarkerOptions();
        makerOptions6.position(new LatLng(37.51142268847191, 126.84076712944774))
                .title("쓰레기통")
                .snippet("푸른마을3단지 마을버스 정류장");
        mMap.addMarker(makerOptions6);

        MarkerOptions makerOptions7 = new MarkerOptions();
        makerOptions7.position(new LatLng(37.51992842513577, 126.83485552944774))
                .title("쓰레기통")
                .snippet("신월7동 문화체육센터 앞");
        mMap.addMarker(makerOptions7);

        MarkerOptions makerOptions8 = new MarkerOptions();
        makerOptions8.position(new LatLng(37.51927071708274, 126.83804367055225))
                .title("쓰레기통")
                .snippet("경창빌딩앞 버스정류장");
        mMap.addMarker(makerOptions8);

        MarkerOptions makerOptions9 = new MarkerOptions();
        makerOptions9.position(new LatLng(37.51779972687891, 126.83423157055223))
                .title("쓰레기통")
                .snippet("신월7동시영아)정문버스정류장앞");
        mMap.addMarker(makerOptions9);

        MarkerOptions makerOptions10 = new MarkerOptions();
        makerOptions10.position(new LatLng(37.5118988278691, 126.83721427055225))
                .title("쓰레기통")
                .snippet("이펜하우스1단지앞 버스정류장");
        mMap.addMarker(makerOptions10);

        MarkerOptions makerOptions11 = new MarkerOptions();
        makerOptions11.position(new LatLng(37.51115568500671, 126.83289787668649))
                .title("쓰레기통")
                .snippet("서남병원앞 버스정류장");
        mMap.addMarker(makerOptions11);

        MarkerOptions makerOptions12 = new MarkerOptions();
        makerOptions12.position(new LatLng(37.524841976962776, 126.85027674239731))
                .title("쓰레기통")
                .snippet("양강중학교 버스정류장앞");
        mMap.addMarker(makerOptions12);

        MarkerOptions makerOptions13 = new MarkerOptions();
        makerOptions13.position(new LatLng(37.52189093295719, 126.85765852331353))
                .title("쓰레기통")
                .snippet("우리은행(신정4동지점)앞");
        mMap.addMarker(makerOptions13);

        MarkerOptions makerOptions14 = new MarkerOptions();
        makerOptions14.position(new LatLng(37.52200390012332, 126.8523031113031))
                .title("쓰레기통")
                .snippet("경인 공개중개사 앞");
        mMap.addMarker(makerOptions14);

        MarkerOptions makerOptions15 = new MarkerOptions();
        makerOptions15.position(new LatLng(37.5175642710775, 126.85852535063205))
                .title("쓰레기통")
                .snippet("목10단지 A상가 버스정류장앞");
        mMap.addMarker(makerOptions15);

        MarkerOptions makerOptions16 = new MarkerOptions();
        makerOptions16.position(new LatLng(37.51884127888422, 126.8636176998538))
                .title("쓰레기통")
                .snippet("양천문화회관앞 버스정류장");
        mMap.addMarker(makerOptions16);

        MarkerOptions makerOptions17 = new MarkerOptions();
        makerOptions17.position(new LatLng(37.507153160117774, 126.86000163558198))
                .title("쓰레기통")
                .snippet("목11단지 1117동 버스정류장");
        mMap.addMarker(makerOptions17);

        MarkerOptions makerOptions18 = new MarkerOptions();
        makerOptions18.position(new LatLng(37.51270829493385, 126.8662866111145))
                .title("쓰레기통")
                .snippet("양천구민체육센터 버스정류장앞");
        mMap.addMarker(makerOptions18);

        MarkerOptions makerOptions19 = new MarkerOptions();
        makerOptions19.position(new LatLng(37.514653346122806, 126.85567952693054))
                .title("쓰레기통")
                .snippet("신서중학교 버스정류장 앞");
        mMap.addMarker(makerOptions19);

        MarkerOptions makerOptions20 = new MarkerOptions();
        makerOptions20.position(new LatLng(37.522102012722826, 126.86412376441804))
                .title("쓰레기통")
                .snippet("남부법원 버스정류장 앞");
        mMap.addMarker(makerOptions20);

        MarkerOptions makerOptions21 = new MarkerOptions();
        makerOptions21.position(new LatLng(37.51288261086189, 126.8665560345765))
                .title("쓰레기통")
                .snippet("양천구청역 건너 버스정류장 앞");
        mMap.addMarker(makerOptions21);

        MarkerOptions makerOptions22 = new MarkerOptions();
        makerOptions22.position(new LatLng(37.521551194657, 126.85884678895493))
                .title("쓰레기통")
                .snippet("우리은행건너편 버스정류장 앞");
        mMap.addMarker(makerOptions22);

        MarkerOptions makerOptions23 = new MarkerOptions();
        makerOptions23.position(new LatLng(37.5112181312126, 126.84303762572553))
                .title("쓰레기통")
                .snippet("한성교회 건너편");
        mMap.addMarker(makerOptions23);

        MarkerOptions makerOptions24 = new MarkerOptions();
        makerOptions24.position(new LatLng(37.51699114911473, 126.85457250674588))
                .title("쓰레기통")
                .snippet("해성산부인과 버스정류장 앞");
        mMap.addMarker(makerOptions24);

        MarkerOptions makerOptions25 = new MarkerOptions();
        makerOptions25.position(new LatLng(37.525645919267085, 126.85919443558197))
                .title("쓰레기통")
                .snippet("신정4동 신협앞");
        mMap.addMarker(makerOptions25);

        MarkerOptions makerOptions26 = new MarkerOptions();
        makerOptions26.position(new LatLng(37.515107398907936, 126.85477380613423))
                .title("쓰레기통")
                .snippet("신정3동 주민센터 앞");
        mMap.addMarker(makerOptions26);

        MarkerOptions makerOptions27 = new MarkerOptions();
        makerOptions27.position(new LatLng(37.51315700838295, 126.86058312944776))
                .title("쓰레기통")
                .snippet("12단지 버스정류장 앞");
        mMap.addMarker(makerOptions27);

        MarkerOptions makerOptions28 = new MarkerOptions();
        makerOptions28.position(new LatLng(37.53838204916697, 126.8640923932541))
                .title("쓰레기통")
                .snippet("대일고등학교 버스정류장");
        mMap.addMarker(makerOptions28);

        MarkerOptions makerOptions29 = new MarkerOptions();
        makerOptions29.position(new LatLng(37.54697498310245, 126.87485590368748))
                .title("쓰레기통")
                .snippet("염창역,중앙 버스정류장");
        mMap.addMarker(makerOptions29);

        MarkerOptions makerOptions30 = new MarkerOptions();
        makerOptions30.position(new LatLng(37.549557210965745, 126.8672332412634))
                .title("쓰레기통")
                .snippet("등촌역4번출구 앞");
        mMap.addMarker(makerOptions30);

        MarkerOptions makerOptions31 = new MarkerOptions();
        makerOptions31.position(new LatLng(37.528077985637594, 126.87651576532897))
                .title("쓰레기통")
                .snippet("행복한백화점 건너편 공동화장실 앞");
        mMap.addMarker(makerOptions31);

        MarkerOptions makerOptions32 = new MarkerOptions();
        makerOptions32.position(new LatLng(37.5264998055037, 126.87382125781095))
                .title("쓰레기통")
                .snippet("목운초등학교 버스정류장 앞");
        mMap.addMarker(makerOptions32);

        MarkerOptions makerOptions33 = new MarkerOptions();
        makerOptions33.position(new LatLng(37.52490060779918, 126.86949463869244))
                .title("쓰레기통")
                .snippet("서울탑치과 버스정류장 앞");
        mMap.addMarker(makerOptions33);

        MarkerOptions makerOptions34 = new MarkerOptions();
        makerOptions34.position(new LatLng(37.527594737902994, 126.86736165092631))
                .title("쓰레기통")
                .snippet("7단지 후문 버스정류장 앞");
        mMap.addMarker(makerOptions34);

        MarkerOptions makerOptions35 = new MarkerOptions();
        makerOptions35.position(new LatLng(37.52798939784433, 126.87329810674592))
                .title("쓰레기통")
                .snippet("오목공원");
        mMap.addMarker(makerOptions35);

        MarkerOptions makerOptions36 = new MarkerOptions();
        makerOptions36.position(new LatLng(37.530106230508075, 126.87534139695894))
                .title("쓰레기통")
                .snippet("목동 파라곤 버스정류장 앞");
        mMap.addMarker(makerOptions36);

        MarkerOptions makerOptions37 = new MarkerOptions();
        makerOptions37.position(new LatLng(37.53477084801087, 126.87578073743438))
                .title("쓰레기통")
                .snippet("목3단지 302동 버스정류장 앞");
        mMap.addMarker(makerOptions37);

        MarkerOptions makerOptions38 = new MarkerOptions();
        makerOptions38.position(new LatLng(37.53787317359276, 126.8821808796234))
                .title("쓰레기통")
                .snippet("월촌중학교 버스정류장 앞");
        mMap.addMarker(makerOptions38);

        MarkerOptions makerOptions39 = new MarkerOptions();
        makerOptions39.position(new LatLng(37.53675373386629, 126.88443683649292))
                .title("쓰레기통")
                .snippet("이대목동병원 버스정류장 앞");
        mMap.addMarker(makerOptions39);

        MarkerOptions makerOptions40 = new MarkerOptions();
        makerOptions40.position(new LatLng(37.533918311468334, 126.88094808220902))
                .title("쓰레기통")
                .snippet("목5단지 B상가 버스정류장 앞");
        mMap.addMarker(makerOptions40);

        MarkerOptions makerOptions41 = new MarkerOptions();
        makerOptions41.position(new LatLng(37.53592922210161, 126.87797373558199))
                .title("쓰레기통")
                .snippet("파리공원 사잇길");
        mMap.addMarker(makerOptions41);

        MarkerOptions makerOptions42 = new MarkerOptions();
        makerOptions42.position(new LatLng(37.525641438340124, 126.86914466441802))
                .title("쓰레기통")
                .snippet("7단지 버스정류장");
        mMap.addMarker(makerOptions42);

        MarkerOptions makerOptions43 = new MarkerOptions();
        makerOptions43.position(new LatLng(37.525751671690834, 126.86316717790984))
                .title("쓰레기통")
                .snippet("목동오거리 보성상가 버스정류장");
        mMap.addMarker(makerOptions43);

        MarkerOptions makerOptions44 = new MarkerOptions();
        makerOptions44.position(new LatLng(37.52252847990489, 126.83632166441805))
                .title("쓰레기통")
                .snippet("GM자동차 서비스앞");
        mMap.addMarker(makerOptions44);

        MarkerOptions makerOptions45 = new MarkerOptions();
        makerOptions45.position(new LatLng(37.51505487904612, 126.87248685581973))
                .title("쓰레기통")
                .snippet("양천공원 버스정류장앞");
        mMap.addMarker(makerOptions45);

        MarkerOptions makerOptions46 = new MarkerOptions();
        makerOptions46.position(new LatLng(37.5342560894292, 126.82892530674592))
                .title("쓰레기통")
                .snippet("신월3동 우체국앞");
        mMap.addMarker(makerOptions46);

        MarkerOptions makerOptions47 = new MarkerOptions();
        makerOptions47.position(new LatLng(37.52408478175904, 126.84801559325412))
                .title("쓰레기통")
                .snippet("신월동 양강초교 앞");
        mMap.addMarker(makerOptions47);

        MarkerOptions makerOptions48 = new MarkerOptions();
        makerOptions48.position(new LatLng(37.52320281255017, 126.86457141779097))
                .title("쓰레기통")
                .snippet("신월동 강서초교 앞");
        mMap.addMarker(makerOptions48);

        MarkerOptions makerOptions49 = new MarkerOptions();
        makerOptions49.position(new LatLng(37.544048413537865, 126.88339136812284))
                .title("쓰레기통")
                .snippet("신목동역 3번출구앞");
        mMap.addMarker(makerOptions49);

        MarkerOptions makerOptions50 = new MarkerOptions();
        makerOptions50.position(new LatLng(37.523994856427755, 126.86457034907379))
                .title("쓰레기통")
                .snippet("진명여고 건너편(락감식당앞)");
        mMap.addMarker(makerOptions50);

        MarkerOptions makerOptions51 = new MarkerOptions();
        makerOptions51.position(new LatLng(37.52383735431994, 126.86611277790983))
                .title("쓰레기통")
                .snippet("진명여고 앞)");
        mMap.addMarker(makerOptions51);



    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        // MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(currentLatLng);
        //markerOptions.title(markerTitle);
        // markerOptions.snippet(markerSnippet);
        // markerOptions.draggable(true);


        // currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Maptivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }



}