package com.tamj.secure.activities;
import com.tamj.secure.AppState;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;
import java.util.Locale;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import com.tamj.secure.thread.ExpireDate;
import com.tamj.secure.view.AppUpdateHelper;
import com.tamj.secure.view.CircleProgressBar;
import com.tamj.secure.view.LinearInView;
import com.tamj.secure.view.LinearOutView;
import com.tamj.secure.view.RotateLoading;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import com.tamj.secure.R;
import com.tamj.secure.adapter.ConfigSpinnerAdapter;
import com.tamj.secure.adapter.LogsAdapter;
import com.tamj.secure.config.ConfigDataBase;
import com.tamj.secure.config.ConfigUtil;
import com.tamj.secure.config.SettingsConstants;
import com.tamj.secure.core.vpnutils.TunnelUtils;
import com.tamj.secure.harliesApplication;
import com.tamj.secure.logger.ConnectionStatus;
import com.tamj.secure.logger.hLogStatus;
import com.tamj.secure.service.HarlieService;
import com.tamj.secure.service.OpenVPNService;
import com.tamj.secure.service.OpenVPNService.ConnectionStats;
import com.tamj.secure.thread.checkUpdate;
import com.tamj.secure.utils.FileUtils;
import com.tamj.secure.utils.PasswordUtil;
import com.tamj.secure.utils.PrefUtil;
import com.tamj.secure.utils.util;
import com.tamj.secure.view.StatisticGraphData;
import com.google.android.play.core.install.model.ActivityResult;

import net.openvpn.openvpn.BuildConfig;


import android.os.Handler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("NewApi")
public class OpenVPNClient extends OpenVPNClientBase implements NavigationView.OnNavigationItemSelectedListener,hLogStatus.StateListener, SettingsConstants,hLogStatus.ByteCountListener,OnClickListener{
    private final Executor executor = Executors.newSingleThreadExecutor();
    private EditText xUser, xPass;
    private LogsAdapter mAdapter;
    private RecyclerView logRecycle;
    private ImageView mDrawerMenu,showLog;
    private LinearInView liveDataIn;
    private LinearOutView liveDataOut;
    private RotateLoading mRotateLoading;
    private CircleProgressBar circleProgressBar;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout serverDialog,networkDialog;
    private Button btn_connector;
    private TextView tunnel_type,duration_view,byteIn_view,byteOut_view,status_view,Config_vers,s_name,p_name,ac_xp,mDataInTv,mDataOutTv,val1,val2;
    private static final int START_BIND_CALLED = 1;
    private static final int REQUEST_IMPORT_FILE = 2;
    private Handler mHandler;
    private AlertDialog cBuiler;
    private static boolean isConnected = false;
    private PrefUtil prefs;
    private ConfigDataBase exported_config;
    private final Handler stats_timer_handler = new Handler();

    private final Runnable stats_timer_task = new Runnable() {
        public void run() {
            if(hLogStatus.isTunnelActive()){
                generateValue();
                duration_view.setText(getUpDateBytes().isConnected() ? getUpDateBytes().elapsedTimeToDisplay(getUpDateBytes().getElapsedTime()) : "00:00:00");
            }
            OpenVPNClient.this.show_stats();
            OpenVPNClient.this.schedule_stats();
        }
    };
    // ===== HEARTBEAT / PING SYSTEM =====

    private final Handler pingHandler = new Handler();
    private final ExecutorService pingExecutor = Executors.newSingleThreadExecutor();
    private boolean pingRunning = false;

