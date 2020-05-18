package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.Location;
import com.sust.monitorapp.common.Constants;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yhl on 2020/4/13.
 *
 * 根据传输过来的位置信息，在地图上显示相应位置，并且点击可以显示详情
 */

public class DeviceLocationActivity extends AppCompatActivity implements AMap.OnMapLoadedListener
        , AMap.InfoWindowAdapter {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.map_dev_location)
    MapView mapDevLocation;


    private AMap aMap;
    private LatLng latlng1 = new LatLng(36.061, 103.834);

    private Location location;
    private Device device;
    private LatLng latlng;

    private Marker curShowWindowMarker;
    private boolean infoWindowShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_location);
        ButterKnife.bind(this);

        // 此方法必须重写
        mapDevLocation.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化标题、mapview
     */
    private void initView() {
        tvTitle.setText("站点位置");
        Intent intent = getIntent();
        device = (Device) intent.getSerializableExtra("device");
        location = (Location) intent.getSerializableExtra("location");

        latlng = new LatLng(location.getLat(), location.getLon());

        System.out.println(latlng.toString());

        if (aMap == null) {
            aMap = mapDevLocation.getMap();
            setUpMap();
        }
    }

    /**
     * 设置地图
     */
    private void setUpMap() {
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式

        //点击地图其他地方，InfoWindow消失
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public boolean onMarkerClick(Marker marker) {
                curShowWindowMarker = marker;
                infoWindowShown = false;
                Log.e("setOnMarkerClickListener", "Marker被点击了");
                return false;//return true 的意思是点击marker,marker不成为地图的中心坐标，反之，成为中心坐标。
            }
        });

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("tag","onMapClick");
                Log.e("tag","curShowWindowMarker="+curShowWindowMarker.isInfoWindowShown());
                //点击其它地方隐藏InfoWindow
                if(curShowWindowMarker.isInfoWindowShown() && !infoWindowShown){
                    infoWindowShown = true;
                    return;
                }

                if(curShowWindowMarker.isInfoWindowShown() && infoWindowShown){
                    curShowWindowMarker.hideInfoWindow();
                }
            }
        });
        addMarkersToMap();// 往地图上添加marker
    }

    /**
     * 地图上添加 marker
     */
    private void addMarkersToMap() {
        //设置InfoWindow的title和snippet
        StringBuilder title = new StringBuilder();
        title.append("设备id: ").append(device.getDevId()).append(" | ")
                .append("MAC地址: ").append(device.getDevMac());
        StringBuilder snippet = new StringBuilder();
        snippet.append("经纬度: ").append(latlng.latitude).append(",").append(latlng.longitude).append("\n")
                .append("详细地址: ").append(location.getAddress());

        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latlng).title(title.toString())
                .snippet(snippet.toString()).draggable(true));
    }

    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(latlng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(
                R.layout.item_info_window, null);
        render(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 设置自定义 infowinfow
     */
    public void render(Marker marker, View view) {

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
//            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
//                    titleText.length(), 0);
//            titleUi.setTextSize(15);

            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
//            snippetUi.setTextSize(15);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapDevLocation.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapDevLocation.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapDevLocation.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapDevLocation.onDestroy();
    }
}
