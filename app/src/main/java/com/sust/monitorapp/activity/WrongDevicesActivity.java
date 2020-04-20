package com.sust.monitorapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.sust.monitorapp.R;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.Location;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Response;

public class WrongDevicesActivity extends AppCompatActivity implements AMap.OnMapLoadedListener
        , AMap.InfoWindowAdapter {

    @BindView(R.id.title_back)
    RelativeLayout titleBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.map_dev_location)
    MapView mapDevLocation;

    //MapView相关
    private AMap aMap;
    private Marker curShowWindowMarker;
    private boolean infoWindowShown = false;
    private ArrayList<MarkerOptions> markerOptionsList = new ArrayList<>();

    //服务器通过网络传输来的异常设备信息。为了避免二次加载，service获取之后通过intent传输过来。
    private Device[] devices;
    private LoadingPopupView popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_devices);
        ButterKnife.bind(this);

        // 此方法必须重写
        mapDevLocation.onCreate(savedInstanceState);
        initView();
        getData();
    }

    /**
     * 获取位置信息
     */
    private void getData() {
        new Thread(() -> {
            popupView = (LoadingPopupView) new XPopup.Builder(WrongDevicesActivity.this)
                    .asLoading("正在加载中").show();
            Looper.prepare();
            try {
                for (Device device : devices) {
                    String url1 = "lac=" + device.getLac() + "&ci=" + device.getCellid();
                    Response response1 = NetUtil.queryAddress(url1);
                    if (response1.isSuccessful()) {
                        //解析位置信息
                        Location location = JsonUtil.jsonToBean(response1.body().string(), Location.class);
                        if (location.getErrcode() == 10000) {
                            Toast.makeText(WrongDevicesActivity.this, "lac和cell ID参数错误", Toast.LENGTH_SHORT).show();
                        } else if (location.getErrcode() == 10001) {
                            Toast.makeText(WrongDevicesActivity.this, "lac和cell ID无查询结果", Toast.LENGTH_SHORT).show();
                        }
                        device.setLat(location.getLat());
                        device.setLon(location.getLon());
                        device.setAddress(location.getAddress());
                    }
                }

                runOnUiThread(() -> {
                    popupView.dismiss();
                    //往地图上添加marker
                    addMarkersToMap();
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Looper.loop();
            }
        }).start();
    }

    /**
     * 初始化标题、mapview
     */
    private void initView() {
        tvTitle.setText("站点位置");

        //todo 获取intent传来的数据
        devices = (Device[]) getIntent().getSerializableExtra("wrong_devices");

        if (aMap == null) {
            aMap = mapDevLocation.getMap();
            setUpMap();
        }
    }

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
                Log.e("tag", "onMapClick");
                Log.e("tag", "curShowWindowMarker=" + curShowWindowMarker.isInfoWindowShown());
                //点击其它地方隐藏InfoWindow
                if (curShowWindowMarker.isInfoWindowShown() && !infoWindowShown) {
                    infoWindowShown = true;
                    return;
                }

                if (curShowWindowMarker.isInfoWindowShown() && infoWindowShown) {
                    curShowWindowMarker.hideInfoWindow();
                }
            }
        });
//        addMarkersToMap();// 往地图上添加marker
    }

    /**
     * 往地图上添加marker
     */
    private void addMarkersToMap() {
        //设置InfoWindow的title和snippet
        for (Device device : devices) {
            StringBuilder title = new StringBuilder();
            title.append("设备id: ").append(device.getDevId()).append(" | ")
                    .append("名称: ").append(device.getDevName()).append(" | ")
                    .append("MAC地址: ").append(device.getDevMac());
            StringBuilder snippet = new StringBuilder();
            snippet.append("详细地址: ").append(device.getAddress());
            markerOptionsList.add(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .position(new LatLng(device.getLat(), device.getLon()))
                    .title(title.toString())
                    .snippet(snippet.toString()).draggable(true));

        }
        aMap.addMarkers(markerOptionsList, true);
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
     * 设计自定义的 infoWindow 属性
     *
     * @param marker infoWindow是哪个 marker 的
     * @param view   infoWindow 的 view
     */
    private void render(Marker marker, View view) {
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
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

        //使得view中的“点击查看数据详情按钮”有效，绑定相应点击事件
        Button button = view.findViewById(R.id.bt_more_info);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WrongDevicesActivity.this, "点击了button:title=" + title, Toast.LENGTH_SHORT).show();
                //从title中截取devId数据，作为跳转时携带数据
                String devId = title.split(":")[1].split("\\|")[0].trim();
                Intent intent = new Intent(WrongDevicesActivity.this, DeviceDataActivity.class);
                intent.putExtra("devId", devId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapLoaded() {
        // 设置所有marker显示在当前可视区域地图中
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions markerOptions : markerOptionsList) {
            builder.include(markerOptions.getPosition());
        }
//        LatLngBounds bounds = builder.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));
//        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1000));
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


    /**
     * 顶部返回按钮点击事件
     */
    @OnClick(R.id.title_back)
    public void onViewClicked() {
        finish();
    }
}