    private final Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!pingRunning) return;

            pingExecutor.execute(() -> {
                try {
                    String api = getPref().getString(CONFIG_API, "");
                    if (api.isEmpty()) {
                        api = "http://panel.tamjph.com/api/ping.php";
                    }

                    String user = getConfig().getSecureString(USERNAME_KEY);
                    String pass = getConfig().getSecureString(PASSWORD_KEY);

                    if (user.isEmpty() || pass.isEmpty()) return;

                   String urlStr = api
        + "?user=" + user
        + "&pass=" + pass
        + "&device_id=" + getHWID(OpenVPNClient.this);

                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setRequestMethod("GET");

                    // 🔐 ADD TOKEN HERE (THIS IS THE ANSWER)
                    conn.setRequestProperty("X-APP-TOKEN", "TAMJ_APP_2026_SECRET");

                    conn.getResponseCode();
                    conn.disconnect();

                } catch (Exception ignored) {}
            });

            pingHandler.postDelayed(this, 60_000);
        }
    };
    private void startPingLoop() {
        if (pingRunning) return;
        pingRunning = true;
        pingHandler.post(pingRunnable);
    }

    private void stopPingLoop() {
        pingRunning = false;
        pingHandler.removeCallbacks(pingRunnable);
    }
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private AdsConsent googleMobileAdsConsentManager;
    private final AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);

    private AppUpdateHelper appUpdateHelper;

    private final ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        int resultCode = result.getResultCode();
        if (resultCode != RESULT_OK) {
            if (resultCode == RESULT_CANCELED) {
                appUpdateHelper.checkForUpdate();
            } else if (resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                appUpdateHelper.checkForUpdate();
            }
        }
    });


    private void cancel_stats() {
        this.stats_timer_handler.removeCallbacks(this.stats_timer_task);
    }

    private void updateTimeLeftView(long millis){
        if (timeLeftTv == null) return;
        if (millis <= 0){
            timeLeftTv.setText("00:00:00");
            // optionally disable add-time if autologin not set
            return;
        }
        long s = millis / 1000;
        long hh = s / 3600;
        long mm = (s % 3600) / 60;
        long ss = s % 60;
        timeLeftTv.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hh, mm, ss));
    }

    private void onAddTimeClicked(){
        // show rewarded ad and on reward add 1 hour
        if (rewardedAd != null) {
            showRewardedAd();
        } else {
            util.showToast(resString(R.string.app_name), "Ad not ready, please try again later");
            loadRewardedAd();
        }
    }

    private void loadRewardedAd(){
        if (rewardedAdLoading || rewardedAd != null) return;
        rewardedAdLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, harliesApplication.resString(R.string.adunit_rewarded), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                rewardedAdLoading = false;
                Log.i("OpenVPNClient","rewarded ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
                rewardedAdLoading = false;
                Log.i("OpenVPNClient","rewarded ad failed: " + loadAdError.getMessage());
            }
        });
    }

    private void showRewardedAd(){
        if (rewardedAd == null) return;
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                loadRewardedAd();
            }
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError){
                rewardedAd = null;
                loadRewardedAd();
            }
        });
        rewardedAd.show(this, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                // grant 1 hour
                long left = getPref().getLong(PREF_ADD_TIME_LEFT, 0);
                left += 3600L * 1000L;
                getEditor().putLong(PREF_ADD_TIME_LEFT, left).apply();
                updateTimeLeftView(left);
                if (left > 0) addTimeHandler.post(addTimeCountdown);
                util.showToast(resString(R.string.app_name), "You've been rewarded 1 hour of time");
            }
        });
    }
    private void schedule_stats() {
        cancel_stats();
        this.stats_timer_handler.postDelayed(this.stats_timer_task, 1000);
    }
    private static long m_SentBytes = 0;
    private static long m_ReceivedBytes = 0;
    public void show_stats() {
        try{
            if(hLogStatus.isTunnelActive()&&!isConnected){
                if(getConfig().getServerType().equals(SERVER_TYPE_OVPN)){
                    ConnectionStats stats = get_connection_stats();
                    hLogStatus.updateByteCount(stats.bytes_in,stats.bytes_out);
                }else{
                    m_ReceivedBytes += getUpDateBytes().getBytesReceived();
                    m_SentBytes += getUpDateBytes().getBytesSent();
                    hLogStatus.updateByteCount(m_ReceivedBytes,m_SentBytes);
                }
            }
            if(hLogStatus.isTunnelActive()&&isConnected){
                if(getConfig().getServerType().equals(SERVER_TYPE_OVPN)){
                    ConnectionStats stats = get_connection_stats();
                    hLogStatus.updateByteCount(stats.bytes_in,stats.bytes_out);
                }else if(getConfig().getServerType().equals(SERVER_TYPE_V2RAY) || getConfig().getServerType().equals(SERVER_TYPE_UDP_HYSTERIA_V1)){
                    m_ReceivedBytes += getUpDateBytes().getBytesReceived();
                    m_SentBytes += getUpDateBytes().getBytesSent();
                    hLogStatus.updateByteCount(m_ReceivedBytes,m_SentBytes);
                }else{
                    hLogStatus.updateByteCount(getUpDateBytes().getTotalBytesReceived(),getUpDateBytes().getTotalBytesSent());
                }
            }
        }catch (Exception ignored){}
    }

    private void doUpdateLayout(){
        boolean isRunning = hLogStatus.isTunnelActive();
        serverDialog.setEnabled(!isRunning);
        networkDialog.setEnabled(!isRunning);
        xUser.setEnabled(!isRunning);
        xPass.setEnabled(!isRunning);
        setupBTNanimation(isRunning);
    }

    private static final String APP_TOKEN = "TAMJ_APP_2026_SECRET";

    @Override

    public void updateState(String state, String logMessage, int localizedResId, ConnectionStatus level) {
        mHandler.post(() -> {
            isConnected = level.equals(ConnectionStatus.LEVEL_CONNECTED);
            status_view.setText(state);
            doUpdateLayout();

            if (isConnected) {
                bindDeviceOnConnect();
                // ✅ START HEARTBEAT ONLY HERE
                startPingLoop();

                showInterstitialAd(() -> {
                    if(getPref().getInt("loadOnce",0)==0) {
                        getEditor().putInt("loadOnce", 1).apply();

                        if (!getPref().getString("Server_message", "").isEmpty()){
                            addlogInfo(
                                    "<font color=#68B86B><br/><br/><b>SERVER INFORMATION!</b><br/><br/>"
                                            + getPref().getString("Server_message", "").replace("\n", "<br/>")
                                            + "<br/><br/>"
                            );
                        }

                        showExpireDate();
                        util.showToast(resString(R.string.app_name),
                                "Connected successfully, enjoy 😊");
                    }
                });

            } else {
                // ❌ STOP HEARTBEAT WHEN NOT CONNECTED
                stopPingLoop();
                getEditor().putInt("loadOnce", 0).apply();

                if(state.equals(resString(R.string.state_reconnecting))
                        && getPref().getBoolean("isRandom",false)) {
                    reLoad_Configs();
                }
            }
        });
    }

    @Override
    public void updateByteCount(long in, long out, long diffIn, long diffOut) {
        inValue = diffIn;
        outValue = diffOut;
        String in1 = ConfigUtil.render_bandwidth(diffIn,true);
        String ut1 = ConfigUtil.render_bandwidth(diffOut,true);
        byteIn_view.setText(ConfigUtil.render_bandwidth(in,false));
        byteOut_view.setText(ConfigUtil.render_bandwidth(out,false));
        mDataInTv.setText(in1);
        mDataOutTv.setText(ut1);
        mDataInTv.setTextColor(in1.equals("0 bit")?Color.parseColor("#9e9e9e"):Color.parseColor("#00FF6A"));
        mDataOutTv.setTextColor(ut1.equals("0 bit")?Color.parseColor("#9e9e9e"):Color.parseColor("#FF002E"));
        val1.setText(inValue+" bit");
        val2.setText(outValue+" bit");
        ((TextView)findViewById(R.id.livedata)).setText(in1.equals("0 bit")&&ut1.equals("0 bit")?"LiveData 🔴":"LiveData 🟢");
        ((TextView)findViewById(R.id.graph_net_type)).setText(util.getNetworkType());
    }

    float inValue = 0;
    float outValue = 0;
    private boolean _stop = false;
    private void generateValue() {
        if (!_stop){
            liveDataIn.addValue(inValue);
            liveDataOut.addValue(outValue);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        _stop = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        _stop = true;
    }

    private void clearDataGraph(){
        inValue = 0;
        outValue = 0;
        liveDataIn.clear();
        liveDataOut.clear();
    }

    private void loadIds(){
        liveDataIn = new LinearInView(this);
        liveDataOut = new LinearOutView(this);
        liveDataIn = findViewById(R.id.mDataIn);
        liveDataOut = findViewById(R.id.mDataOut);
        mDataInTv = findViewById(R.id.mDataInTv);
        mDataOutTv = findViewById(R.id.mDataOutTv);
        val2 = findViewById(R.id.val2);
        val1 = findViewById(R.id.val1);
        val1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,8);
        val2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,8);

        cBuiler = new AlertDialog.Builder(this).create();
        adContainerView = findViewById(R.id.ad_view_container);
        mDrawerMenu = findViewById(R.id.mDrawerMenu);
        loadMainDrawer();
        circleProgressBar = findViewById(R.id.circle_progress);
        mRotateLoading = findViewById(R.id.mRotateLoading);
        ac_xp = findViewById(R.id.ac_xp);
        xUser = findViewById(R.id.x_username);
        xPass = findViewById(R.id.x_password);
    timeLeftTv = findViewById(R.id.timeLeft);
    addTimeBtn = findViewById(R.id.addTime);
    addTimeBlock = findViewById(R.id.add_time_block);
    if (addTimeBtn != null) addTimeBtn.setOnClickListener(v -> onAddTimeClicked());
        s_name = findViewById(R.id._server_name);
        p_name = findViewById(R.id._tweak_name);
        serverDialog = findViewById(R.id.select_server);
        networkDialog = findViewById(R.id.select_network);
        btn_connector = findViewById(R.id.btn_connect);
        duration_view = findViewById(R.id.duration);
        byteIn_view = findViewById(R.id.bytes_in);
        byteOut_view = findViewById(R.id.bytes_out);
        status_view = findViewById(R.id.status);
        Config_vers = findViewById(R.id.config_version);
        tunnel_type = findViewById(R.id.tunnel_spin);
        logRecycle = findViewById(R.id.lRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new LogsAdapter(layoutManager, this);
        logRecycle.setAdapter(mAdapter);
        logRecycle.setLayoutManager(layoutManager);
        View bottomSheet = findViewById(R.id.log_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        showLog = findViewById(R.id.show_log_view);
        findViewById(R.id.status_log_menu).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(OpenVPNClient.this, v);
            popup.getMenu().add(0, 0, 0, "Clear logs");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==0){
                    mAdapter.clearLog();
                    v.setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    return false;
                }
                return true;
            });
            popup.show();
        });
        findViewById(R.id.log_view).setOnClickListener(v -> {
            if (showLog.getRotation() == 0) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    showLog.animate().setDuration(200).rotation(0);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    showLog.animate().setDuration(200).rotation(180);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        xUser.setText(getPref().getString("_screenUsername_key", ""));
        xUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String u = xUser.getText().toString().trim();
                if (!getConfig().getConfigIsAutoLogIn())
                    getEditor().putString("_screenUsername_key", u).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        xPass.setText(getPref().getString("_screenPassword_key", ""));
        xPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String u = xPass.getText().toString().trim();
                if (!getConfig().getConfigIsAutoLogIn() && !u.equals("******"))
                    getEditor().putString("_screenPassword_key", u).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mDrawerMenu.setOnClickListener(v -> {
            open();
        });
        findViewById(R.id.popmenu).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(OpenVPNClient.this, v);
            popup.getMenu().add(0, 0, 0, colorTitle("Release notes", "#FFFFFF"));
popup.getMenu().add(1, 1, 1, colorTitle("Telegram", "#FFFFFF"));
popup.getMenu().add(2, 2, 2, colorTitle("Exit all", "#FFFFFF"));
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==0){
                    mReleaseNotes("Release Notes",getPref().getString(RELEASE_NOTE,""));
                    return false;
                } else if (item.getItemId()==1){
                    getDEditor().putBoolean("join_tele",false).apply();
                    mFirstNotes();
                    return false;
                }
                 else if (item.getItemId()==2){
                    exitView();
                    return false;
                }
                return true;
            });
            popup.show();
        });
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.main);

        appUpdateHelper = new AppUpdateHelper(this, activityResultLauncher);
        appUpdateHelper.checkForUpdate();

        exported_config = new ConfigDataBase(OpenVPNClient.this, "ExportedConfigData");
        new util(OpenVPNClient.this);
        mHandler = new Handler();
        prefs = new PrefUtil(harliesApplication.getDefaultSharedPreferences());
        new PasswordUtil(harliesApplication.getDefaultSharedPreferences());
        doBindService();
        consent();
        LoadDefaultConfig();
        findViewById(R.id.main_window_bg).setBackgroundColor(getConfig().getMainLayoutBG());
        loadIds();
        loadV2RaySetups();
        /*mDNS = findViewById(R.id.dns_forward);
        mDNS.setOnClickListener(view -> {
            getConfig().setVpnDnsForward(mDNS.isChecked());
        });
        mVoid = findViewById(R.id.udp_forward);
        mVoid.setOnClickListener(view -> {
            getConfig().setVpnUdpForward(mVoid.isChecked());
        });*/
        serverDialog.setOnClickListener(OpenVPNClient.this);
        networkDialog.setOnClickListener(OpenVPNClient.this);
        btn_connector.setOnClickListener(OpenVPNClient.this);
        submitReloadProfileIntent(getPref().getString(SERVER_TYPE_OVPN,"[]"));

        googleMobileAdsConsentManager = AdsConsent.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(this, consentError -> {
            if (BuildConfig.DEBUG) {
                Log.d("%s: %s", consentError.getErrorCode() +consentError.getMessage());
            }
            if (googleMobileAdsConsentManager.canRequestAds()) {
                initializeMobileAdsSdk();
            }
        });
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }

        if (ActivityCompat.checkSelfPermission(OpenVPNClient.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1234);
        }
        // start countdown if previously added time exists
        long left = getPref().getLong(PREF_ADD_TIME_LEFT, 0);
        if (left > 0) addTimeHandler.post(addTimeCountdown);

        // preload rewarded ad
        loadRewardedAd();

        mFirstNotes();
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               exitView();
            }
        });

    }


    private void inboxNotification(int icon, String title, String msg, int ntfy) {
        Notification.Builder mBuilder = new Notification.Builder(OpenVPNClient.this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_icon))
                .setSmallIcon(icon)
                .setContentTitle("Message Received")
                .setContentText(msg)
                .setAutoCancel(true);
        Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
        inboxStyle.setBigContentTitle(title);
        inboxStyle.bigText(msg);
        mBuilder.setStyle(inboxStyle);
        Intent intent = getIntent();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(OpenVPNClient.this);
        stackBuilder.addNextIntent(intent);
        mBuilder.setContentIntent(ConfigUtil.getPendingIntent(OpenVPNClient.this));
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_userreq);
            NotificationChannel mChannel = new NotificationChannel("openvpn_userreq",name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(resString(R.string.channel_description_userreq));
            mChannel.enableVibration(true);
            mChannel.setLightColor(Color.CYAN);
            mBuilder.setChannelId("openvpn_userreq");
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        } else {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        }
        if (mNotificationManager != null) {
            mNotificationManager.notify(ntfy, mBuilder.build());
        }
    }

    private boolean isCheckUpdateIsRunning = false;
    private void autoUpdate(){
        if (!util.isNetworkAvailable(OpenVPNClient.this))return;
        isCheckUpdateIsRunning = true;
        String a = getPref().getString(CONFIG_URL,"");
        new checkUpdate(a, new checkUpdate.Listener() {
            @Override
            public void onError(String config) {
                isCheckUpdateIsRunning = false;
            }
            @Override
            public void onCompleted(final String config)
            {
                isCheckUpdateIsRunning = false;
                String mData = FileUtils.showJson(config);
                try{
                    exported_config.updateData("1", config);
                    JSONArray sjarr = new JSONArray();
                    JSONArray pjarr = new JSONArray();
                    JSONObject obj = new JSONObject(mData);
                    if (getConfig().getVersionCompare(obj.getString("Version"),getPref().getString(CONFIG_VERSION,"0"))){
                        if (addOrEditedServers().length()!=0)for (int i=0;i < addOrEditedServers().length();i++) {
                            sjarr.put(addOrEditedServers().getJSONObject(i));
                        }
                        if (obj.getJSONArray("Servers").length()!=0)for (int i=0;i < obj.getJSONArray("Servers").length();i++) {
                            sjarr.put(obj.getJSONArray("Servers").getJSONObject(i));
                        }
                        if (addOrEditedNetwork().length()!=0)for (int i=0;i < addOrEditedNetwork().length();i++) {
                            pjarr.put(addOrEditedNetwork().getJSONObject(i));
                        }
                        if (obj.getJSONArray("HTTPNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("HTTPNetworks").length();i++) {
                            pjarr.put(obj.getJSONArray("HTTPNetworks").getJSONObject(i));
                        }
                        if (obj.getJSONArray("SSLNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("SSLNetworks").length();i++) {
                            pjarr.put(obj.getJSONArray("SSLNetworks").getJSONObject(i));
                        }
                        getServerData().updateData("1", sjarr.toString());
                        getNetworkData().updateData("1", pjarr.toString());
                        loadServerArrayDragaPosition();
                        getEditor().putInt(SERVER_POSITION,0).apply();
                        getEditor().putInt(NETWORK_POSITION,0).apply();
                        getEditor().putString("CONFIG_FILE_NAME", obj.has("FileName")?obj.getString("FileName"):"Exported Config").apply();
                        getEditor().putString(CONFIG_VERSION, obj.getString("Version")).apply();
                        getEditor().putString(RELEASE_NOTE, obj.getString("ReleaseNotes")).apply();
                        getEditor().putString(CONTACT_SUPPORT, obj.getString("contactSupport")).apply();
                        getEditor().putString(OPEN_VPN_CERT, obj.getString("Ovpn_Cert")).apply();
                        getEditor().putString(CONFIG_URL,FileUtils.showJson(obj.getString("config_url"))).apply();
                        getEditor().putString(CONFIG_API,obj.has("account_api")?FileUtils.showJson(obj.getString("account_api")):"").apply();
                        getEditor().putString(UPLOAD_GET_API,obj.has("upload_get_api")?FileUtils.showJson(obj.getString("upload_get_api")):"").apply();
                        getEditor().putString(UPLOAD_POST_API,obj.has("upload_post_api")?FileUtils.showJson(obj.getString("upload_post_api")):"").apply();
                        getEditor().putString(CONFIG_EDITOR_CODE,obj.has("AppConfPass")?FileUtils.showJson(obj.getString("AppConfPass")):"").apply();
                        if(obj.has("JSONsettings"))getJSONsettings(obj.getJSONArray("JSONsettings").toString());
                        getEditor().putBoolean("isRandom", false).apply();
                        getEditor().putBoolean("isAdminAccept", false).apply();
                        doUpdateLayout();
                        loadConfigurations();
                        Config_vers.setText("Config ver: "+obj.getString("Version"));
                        inboxNotification(R.drawable.icon_icon,"New config release",obj.getString("ReleaseNotes"),3);
                        submitReloadProfileIntent(getPref().getString(SERVER_TYPE_OVPN,"[]"));
                    }
                }catch (Exception e){
                    isCheckUpdateIsRunning = false;
                }
            }
        }).start();
    }


    private void mUpdate(){
        new util(OpenVPNClient.this);
        isCheckUpdateIsRunning = true;
        String a = getPref().getString(CONFIG_URL,"");
        Toast.makeText(OpenVPNClient.this, "Checking Updates", Toast.LENGTH_LONG).show();
        new checkUpdate(a, new checkUpdate.Listener() {
            @Override
            public void onError(String config) {
                isCheckUpdateIsRunning = false;
                util.showToast("Oppss...!", config);
            }
            @Override
            public void onCompleted(final String config) {
                isCheckUpdateIsRunning = false;
                String mData = FileUtils.showJson(config);
                try{
                    exported_config.updateData("1", config);
                    JSONArray sjarr = new JSONArray();
                    JSONArray pjarr = new JSONArray();
                    JSONObject obj = new JSONObject(mData);
                    if (getConfig().getVersionCompare(obj.getString("Version"),getPref().getString(CONFIG_VERSION,"0"))){
                        if (addOrEditedServers().length()!=0)for (int i=0;i < addOrEditedServers().length();i++) {
                            sjarr.put(addOrEditedServers().getJSONObject(i));
                        }
                        if (obj.getJSONArray("Servers").length()!=0)for (int i=0;i < obj.getJSONArray("Servers").length();i++) {
                            sjarr.put(obj.getJSONArray("Servers").getJSONObject(i));
                        }
                        if (addOrEditedNetwork().length()!=0)for (int i=0;i < addOrEditedNetwork().length();i++) {
                            pjarr.put(addOrEditedNetwork().getJSONObject(i));
                        }
                        if (obj.getJSONArray("HTTPNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("HTTPNetworks").length();i++) {
                            pjarr.put(obj.getJSONArray("HTTPNetworks").getJSONObject(i));
                        }
                        if (obj.getJSONArray("SSLNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("SSLNetworks").length();i++) {
                            pjarr.put(obj.getJSONArray("SSLNetworks").getJSONObject(i));
                        }
                        getServerData().updateData("1", sjarr.toString());
                        getNetworkData().updateData("1", pjarr.toString());
                        loadServerArrayDragaPosition();
                        getEditor().putInt(SERVER_POSITION,0).apply();
                        getEditor().putInt(NETWORK_POSITION,0).apply();
                        getEditor().putString("CONFIG_FILE_NAME", obj.has("FileName")?obj.getString("FileName"):"Exported Config").apply();
                        getEditor().putString(CONFIG_VERSION, obj.getString("Version")).apply();
                        getEditor().putString(RELEASE_NOTE, obj.getString("ReleaseNotes")).apply();
                        getEditor().putString(CONTACT_SUPPORT, obj.getString("contactSupport")).apply();
                        getEditor().putString(OPEN_VPN_CERT, obj.getString("Ovpn_Cert")).apply();
                        getEditor().putString(CONFIG_URL,FileUtils.showJson(obj.getString("config_url"))).apply();
                        getEditor().putString(CONFIG_API,obj.has("account_api")?FileUtils.showJson(obj.getString("account_api")):"").apply();
                        getEditor().putString(UPLOAD_GET_API,obj.has("upload_get_api")?FileUtils.showJson(obj.getString("upload_get_api")):"").apply();
                        getEditor().putString(UPLOAD_POST_API,obj.has("upload_post_api")?FileUtils.showJson(obj.getString("upload_post_api")):"").apply();
                        getEditor().putString(CONFIG_EDITOR_CODE,obj.has("AppConfPass")?FileUtils.showJson(obj.getString("AppConfPass")):"").apply();
                        if(obj.has("JSONsettings"))getJSONsettings(obj.getJSONArray("JSONsettings").toString());
                        getEditor().putBoolean("isRandom", false).apply();
                        getEditor().putBoolean("isAdminAccept", false).apply();
                        doUpdateLayout();
                        loadConfigurations();
                        Config_vers.setText("Config ver: "+obj.getString("Version"));
                        submitReloadProfileIntent(getPref().getString(SERVER_TYPE_OVPN,"[]"));
                    }else{
                        Toast.makeText(OpenVPNClient.this, "No Update available!", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    isCheckUpdateIsRunning = false;
                    util.showToast("Error...!", e.getMessage());
                }
            }
        }).start();
    }

    /*private JSONObject xp_json() {
        try {
            JSONArray ServerList = new JSONArray(xp_servers.getData());
            JSONArray PayloadList = new JSONArray(xp_http.getData());
            JSONArray SSLList = new JSONArray(xp_ssl.getData());
            JSONArray AdvanceSettingsJS = settingsJSON();
            return new JSONObject(getPref().getString("Configuration", "{}"))
                    .put("FileName", "Exported Config")
                    .put("Version", getPref().getString(CONFIG_VERSION,"0"))
                    .put("ReleaseNotes",getPref().getString(RELEASE_NOTE,""))
                    .put("contactSupport",getPref().getString(CONTACT_SUPPORT,""))
                    .put("config_url",FileUtils.hideJson(getPref().getString(CONFIG_URL,"")))
                    .put("account_api",FileUtils.hideJson(getPref().getString(CONFIG_API,"")))
                    .put("cloud_get_api",FileUtils.hideJson(getPref().getString(UPLOAD_GET_API,"")))
                    .put("cloud_post_api",FileUtils.hideJson(getPref().getString(UPLOAD_POST_API,"")))
                    .put("AppConfPass",FileUtils.hideJson(getPref().getString(CONFIG_EDITOR_CODE,"")))
                    .put("Servers", ServerList)
                    .put("HTTPNetworks", PayloadList)
                    .put("SSLNetworks", SSLList)
                    .put("JSONsettings", AdvanceSettingsJS)
                    .put("Ovpn_Cert",getPref().getString(OPEN_VPN_CERT,""));
        } catch (JSONException e) {
            return null;
        }
    }
    private JSONArray settingsJSON() {
        try {
            String[] m_dnsResolvers = getConfig().getVpnDnsResolver();
            JSONArray jr = new JSONArray();
            JSONObject js = new JSONObject();
            js.put("mLocalPort",getConfig().getLocalPort());
            js.put("mAutoClearLog",getConfig().getAutoClearLog());
            js.put("mIsDisabledDelaySSH",getConfig().getIsDisabledDelaySSH());
            js.put("mCompression",getConfig().getCompression());
            js.put("mVpnDnsForward",getConfig().getVpnDnsForward());
            js.put("mVpnDnsResolver",m_dnsResolvers[0]+":"+m_dnsResolvers[1]);
            js.put("mVpnUdpForward",getConfig().getVpnUdpForward());
            js.put("mVpnUdpResolver",getConfig().getVpnUdpResolver());
            js.put("mSSHPinger","3");
            js.put("mPingServer",getConfig().getPingServer());
            js.put("mProxyAddress",getConfig().getProxyAddress());
            js.put("mReconnTime",getConfig().getReconnTime());
            js.put("mIsTetheringSubnet",getConfig().getIsTetheringSubnet());
            jr.put(js);
            return jr;
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }*/

    private void mImport(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT_FILE);
    }

    private boolean LoadDefaultConfig(){
        boolean showFirstTime = getPref().getBoolean("connect_first_time", true);
        if (Boolean.valueOf(showFirstTime).booleanValue()) {
            extractZipConfig();
            init_default_preferences(prefs);
            try {
                String data = FileUtils.readFromAsset(OpenVPNClient.this,"mtk.hs");
                JSONObject obj = new JSONObject(data);
                JSONArray pjarr = new JSONArray();
                if (obj.getJSONArray("HTTPNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("HTTPNetworks").length();i++) {
                    pjarr.put(obj.getJSONArray("HTTPNetworks").getJSONObject(i));
                }
                if (obj.getJSONArray("SSLNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("SSLNetworks").length();i++) {
                    pjarr.put(obj.getJSONArray("SSLNetworks").getJSONObject(i));
                }
                if (pjarr.length()==0){
                    getNetworkData().insertData("[]");
                }else if (pjarr.length()!=0){
                    getNetworkData().insertData(pjarr.toString());
                }
                getServerData().insertData(obj.getJSONArray("Servers").toString());
                loadServerArrayDragaPosition();
                exported_config.insertData("[]");
                getEditor().putString("CONFIG_FILE_NAME", obj.has("FileName")?obj.getString("FileName"):"Exported Config").apply();
                getEditor().putString(CONFIG_VERSION, obj.getString("Version")).apply();
                getEditor().putString(RELEASE_NOTE, obj.getString("ReleaseNotes")).apply();
                getEditor().putString(CONTACT_SUPPORT, obj.getString("contactSupport")).apply();
                getEditor().putString(OPEN_VPN_CERT, obj.getString("Ovpn_Cert")).apply();
                getEditor().putString(CONFIG_URL,FileUtils.showJson(obj.getString("config_url"))).apply();
                getEditor().putString(CONFIG_API,obj.has("account_api")?FileUtils.showJson(obj.getString("account_api")):"").apply();
                getEditor().putString(UPLOAD_GET_API,obj.has("upload_get_api")?FileUtils.showJson(obj.getString("upload_get_api")):"").apply();
                getEditor().putString(UPLOAD_POST_API,obj.has("upload_post_api")?FileUtils.showJson(obj.getString("upload_post_api")):"").apply();
                getEditor().putString(CONFIG_EDITOR_CODE,obj.has("AppConfPass")?FileUtils.showJson(obj.getString("AppConfPass")):"").apply();
                if(obj.has("JSONsettings"))getJSONsettings(obj.getJSONArray("JSONsettings").toString());
                getEditor().putBoolean("isRandom", false).apply();
                getEditor().putBoolean("isAdminAccept", false).apply();
                reLoad_Configs();
                getEditor().putBoolean("connect_first_time",false).apply();
                return true;
            } catch (Exception e) {
                util.showToast("LoadDefaultConfig Error!", e.getMessage());
            }
        }
        return false;
    }

private boolean checkConfiguration(){
    if(!util.isMyApp()){
        submitDisconnectIntent();
        util.showToast("Oppss...!", " " + resString(R.string.app_name));
        addlogInfo("<font color = #d50000>" + " " + resString(R.string.app_name));
        return false;
    }
    else if(!reLoad_Configs()){
        util.showToast("Oppss...!","Config load error!");
        return false;
    }
    // DO NOT require normal internet here.
    // Promo/no-load may only get internet after tunnel is up.
    return true;
}

    private void startOrStopTunnel() {
        getEditor().putInt("loadOnce",0).apply();
        m_SentBytes = 0;
        m_ReceivedBytes = 0;
        clearDataGraph();
        if (hLogStatus.isTunnelActive()){
            stopTunnelService();
            cancel_stats();
        }
        else{
            if (getConfig().getAutoClearLog())mAdapter.clearLog();
            if(checkConfiguration()){
                start_connect();
            }
        }
    }

    public void stopTunnelService(){
        m_SentBytes = 0;
        m_ReceivedBytes = 0;
        getEditor().putInt("loadOnce",0).apply();
        submitDisconnectIntent();
    }

    private void stop_service() {
        hLogStatus.removeStateListener(this);
        hLogStatus.removeByteCountListener(this);
    }

    private void stop() {
        v2rayRegisterUnregisterReceiver(false);
        stop_service();
        doUnbindService();
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hLogStatus.addStateListener(this);
        hLogStatus.addByteCountListener(this);
        if(hLogStatus.isTunnelActive())schedule_stats();
        if(!hLogStatus.isTunnelActive())clearDataGraph();
        if (isDrawerOpen())close();
        autoUpdate();
        loadConfigurations();
        ac_xp.setText(getPref().getString("_AccountXp",date));
        Config_vers.setText("Config ver: "+getPref().getString(CONFIG_VERSION,"1.1"));
        doUpdateLayout();
        ((TextView)findViewById(R.id.graph_net_type)).setText(util.getNetworkType());
        if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY))loadV2rayConfig();
        if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY))reloadV2RAY();
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        /*
        mDNS.setChecked(getConfig().getVpnDnsForward());
        mVoid.setChecked(getConfig().getVpnUdpForward());
        */
        if (adView != null) {
            adView.resume();
        }

    }


    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
        cancel_stats();
        stop();
    }



    public void onClick(View v) {
        int viewid = v.getId();
        if (viewid == R.id.btn_connect) {
            startOrStopTunnel();
        } else if (viewid == R.id.select_server) {
            startActivity(new Intent(OpenVPNClient.this, ConfigSpinnerAdapter.class).putExtra("mConfigType","0"));
        } else if (viewid == R.id.select_network) {
            startActivity(new Intent(OpenVPNClient.this, ConfigSpinnerAdapter.class).putExtra("mConfigType","1"));
        }
    }

    @Override
    public void startOpenVPN() {
        super.startOpenVPN();
        resolve_epki_alias_then_connect();
    }

    private void start_connect() {
    try {
        if (getConfig().getConfigIsAutoLogIn()) {
            long left = getPref().getLong(PREF_ADD_TIME_LEFT, 0);
            if (left <= 0) {
                View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_add_time, null);
                cBuiler = new AlertDialog.Builder(this).create();
                TextView title = inflate.findViewById(R.id.dialog_title);
                TextView msg = inflate.findViewById(R.id.dialog_message);
                Button cancelBtn = inflate.findViewById(R.id.dialog_cancel);
                Button watchBtn = inflate.findViewById(R.id.dialog_watch_ad);

                title.setText("Add Time Required");
                msg.setText("Your account time has expired. Watch a short ad to get 1 hour of usage.");

                cancelBtn.setOnClickListener(p1 -> cBuiler.dismiss());
                watchBtn.setOnClickListener(p1 -> {
                    cBuiler.dismiss();
                    onAddTimeClicked();
                });

                cBuiler.setView(inflate);
                cBuiler.setCancelable(true);
                cBuiler.show();
                return;
            }
        }
    } catch (Exception ignored) {}

    Intent intent = VpnService.prepare(this);
    if (intent != null) {
        try {
            startActivityForResult(intent, START_BIND_CALLED);
            return;
        } catch (ActivityNotFoundException ignored) {
            return;
        }
    }

    if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY) && removeServer()) {
        startTunnelService();
        return;
    }

    // DO NOT touch panel before VPN starts
    startTunnelService();
}
    private void bindDeviceOnConnect() {
        try {
            String api = (getPref().getString(CONFIG_API, "").isEmpty())
                    ? "http://panel.tamjph.com/api/users.php?username="
                    : getPref().getString(CONFIG_API, "");

            String user = getConfig().getSecureString(USERNAME_KEY);
            String pass = getConfig().getSecureString(PASSWORD_KEY);

            if (user.isEmpty() || pass.isEmpty()) return;

            String id = getHWID(this);
            String model = Build.MODEL;

            String url = api + user
                    + "&password=" + pass
                    + "&device_id=" + id
                    + "&device_model=" + model;

            new ExpireDate(url, null).start(); // fire & forget
        } catch (Exception ignored) {}
    }
    private void startTunnelService() {
    TunnelUtils.restartRotateAndRandom();
    schedule_stats();
    StatisticGraphData.getStatisticData().getDataTransferStats().startConnected();

    if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY)) {
        loadV2rayConfig();
    }

    // Start VPN first
    startService(new Intent(OpenVPNClient.this, HarlieService.class)
            .setAction(HarlieService.START_SERVICE));
}

    private OpenVPNService.Profile selected_profile() {
        OpenVPNService.ProfileList proflist = profile_list();
        if (proflist != null) {
            return proflist.get_profile_by_name(getConfig().getServerName());
        }
        return null;
    }
    private void resolve_epki_alias_then_connect() {
        resolveExternalPkiAlias(selected_profile(), OpenVPNClient.this::do_connect);
    }

    private void do_connect(String epki_alias) {
        String app_name = "net.openvpn.connect.android";
        prefs.set_string("n_username", getConfig().getSecureString(USERNAME_KEY));
        String username = getConfig().getSecureString(USERNAME_KEY);
        String password = getConfig().getSecureString(PASSWORD_KEY);
        String proxy_name = null;
        String server = null;
        String pk_password = null;
        String response = null;
        boolean is_auth_pwd_save = false;
        String profile_name = getConfig().getServerName();
        String vpn_proto = prefs.get_string("vpn_proto");
        String conn_timeout = prefs.get_string("conn_timeout");
        String compression_mode = prefs.get_string("compression_mode");
        String ipv6 = this.prefs.get_string("ipv6");
        submitConnectIntent(profile_name, server, vpn_proto, ipv6, conn_timeout, username, password, is_auth_pwd_save, pk_password, response, epki_alias, compression_mode, proxy_name, null, null, true, get_gui_version(app_name));
    }


    protected void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case START_BIND_CALLED:
                if (AppState.isDeviceBlocked()) {
                    util.showToast("Blocked", "This account is already used on another device");
                    return;
                }
                if (result == RESULT_OK) {
                    start_connect();
                    return;
                }
                return;
            case REQUEST_IMPORT_FILE:
                if (result == RESULT_OK) {
                    Uri uri = data.getData();
                    String mData = FileUtils.showJson(FileUtils.readTextUri(OpenVPNClient.this,uri));
                    try{
                        exported_config.updateData("1", FileUtils.readTextUri(OpenVPNClient.this,uri));
                        JSONArray sjarr = new JSONArray();
                        JSONArray pjarr = new JSONArray();
                        JSONObject obj = new JSONObject(mData);
                        if (getConfig().getVersionCompare(obj.getString("Version"),getPref().getString(CONFIG_VERSION,"0"))){
                            if (addOrEditedServers().length()!=0)for (int i=0;i < addOrEditedServers().length();i++) {
                                sjarr.put(addOrEditedServers().getJSONObject(i));
                            }
                            if (obj.getJSONArray("Servers").length()!=0)for (int i=0;i < obj.getJSONArray("Servers").length();i++) {
                                sjarr.put(obj.getJSONArray("Servers").getJSONObject(i));
                            }
                            if (addOrEditedNetwork().length()!=0)for (int i=0;i < addOrEditedNetwork().length();i++) {
                                pjarr.put(addOrEditedNetwork().getJSONObject(i));
                            }
                            if (obj.getJSONArray("HTTPNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("HTTPNetworks").length();i++) {
                                pjarr.put(obj.getJSONArray("HTTPNetworks").getJSONObject(i));
                            }
                            if (obj.getJSONArray("SSLNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("SSLNetworks").length();i++) {
                                pjarr.put(obj.getJSONArray("SSLNetworks").getJSONObject(i));
                            }
                            getServerData().updateData("1", sjarr.toString());
                            getNetworkData().updateData("1", pjarr.toString());
                            loadServerArrayDragaPosition();
                            getEditor().putInt(SERVER_POSITION,0).apply();
                            getEditor().putInt(NETWORK_POSITION,0).apply();
                            getEditor().putString("CONFIG_FILE_NAME", obj.has("FileName")?obj.getString("FileName"):"Exported Config").apply();
                            getEditor().putString(CONFIG_VERSION, obj.getString("Version")).apply();
                            getEditor().putString(RELEASE_NOTE, obj.getString("ReleaseNotes")).apply();
                            getEditor().putString(CONTACT_SUPPORT, obj.getString("contactSupport")).apply();
                            getEditor().putString(OPEN_VPN_CERT, obj.getString("Ovpn_Cert")).apply();
                            getEditor().putString(CONFIG_URL,FileUtils.showJson(obj.getString("config_url"))).apply();
                            getEditor().putString(CONFIG_API,obj.has("account_api")?FileUtils.showJson(obj.getString("account_api")):"").apply();
                            getEditor().putString(UPLOAD_GET_API,obj.has("upload_get_api")?FileUtils.showJson(obj.getString("upload_get_api")):"").apply();
                            getEditor().putString(UPLOAD_POST_API,obj.has("upload_post_api")?FileUtils.showJson(obj.getString("upload_post_api")):"").apply();
                            getEditor().putString(CONFIG_EDITOR_CODE,obj.has("AppConfPass")?FileUtils.showJson(obj.getString("AppConfPass")):"").apply();
                            if(obj.has("JSONsettings"))getJSONsettings(obj.getJSONArray("JSONsettings").toString());
                            getEditor().putBoolean("isRandom", false).apply();
                            getEditor().putBoolean("isAdminAccept", false).apply();
                            Config_vers.setText("Config ver: "+getPref().getString(CONFIG_VERSION,"1.1"));
                            loadConfigurations();
                            submitReloadProfileIntent(getPref().getString(SERVER_TYPE_OVPN,"[]"));
                            if (HarlieService.isVPNRunning())stopTunnelService();
                        }else{
                            Toast.makeText(OpenVPNClient.this, "No Update available!", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                        util.showToast("Error...!", e.getMessage());
                    }
                    if (HarlieService.isVPNRunning())stopTunnelService();
                    return;
                }
                recreate();
                return;
            default:
                super.onActivityResult(request, result, data);
        }
    }

    private void loadConfigurations() {
        if(reLoad_Configs()){
            try {
                if (networkArrayDragaPosition().length()==0){
                    networkDialog.setVisibility(View.GONE);
                }else if (serverArrayDragaPosition(mSType(networkArrayDragaPosition().getJSONObject(getPref().getInt(NETWORK_POSITION, 0)))).length()==0){
                    serverDialog.setVisibility(View.GONE);
                    networkDialog.setVisibility(View.GONE);
                }else{
                    JSONObject js1 = networkArrayDragaPosition().getJSONObject(getPref().getInt(NETWORK_POSITION, 0));
                    p_name.setText(js1.getString("Name"));
                    TextView ptv4 = findViewById(R.id.tvNetworkInfo);
                    ptv4.setText(getNetworkType(js1));
                    InputStream open1;
                    if (js1.has("serverType")){
                        open1 = getAssets().open("flags/" + "flag_" + js1.getString("FLAG") + ".png");
                    }else{
                        open1 = getAssets().open("networks/" + "icon_" + js1.getString("FLAG") + ".png");
                    }
                    ((ImageView)findViewById(R.id.p_icon)).setImageDrawable(Drawable.createFromStream(open1, null));
                    tunnel_type.setText(getTunnelType(js1));
                    p_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
                    ptv4.setTextSize(TypedValue.COMPLEX_UNIT_DIP,7);

                    JSONObject js2 = serverArrayDragaPosition(mSType(js1)).getJSONObject(getPref().getInt(SERVER_POSITION, 0));
                    s_name.setText(js2.getString("Name"));
                    TextView stv2 = findViewById(R.id.tvServerInfo);
                    stv2.setText(getServerType(js2));
                    InputStream open2 = getAssets().open("flags/" + "flag_" + js2.getString("FLAG") + ".png");
                    ((ImageView)findViewById(R.id.s_icon)).setImageDrawable(Drawable.createFromStream(open2, null));
                    s_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
                    stv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,7);
                    if(getPref().getBoolean("isRandom",false) && getPref().getBoolean("show_random_layout", false)){
                        s_name.setText("AUTO SERVER");
                        stv2.setText("Random");
                        ((ImageView)findViewById(R.id.s_icon)).setImageResource(R.drawable.ic_server);
                    }
                    serverDialog.setVisibility(isHidePayloadTweak());
                    networkDialog.setVisibility(View.VISIBLE);
                    if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY)){
                        tunnel_type.setText("v2ray|Xray");
                        //tunnel_type.setTextColor(Color.parseColor("#8A5746"));
                    }
                    findViewById(R.id.account_ly).setVisibility(getConfig().getConfigIsAutoLogIn()? View.GONE : View.VISIBLE);
                    findViewById(R.id.ac_xp_tl).setVisibility(getConfig().getConfigIsAutoLogIn()? View.GONE : View.VISIBLE);
                    ac_xp.setVisibility(getConfig().getConfigIsAutoLogIn()? View.GONE : View.VISIBLE);
                    // Add-time UI: show only when server is autologin (toggle entire block)
                    if (addTimeBlock != null){
                        addTimeBlock.setVisibility(getConfig().getConfigIsAutoLogIn()? View.VISIBLE: View.GONE);
                    }
                    open1.close();
                    open2.close();
                }
            } catch (Exception e) {
                tunnel_type.setText(e.toString());
                serverDialog.setVisibility(View.GONE);
                networkDialog.setVisibility(View.GONE);
            }
        }else{
            serverDialog.setVisibility(View.GONE);
            networkDialog.setVisibility(View.GONE);
        }
    }

    private String getServerType(JSONObject js) throws JSONException {
        if (js.has("Server_msg")){
            if(js.getString("Server_msg").isEmpty()){
                if (js.getInt("serverType")==0){
                    return "OpenVPN";
                }else if (js.getInt("serverType")==1){
                    return "SSH Tunnel";
                } else if (js.getInt("serverType")==2){
                    return "DNSTT";
                } else if (js.getInt("serverType")==3){
                    return "v2ray";
                } else if (js.getInt("serverType")==4){
                    return "Hysteria UDP";
                }
            }else{
                return js.getString("Server_msg");
            }
        }else if (js.getInt("serverType")==0){
            return "OpenVPN";
        }else if (js.getInt("serverType")==1){
            return "SSH Tunnel";
        } else if (js.getInt("serverType")==2){
            return "DNSTT";
        } else if (js.getInt("serverType")==3){
            return "v2ray";
        } else if (js.getInt("serverType")==4){
            return "Hysteria UDP";
        }
        return "Unknown!";
    }

    private int isHidePayloadTweak(){
        if (networkArrayDragaPosition().length()==0){
            return View.GONE;
        }
        if (getConfig().getServerType().equals(SERVER_TYPE_V2RAY)){
            return View.GONE;
        }
        return View.VISIBLE;
    }

    private String getTunnelType(JSONObject js) throws JSONException {
        if (js.has("serverType")){
            return "v2ray";
        }else{
            boolean is = (js.getString("Name").contains("Direct")||js.getString("Name").contains("direct"));
            if (js.getInt("proto_spin") == 0) {
                if (is){
                    if (js.getString("NetworkPayload").isEmpty()){
                        //tunnel_type.setTextColor(Color.parseColor("#ffb71c1c"));
                        return "Direct";
                    }else{
                        //tunnel_type.setTextColor(Color.parseColor("#ffff1744"));
                        return "Direct Payload";
                    }
                }
                //tunnel_type.setTextColor(Color.parseColor("#ff6200ea"));
                return "TCP|PROXY";
            } else if (js.getInt("proto_spin") == 1) {
                //tunnel_type.setTextColor(Color.parseColor("#ff0091ea"));
                return "HYSTERIA";
            } else if (js.getInt("proto_spin") == 2) {
                return "SLOWDNS";
            }else if (js.getInt("proto_spin") == 3) {
                //tunnel_type.setTextColor(Color.parseColor("#ff00c853"));
                return "TCP|SSL|TLS";
            } else if (js.getInt("proto_spin") == 4) {
                //tunnel_type.setTextColor(Color.parseColor("#ffc51162"));
                return "SSL|PAYLOAD";
            } else if (js.getInt("proto_spin") == 5) {
                //tunnel_type.setTextColor(Color.parseColor("#ff2962ff"));
                return "SSL|PROXY";
            }
        }
        return "Unknown!";
    }
    private String getNetworkType(JSONObject js) throws JSONException {
        if (js.has("Info")){
            if(js.getString("Info").isEmpty()){
                if (js.has("serverType")){
                    return "v2ray";
                }else{
                    boolean is = (js.getString("Name").contains("Direct")||js.getString("Name").contains("direct"));
                    if (js.getInt("proto_spin") == 0) {
                        if (is){
                            if (js.getString("NetworkPayload").isEmpty()){
                                return "Direct";
                            }else{
                                return "Direct Payload";
                            }
                        }
                        return "HTTP PROXY";
                    } else if (js.getInt("proto_spin") == 1) {
                        return "UDP HYSTERIA";
                    } else if (js.getInt("proto_spin") == 2) {
                        return "SLOWDNS";
                    } else if (js.getInt("proto_spin") == 3) {
                        return "SSL/SNI";
                    } else if (js.getInt("proto_spin") == 4) {
                        return "SSL+PAYLOAD";
                    } else if (js.getInt("proto_spin") == 5) {
                        return "SSL+PROXY";
                    }
                }
            } else{
                return js.getString("Info");
            }
        }else if (js.has("serverType")){
            return "v2ray";
        }else{
            boolean is = (js.getString("Name").contains("Direct")||js.getString("Name").contains("direct"));
            if (js.getInt("proto_spin") == 0) {
                if (is){
                    if (js.getString("NetworkPayload").isEmpty()){
                        return "Direct";
                    }else{
                        return "Direct Payload";
                    }
                }
                return "HTTP PROXY";
            } else if (js.getInt("proto_spin") == 1) {
                return "UDP HYSTERIA";
            } else if (js.getInt("proto_spin") == 2) {
                return "SLOWDNS";
            }else if (js.getInt("proto_spin") == 3) {
                return "SSL/SNI";
            } else if (js.getInt("proto_spin") == 4) {
                return "SSL+PAYLOAD";
            } else if (js.getInt("proto_spin") == 5) {
                return "SSL+PROXY";
            }
        }
        return "Unknown!";
    }
    private void getJSONsettings(String obj){
        try{
            JSONArray jarr = new JSONArray(obj.trim());
            for (int i=0;i < jarr.length();i++) {
                JSONObject js = jarr.getJSONObject(i);
                getConfig().setLocalPort(js.getString("mLocalPort"));
                getConfig().setAutoClearLog(js.getBoolean("mAutoClearLog"));
                getConfig().setDisabledDelaySSH(js.getBoolean("mIsDisabledDelaySSH"));
                getConfig().setCompression(js.getBoolean("mCompression"));
                getConfig().setVpnDnsForward(js.getBoolean("mVpnDnsForward"));
                getConfig().setVpnDnsResolver(js.getString("mVpnDnsResolver"));
                getConfig().setVpnUdpForward(js.getBoolean("mVpnUdpForward"));
                getConfig().setVpnUdpResolver(js.getString("mVpnUdpResolver"));
                getConfig().setPingThread(Integer.parseInt(js.getString("mSSHPinger").isEmpty()?"3":js.getString("mSSHPinger")));
                getConfig().setPingServer(js.getString("mPingServer"));
                getConfig().setProxyAddress(js.getString("mProxyAddress"));
                getConfig().setReconnTime(js.getInt("mReconnTime"));
                getConfig().setTetheringSubnet(js.getBoolean("mIsTetheringSubnet"));
            }
        } catch (JSONException e) {
            util.showToast("getJSONsettings Error!", e.getMessage());
        }
    }


    private void setupBTNanimation(boolean isRunning){
        if (isRunning && !isConnected){
            if (!mRotateLoading.isStart()){
                mRotateLoading.start();
                circleProgressBar.setProgressWithAnimation(0);
            }
        }
        if (isConnected){
            if (mRotateLoading.isStart())mRotateLoading.stop();
            circleProgressBar.setProgressWithAnimation(100);
        }
        if (!isRunning){
            circleProgressBar.setProgressWithAnimation(0);
            if (mRotateLoading.isStart())mRotateLoading.stop();
        }
        //status_view.setTextColor(isConnected? Color.parseColor("#ff36ff00"):Color.BLACK);
        if (!getConfig().getIsScreenOn() || !util.isNetworkAvailable(OpenVPNClient.this)){
            //findViewById(R.id.b_progress).setVisibility(View.GONE);
            clearAllDataAnim(!isRunning);
            /*mDNS.setEnabled(true);
            mVoid.setEnabled(true);*/
        }else {
            if (isConnected){
                //btn_connector.setText("STOP");
                /*mDNS.setEnabled(false);
                mVoid.setEnabled(false);*/
                //findViewById(R.id.b_progress).setVisibility(View.GONE);
            }else{
                if (isRunning){
                    //btn_connector.setText("STOP");
                    /*mDNS.setEnabled(false);
                    mVoid.setEnabled(false);*/
                    //findViewById(R.id.b_progress).setVisibility(View.VISIBLE);
                }else{
                    //btn_connector.setText("START");
                    //findViewById(R.id.b_progress).setVisibility(View.GONE);
                    clearAllDataAnim(true);
                    /*mDNS.setEnabled(true);
                    mVoid.setEnabled(true);*/
                }
            }
        }
    }
    private void clearAllDataAnim(boolean isRunning){
        if (isRunning){
            clearAllTestDelay();
        }
    }


    public DrawerLayout mDrawerLayout;
    private NavigationView drawerNavigationView;

    private void loadMainDrawer() {
        drawerNavigationView = findViewById(R.id.drawerNavigationView);
        mDrawerLayout = findViewById(R.id.drawerLayoutMain);
        View v = drawerNavigationView.getHeaderView(0);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                            @Override
                                            public void onDrawerSlide(View drawerView, float slideOffset) {
                                            }
                                            @Override
                                            public void onDrawerOpened(View view) {
                                            }
                                            @Override
                                            public void onDrawerClosed(View drawerView) {
                                            }
                                        }
        );
        drawerNavigationView.setNavigationItemSelectedListener(this);
    }

    public boolean isDrawerOpen(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            return true;
        }
        return false;
    }

    public void close(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }
    public void open(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.options) {

            showInterstitialAd(() -> {
                if (!hLogStatus.isTunnelActive()) {
                    close();
                    startActivity(new Intent(OpenVPNClient.this, advanceSettings.class));
                }
            });

        } else if (id == R.id.item_app) {

            showInterstitialAd(() -> {
                close();
                getConfig().launchMarketDetails();
            });

        } else if (id == R.id.item_checkupdates) {

            showInterstitialAd(() -> {
                close();
                mUpdate();
            });

        } else if (id == R.id.item_paste) {

            showInterstitialAd(() -> {
                close();
                mPaste();
            });

        } else if (id == R.id.item_im) {
            close();
            mImport();
        }/* else if (id == R.id.item_ex) {
            close();
            exportDialog();
        }*/
        return true;
    }

    private String str = "";
    public String save(String fileName,String content) {
        str = "Ducuments/MTK/"+fileName+".hs";
        executor.execute(() -> {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/MTK");
            dir.mkdirs();
            File file=new File(dir,fileName+".hs");
            try (Writer os = new OutputStreamWriter(new FileOutputStream(file))) {
                os.write(content);
                os.flush();
                os.close();
                str = dir+"/"+fileName+".hs";
            }
            catch (Throwable e) {
                str = e.getMessage();
            }
        });
        return str;
    }

    private void mPaste(){
        if (cBuiler != null) if (cBuiler.isShowing()) cBuiler.dismiss();
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification_dialog, null);
        cBuiler = new AlertDialog.Builder(this).create();
        ((AppCompatImageView)inflate.findViewById(R.id.notification_icon)).setImageResource(R.drawable.icon_icon);
        ((TextView)inflate.findViewById(R.id.notification_title)).setText(resString(R.string.app_name));
        ((TextView)inflate.findViewById(R.id.notification_message)).setText("This option is Custom Update for add Server or \nNetwork");
        TextView no = inflate.findViewById(R.id.notification_btn_no);
        TextView yes = inflate.findViewById(R.id.notification_btn_yes);
        no.setText("Exit");
        yes.setText("Paste");
        no.setOnClickListener(p1 -> {
            cBuiler.dismiss();
        });
        yes.setOnClickListener(p1 -> {
            String clipData = FileUtils.getClipboard(OpenVPNClient.this);
            if(clipData.isEmpty()){
                util.showToast("Error!", "Config Clipboard is empty!");
                return;
            }
            String mData = FileUtils.showJson(clipData);
            try{
                exported_config.updateData("1", clipData);
                JSONArray sjarr = new JSONArray();
                JSONArray pjarr = new JSONArray();
                JSONObject obj = new JSONObject(mData);
                if (getConfig().getVersionCompare(obj.getString("Version"),getPref().getString(CONFIG_VERSION,"0"))){
                    if (addOrEditedServers().length()!=0)for (int i=0;i < addOrEditedServers().length();i++) {
                        sjarr.put(addOrEditedServers().getJSONObject(i));
                    }
                    if (obj.getJSONArray("Servers").length()!=0)for (int i=0;i < obj.getJSONArray("Servers").length();i++) {
                        sjarr.put(obj.getJSONArray("Servers").getJSONObject(i));
                    }
                    if (addOrEditedNetwork().length()!=0)for (int i=0;i < addOrEditedNetwork().length();i++) {
                        pjarr.put(addOrEditedNetwork().getJSONObject(i));
                    }
                    if (obj.getJSONArray("HTTPNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("HTTPNetworks").length();i++) {
                        pjarr.put(obj.getJSONArray("HTTPNetworks").getJSONObject(i));
                    }
                    if (obj.getJSONArray("SSLNetworks").length()!=0)for (int i=0;i < obj.getJSONArray("SSLNetworks").length();i++) {
                        pjarr.put(obj.getJSONArray("SSLNetworks").getJSONObject(i));
                    }
                    getServerData().updateData("1", sjarr.toString());
                    getNetworkData().updateData("1", pjarr.toString());
                    loadServerArrayDragaPosition();
                    getEditor().putInt(SERVER_POSITION,0).apply();
                    getEditor().putInt(NETWORK_POSITION,0).apply();
                    getEditor().putString("CONFIG_FILE_NAME", obj.has("FileName")?obj.getString("FileName"):"Exported Config").apply();
                    getEditor().putString(CONFIG_VERSION, obj.getString("Version")).apply();
                    getEditor().putString(RELEASE_NOTE, obj.getString("ReleaseNotes")).apply();
                    getEditor().putString(CONTACT_SUPPORT, obj.getString("contactSupport")).apply();
                    getEditor().putString(OPEN_VPN_CERT, obj.getString("Ovpn_Cert")).apply();
                    getEditor().putString(CONFIG_URL,FileUtils.showJson(obj.getString("config_url"))).apply();
                    getEditor().putString(CONFIG_API,obj.has("account_api")?FileUtils.showJson(obj.getString("account_api")):"").apply();
                    getEditor().putString(UPLOAD_GET_API,obj.has("upload_get_api")?FileUtils.showJson(obj.getString("upload_get_api")):"").apply();
                    getEditor().putString(UPLOAD_POST_API,obj.has("upload_post_api")?FileUtils.showJson(obj.getString("upload_post_api")):"").apply();
                    getEditor().putString(CONFIG_EDITOR_CODE,obj.has("AppConfPass")?FileUtils.showJson(obj.getString("AppConfPass")):"").apply();
                    if(obj.has("JSONsettings"))getJSONsettings(obj.getJSONArray("JSONsettings").toString());
                    getEditor().putBoolean("isRandom", false).apply();
                    getEditor().putBoolean("isAdminAccept", false).apply();
                    loadConfigurations();
                    Config_vers.setText("Config ver: "+getPref().getString(CONFIG_VERSION,"1.1"));
                    submitReloadProfileIntent(getPref().getString(SERVER_TYPE_OVPN,"[]"));
                    if (HarlieService.isVPNRunning())stopTunnelService();
                    cBuiler.dismiss();
                }else{
                    Toast.makeText(OpenVPNClient.this, "No Update available!", Toast.LENGTH_LONG).show();
                    cBuiler.dismiss();
                }
            }catch (Exception e){
                util.showToast("Error...!", e.getMessage());
            }
        });
        cBuiler.setView(inflate);
        cBuiler.setCancelable(false);
        cBuiler.show();
    }
        private CharSequence colorTitle(String text, String colorHex) {
    android.text.SpannableString s = new android.text.SpannableString(text);
    s.setSpan(
            new android.text.style.ForegroundColorSpan(android.graphics.Color.parseColor(colorHex)),
            0,
            s.length(),
            0
    );
    return s;
}

    private void mReleaseNotes(String title,final String message){
        if (cBuiler != null) if (cBuiler.isShowing()) cBuiler.dismiss();
        View inflate = LayoutInflater.from(OpenVPNClient.this).inflate(R.layout.notification_layout, null);
        cBuiler = new AlertDialog.Builder(OpenVPNClient.this).create();
        AppCompatTextView ms1 = inflate.findViewById(R.id.log_title);
        AppCompatTextView ms2 = inflate.findViewById(R.id.log_message);
        inflate.findViewById(R.id.notif_dismiss_btn).setOnClickListener(p1 -> {
            cBuiler.dismiss();
        });
        ms1.setText(title);
        ms2.setText(message);
        cBuiler.setView(inflate);
        cBuiler.setCancelable(false);
        cBuiler.show();
    }
    private void exportDialog(){
        if (cBuiler != null) if (cBuiler.isShowing()) cBuiler.dismiss();
        View inflate = LayoutInflater.from(this).inflate(R.layout.notification_dialog, null);
        cBuiler = new AlertDialog.Builder(this).create();
        ((AppCompatImageView)inflate.findViewById(R.id.notification_icon)).setImageResource(R.drawable.icon_icon);
        ((TextView)inflate.findViewById(R.id.notification_title)).setText(resString(R.string.app_name));
        ((TextView)inflate.findViewById(R.id.notification_message)).setText("Are you sure you want to File Export?");
        TextView no = inflate.findViewById(R.id.notification_btn_no);
        TextView yes = inflate.findViewById(R.id.notification_btn_yes);
        no.setText("No");
        yes.setText("Yes");
        no.setOnClickListener(p1 -> {
            cBuiler.dismiss();
        });
        yes.setOnClickListener(p1 -> {
            try {
                String saveFile = save(getPref().getString("CONFIG_FILE_NAME",""),exported_config.getData());
                util.showToast(resString(R.string.app_name), "Config save at "+saveFile);
            } catch (Exception e) {
                Toast.makeText(OpenVPNClient.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            cBuiler.dismiss();
        });
        cBuiler.setView(inflate);
        cBuiler.setCancelable(false);
        cBuiler.show();
    }
    private void mFirstNotes(){
        if (cBuiler != null) if (cBuiler.isShowing()) cBuiler.dismiss();
        if (!getDPrefs().getBoolean("join_tele", false)){
            View inflate = LayoutInflater.from(this).inflate(R.layout.notification_dialog, null);
            cBuiler = new AlertDialog.Builder(this).create();
            ((TextView)inflate.findViewById(R.id.notification_message)).setText("We have a Telegram support channel where we post\n" +
                    "and discuss about Settings, new Features, and also\n" +
                    "assist our Users.\n" +
                    "Would you like to join us there?");
            inflate.findViewById(R.id.notification_btn_no).setOnClickListener(p1 -> {
                getDEditor().putBoolean("join_tele",false).apply();
                cBuiler.dismiss();
            });
            inflate.findViewById(R.id.notification_btn_yes).setOnClickListener(p1 -> {
                getDEditor().putBoolean("join_tele",true).apply();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+snf3in9ZwfAwZTA1"));
                    startActivity(Intent.createChooser(intent, "launch Telegram"));
                } catch (Exception e) {
                    util.showToast("Error","Please download the Telegram app");
                }
                cBuiler.dismiss();
            });
            cBuiler.setView(inflate);
            cBuiler.setCancelable(false);
            cBuiler.show();
        }
    }


    private AdView adView;
    private FrameLayout adContainerView;
    private InterstitialAd interstitialAd;
    private boolean adIsLoading;
    // add-time feature fields
    private TextView timeLeftTv;
    private Button addTimeBtn;
    private View addTimeBlock;
    private RewardedAd rewardedAd;
    private boolean rewardedAdLoading = false;
    private final String PREF_ADD_TIME_LEFT = "pref_add_time_left"; // milliseconds
    private final Handler addTimeHandler = new Handler();
    private final Runnable addTimeCountdown = new Runnable() {
        @Override
        public void run() {
            long left = getPref().getLong(PREF_ADD_TIME_LEFT, 0);
            if (left > 0) {
                // Only decrement the remaining time while the VPN tunnel is active.
                // If not active, keep the handler running so it can resume when the tunnel starts.
                if (hLogStatus.isTunnelActive()) {
                    left -= 1000;
                    if (left < 0) left = 0;
                    getEditor().putLong(PREF_ADD_TIME_LEFT, left).apply();
                    updateTimeLeftView(left);
                    if (left > 0) {
                        addTimeHandler.postDelayed(this, 1000);
                    }
                } else {
                    // VPN not active: update UI and check again in 1s without decrementing
                    updateTimeLeftView(left);
                    addTimeHandler.postDelayed(this, 1000);
                }
            } else {
                updateTimeLeftView(0);
            }
        }
    };

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().build());

        new Thread(() -> {
            MobileAds.initialize(this, initializationStatus -> {});
            runOnUiThread(() -> {
                loadBanner();
                setupInterstitial();
            });
        }).start();
    }

    private void loadBanner() {
        adView = new AdView(this);
        adView.setAdUnitId(harliesApplication.resString(R.string.adunit_banner));
        adView.setAdSize(AdSize.BANNER);
        adContainerView.removeAllViews();
        adContainerView.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void showInterstitialAd(Runnable onAdDismissed) {
        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    onAdDismissed.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    Log.d("TAG", "The ad failed to show.");
                    onAdDismissed.run();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d("TAG", "The ad was shown.");
                }
            });

            interstitialAd.show(this);
        } else {
            onAdDismissed.run(); // Proceed if no ad is available
            if (googleMobileAdsConsentManager.canRequestAds()) {
                setupInterstitial();
            }
        }
    }

    private void setupInterstitial() {

        if (adIsLoading || interstitialAd != null) {
            return;
        }
        adIsLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(OpenVPNClient.this, harliesApplication.resString(R.string.adunit_interstitial), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        OpenVPNClient.this.interstitialAd = interstitialAd;
                        adIsLoading = false;
                        Log.i("OpenVPNClient", "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        OpenVPNClient.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                        OpenVPNClient.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("OpenVPNClient", loadAdError.getMessage());
                        interstitialAd = null;
                        adIsLoading = false;

                        String error =
                                String.format(
                                        java.util.Locale.US,
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(),
                                        loadAdError.getCode(),
                                        loadAdError.getMessage());
                        Log.d("OpenVPNClient", "onAdFailedToLoad() with error: " + error);
                    }
                });
    }

    private void exitView() {
        if (cBuiler != null) {
            if (cBuiler.isShowing()) {
                cBuiler.dismiss();
            }
        }
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        View inflate = LayoutInflater.from(OpenVPNClient.this).inflate(R.layout.notification_dialog, null);
        cBuiler = new AlertDialog.Builder(OpenVPNClient.this).create();
        ((AppCompatImageView)inflate.findViewById(R.id.notification_icon)).setImageResource(R.drawable.icon_icon);
        ((TextView)inflate.findViewById(R.id.notification_title)).setText("Exit/Minimize");
        ((TextView)inflate.findViewById(R.id.notification_message)).setText("Do you want to minimize or exit?");
        TextView no = inflate.findViewById(R.id.notification_btn_no);
        TextView yes = inflate.findViewById(R.id.notification_btn_yes);
        no.setText("Minimize");
        yes.setText("Exit");
        no.setOnClickListener(p1 -> {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            cBuiler.dismiss();
        });
        yes.setOnClickListener(p1 -> {
            cBuiler.dismiss();
            if (HarlieService.isVPNRunning()) {
                stopTunnelService();
            }
            finishAffinity();
            System.exit(0);
        });

        cBuiler.setView(inflate);
        cBuiler.setCancelable(true);
        cBuiler.show();
    }

    private String date = "Expiry: --/--/-- | 0 Days";

    private void showExpireDate() {

        String api = "http://panel.tamjph.com/api/expiry.php?username=";
        String user = getConfig().getSecureString(USERNAME_KEY);
        String pass = getConfig().getSecureString(PASSWORD_KEY);

        if (user.isEmpty() || pass.isEmpty()) return;

        ac_xp.setVisibility(View.VISIBLE);
        ac_xp.setTextColor(getConfig().getColorAccent());

        String model = Build.MODEL;
        String id = getHWID(this); // ✅ FIXED

        String jsonUrl = api + user
                + "&password=" + pass
                + "&device_id=" + id
                + "&device_model=" + model;

        new ExpireDate(jsonUrl, new ExpireDate.ExpireDateListener() {

            @Override
            public void onExpireDate(String expiry) {

                if (expiry == null || expiry.isEmpty() || expiry.equals("none")) {
                    ac_xp.setText("Account expired");
                    ac_xp.setTextColor(Color.RED);
                    return;
                }

                date = "Expiry: " + getExpireDate(expiry)
                        + " | " + getDaysLeft(expiry);

                ac_xp.setText(date);
                ac_xp.setTextColor(getConfig().getColorAccent());
            }

            @Override
            public void onDeviceNotMatch() {
                runOnUiThread(() -> {
                    ac_xp.setText("This account is used on another device!");
                    ac_xp.setTextColor(Color.RED);

                    // 🔥 HARD KILL VPN
                    stopTunnelService();

                    // Optional: block reconnect
                    AppState.setDeviceBlocked(true);
                });
            }

            @Override
            public void onAuthFailed() {
                ac_xp.setText("Authentication failed");
                ac_xp.setTextColor(Color.RED);
            }

            @Override
            public void onError() {
                ac_xp.setText("Expiry date failed to load!");
                ac_xp.setTextColor(Color.RED);
            }

        }).start();
    }

    public static String getHWID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                "device_id", Context.MODE_PRIVATE);

        String id = sp.getString("hwid", null);

        if (id == null) {
            id = UUID.randomUUID().toString()
                    .replace("-", "")
                    .toUpperCase(Locale.US);
            sp.edit().putString("hwid", id).apply();
        }
        return id;
    }
    public static String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                StringBuilder toHexString = new StringBuilder(Integer.toHexString(b & 255));
                while (toHexString.length() < 2) {
                    toHexString.insert(0, "0");
                }
                stringBuilder.append(toHexString);
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getDaysLeft(String thatDate) {
        String[] split = thatDate.split("-");
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(split[0]),
                Integer.parseInt(split[1]) - 1,
                Integer.parseInt(split[2]));

        long days = (c.getTimeInMillis()
                - Calendar.getInstance().getTimeInMillis())
                / 86400000;

        return days + " Days";
    }

    private String getExpireDate(String date) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat out = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            return out.format(in.parse(date));
        } catch (Exception e) {
            return date;
        }
    }
    private void showXpDialog(String str) {
        if (HarlieService.isVPNRunning())stopTunnelService();
        if(cBuiler!=null)if(cBuiler.isShowing())cBuiler.dismiss();
        View inflate = LayoutInflater.from(this).inflate(R.layout.notif2, null);
        cBuiler = new AlertDialog.Builder(this).create();
        TextView title = inflate.findViewById(R.id.notiftext1);
        final TextView ms = inflate.findViewById(R.id.confimsg);
        TextView cancel = inflate.findViewById(R.id.appButton1);
        TextView clear = inflate.findViewById(R.id.appButton2txt);
        RelativeLayout btn = inflate.findViewById(R.id.appButton2);
        cancel.setTextColor(getConfig().getColorAccent());
        inflate.findViewById(R.id.color_bg).setBackgroundColor(getConfig().getColorAccent());
        btn.setBackgroundTintList(ColorStateList.valueOf(getConfig().getColorAccent()));
        ms.setTextColor(getConfig().gettextColor());
        title.setTextColor(getConfig().getAppThemeUtil()? Color.BLACK:Color.WHITE);
        title.setText("Oppss...!");
        ms.setText(str);
        cancel.setText("Close");
        clear.setText("Clear");
        inflate.findViewById(R.id.appButton0).setOnClickListener(p1 -> {
            if (HarlieService.isVPNRunning())stopTunnelService();
            cBuiler.dismiss();
        });

        btn.setOnClickListener(p1 -> {
            xUser.setText("");
            xPass.setText("");
            getEditor().putString("_screenUsername_key","").apply();
            getEditor().putString("_screenPassword_key","").apply();
            getConfig().setUser("");
            getConfig().setUserPass("");
            getEditor().putString("_AccountXp",date).apply();
            if (HarlieService.isVPNRunning())stopTunnelService();
            ac_xp.setText(date);
            cBuiler.dismiss();
        });
        cBuiler.setView(inflate);
        cBuiler.setCancelable(false);
        cBuiler.getWindow().getAttributes().windowAnimations = R.style.alertDialog;
        cBuiler.show();
    }

}
