package com.octane.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octane.app.API.API;
import com.octane.app.API.APIClient;
import com.octane.app.Component.CircularActiveView;
import com.octane.app.Component.CircularNavigationView;
import com.octane.app.GlobalConstant;
import com.octane.app.Model.Profile;
import com.octane.app.Model.ResponseModel;
import com.octane.app.Preference.SharedPref;
import com.octane.app.R;
import com.octane.app.fragment.ActiveFragment;
import com.octane.app.fragment.BluetoothDialog;
import com.octane.app.fragment.HelpFragment;
import com.octane.app.fragment.NavigationFragment;
import com.octane.app.fragment.OnFragmentInteractionListener;
import com.octane.app.fragment.SettingFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener,
        ListView.OnItemClickListener,
        CircularActiveView.OnCircularItemClickListener,
        CircularNavigationView.OnCircularItemClickListener {


    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    int curSelItem;
    int mCurProfileIndex;

    ActiveFragment activeFragment;
    NavigationFragment navigationFragment;
    SettingFragment settingFragment;
    HelpFragment helpFragment;
    Dialog bluetoothDialog;

    TextView txtStatus;
    ListView pairedList;
    ProgressBar progressBar;
    ImageView warningImg;

    private static final int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    int backButtonCount = 0;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    TextView textStatus;

    Fragment fragment;
    boolean connectFlag = false;
    String pinCode = null;

    public List<Profile> profiles;

    public static String CheckSum(byte[] buffer, int bytes) {
        byte check;
        int i = 1;
        String A = "";
        check = 0;
        while (buffer[i] != '*' && i < bytes) {
            check = (byte) (check ^ buffer[i]);
            i++;
        }
        A = String.format("%02X", check);
        return A;
    }

    @Override
    protected void onStart() {
        super.onStart();
        backButtonCount = 0;
        //Turn ON BlueTooth if it is OFF

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else if(!connectFlag)
            setup();

    }

    private void setup() {

        bluetoothDialog.show();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        boolean flag = false;
        if (pairedDevices.size() > 0) {
            pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
                String str = device.getName();
                if (str.toLowerCase().contains("octane") || str.equals("octane")) {
                    //textStatus.setText("Auto connecting to RAC3RCHIP...");
                    //bluetoothDialog.getTxtStatus().setText(R.string.msg_connecting_bluetooth_module);
                    myThreadConnectBTdevice = new HomeActivity.ThreadConnectBTdevice(device);
                    myThreadConnectBTdevice.start();
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            setErrorMsg(R.string.msg_find_module_error);
            pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
                    android.R.layout.simple_list_item_1, pairedDeviceArrayList);
            pairedList.setAdapter(pairedDeviceAdapter);
            pairedList.setVisibility(View.VISIBLE);
            pairedList.setOnItemClickListener(this);
            //bluetoothDialog.setPairedDevices(pairedDeviceAdapter);
            //textStatus.setText("Cannot find RAC3RCHIP...");
        }
    }

    private void setErrorMsg(int msgId) {
        progressBar.setVisibility(View.GONE);
        warningImg.setVisibility(View.VISIBLE);
        txtStatus.setText(msgId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device =
                (BluetoothDevice) parent.getItemAtPosition(position);
        Toast.makeText(HomeActivity.this,
                "Name: " + device.getName() + "\n"
                        + "Address: " + device.getAddress() + "\n"
                        + "BondState: " + device.getBondState() + "\n"
                        + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                        + "Class: " + device.getClass(),
                Toast.LENGTH_LONG).show();

        myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
        myThreadConnectBTdevice.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else {
                Toast.makeText(this,
                        "BlueTooth NOT enabled",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!SharedPref.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (!SharedPref.getInstance(this).doReadHelp()) {
            startActivity(new Intent(this, HelpActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);
        profiles = new ArrayList<>();
        profiles.add(new Profile("50", "Normal"));
        profiles.add(new Profile("00", "Sport"));
        profiles.add(new Profile("01", "Super sport"));
        profiles.add(new Profile("02", "Valet parking"));
        profiles.add(new Profile("03", "Economy"));
        profiles.add(new Profile("04", "Special"));


        activeFragment = new ActiveFragment();
        navigationFragment = new NavigationFragment();
        settingFragment = new SettingFragment();
        helpFragment = new HelpFragment();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.color.white);
        // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
//                FragmentManager manager = getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.main_frame,fragment);
//                transaction.commit();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        curSelItem = R.id.nav_profile;
        mCurProfileIndex = -1;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, activeFragment);
        fragmentTransaction.commit();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        //bluetooth setting
        textStatus = findViewById(R.id.txtStatus);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this,
                    "FEATURE_BLUETOOTH NOT support",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        //bluetoothDialog = new BluetoothDialog();
        //bluetoothDialog.show(getSupportFragmentManager(), "BluetoothDialogFragment");

        //bluetoothDialog.setErrorMsg(R.string.msg_find_module_error);
        bluetoothDialog = new Dialog(this);
        bluetoothDialog.setContentView(R.layout.dialog_bluetooth);
        bluetoothDialog.setCancelable(false);
        txtStatus = (TextView) bluetoothDialog.findViewById(R.id.txt_bluetooth_status);
        pairedList = (ListView) bluetoothDialog.findViewById(R.id.list_paired_devices);
        progressBar = (ProgressBar) bluetoothDialog.findViewById(R.id.img_progress);
        warningImg = (ImageView) bluetoothDialog.findViewById(R.id.img_error);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backButtonCount >= 1) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            } else {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearBottomNavView(boolean flag) {
        bottomNavigationView.getMenu().setGroupCheckable(0, !flag, true);
    }

    private void clearSideNavView(boolean flag) {
//        navigationView.getMenu().setGroupCheckable(0,!flag,true);
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setCheckable(!flag);
            menu.getItem(i).setChecked(!flag);
        }
    }

    private void setNavItem(int id) {
        curSelItem = id;
        if (navigationView.getMenu().findItem(id) != null) {
            navigationView.setCheckedItem(id);
            return;
        }
        if (bottomNavigationView.getMenu().findItem(id) != null) {

            bottomNavigationView.setSelectedItemId(id);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (curSelItem == id)
            return false;

        // Handle navigation view item clicks here.
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // Handle navigation view item clicks here.
        switch (id) {
            case R.id.nav_help: {
                fragment = helpFragment;
                break;
            }
            case R.id.nav_profile: {
                fragment = activeFragment;
                break;
            }
            case R.id.nav_setting: {
                fragment = settingFragment;
                break;
            }
            case R.id.nav_logout: {
                //clearBottomNavView(true);
                //navigationView.setCheckedItem(id);

                SharedPref.getInstance(this).logout();
                return true;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);



        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
        curSelItem = id;

        return true;
    }

    @Override
    public void onFragmentInteraction(int id) {
        setNavItem(id);
    }

    @Override
    public void onCircularItemClick() {

        if (mCurProfileIndex == -1) {
            Toast.makeText(this, "You cannot navigate profile!", Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.main_frame, new NavigationFragment());
        transaction.commit();
    }


    public int getCurProfileIndex() {
        return mCurProfileIndex;
    }

    @Override
    public void onCircularItemClick(final int index) {
        Log.d("debug", Integer.toString(index));
        if (mCurProfileIndex < 0) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Navigate Profile")
                .setMessage("Are you sure you want to change profile to " + profiles.get(index).getProfileName() +
                        " from " + profiles.get(mCurProfileIndex).getProfileName())

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        navigateProfile(profiles.get(index).getProfileCode());
                        mCurProfileIndex = index;
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
//        if(index >= 0 ){
//
//        }
    }

    public void onChangePINClicked(final String PINCode) {
        if (PINCode.length() != 4) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Change PIN Code")
                .setMessage("Are you sure you want to change PIN code to " + PINCode)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //savePINToServer(PINCode);
                        changePIN(PINCode);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void savePINToServer(String PINCode) {
        API api = APIClient.getClient().create(API.class);
        String email = SharedPref.getInstance(HomeActivity.this).getUserEmail();
        String password = SharedPref.getInstance(HomeActivity.this).getUserPassword();
        if (email == null || password == null) {
            Toast.makeText(HomeActivity.this, "Unauthorized!", Toast.LENGTH_LONG).show();
            return;
        }
        pinCode = null;
        Call<ResponseModel> savePIN = api.savePIN(email, password, PINCode);
        savePIN.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.body() == null)
                    return;
                if (Objects.requireNonNull(response.body()).getIsSuccess() == 1) {
                    Toast.makeText(HomeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    processSuccess();
                } else {
                    Toast.makeText(HomeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changePIN(String PINCode) {
        /*testing code*/
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!bluetoothDialog.isShowing()){
                    bluetoothDialog.show();
                }
                txtStatus.setText("Changing PIN Code...");
                progressBar.setVisibility(View.VISIBLE);
                warningImg.setVisibility(View.GONE);
                pairedList.setVisibility(View.GONE);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                final Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        bluetoothDialog.cancel();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        transaction.replace(R.id.main_frame,new ActiveFragment());
                        transaction.commit();
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                    }
                };
                handler.postDelayed(run,1000);
            }
        });
        */
        if (myThreadConnected != null) {
            // send "change pin packet"
            String msg = "$MA,06,81,";
            msg += PINCode;
            msg += ",*";
            String checkSum = HomeActivity.CheckSum(msg.getBytes(), msg.length());
            msg += checkSum;
            msg += '\r';
            msg += '\n';
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!bluetoothDialog.isShowing()) {
                        bluetoothDialog.show();
                    }
                    txtStatus.setText("Changing PIN Code...");
                    progressBar.setVisibility(View.VISIBLE);
                    warningImg.setVisibility(View.GONE);
                    pairedList.setVisibility(View.GONE);
                }
            });
            myThreadConnected.write(msg.getBytes());
            pinCode = PINCode;
        }
    }

    private void navigateProfile(final String profile) {
        if (myThreadConnected != null) {
            // send "select profile packet"
            String msg = "$MA,05,81,";
            msg += profile;
            msg += ",*";
            String checkSum = HomeActivity.CheckSum(msg.getBytes(), msg.length());
            msg += checkSum;
            msg += '\r';
            msg += '\n';
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!bluetoothDialog.isShowing()) {
                        bluetoothDialog.show();
                    }
                    txtStatus.setText("Navigating profile...");
                    progressBar.setVisibility(View.VISIBLE);
                    warningImg.setVisibility(View.GONE);
                    pairedList.setVisibility(View.GONE);
                }
            });
            myThreadConnected.write(msg.getBytes());
        }
        /*testing code*/
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!bluetoothDialog.isShowing()){
                    bluetoothDialog.show();
                }
                txtStatus.setText("Navigating profile...");
                progressBar.setVisibility(View.VISIBLE);
                warningImg.setVisibility(View.GONE);
                pairedList.setVisibility(View.GONE);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                final Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        bluetoothDialog.cancel();
                        //mCurProfileIndex = profiles.indexOf(profile);
                        getSupportFragmentManager().popBackStack();
                    }
                };
                handler.postDelayed(run,1000);
            }
        });
        */

    }

    public void processSuccess() {
        Toast.makeText(getApplicationContext(),
                "Success:Your petition was completed!",
                Toast.LENGTH_LONG).show();
        bluetoothDialog.cancel();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.main_frame, new ActiveFragment());
        transaction.commit();
    }

    private void getProfile() {
        if (myThreadConnected != null) {
            // send "get profile packet"
            String msg = "$MA,05,01,*";
            String checkSum = HomeActivity.CheckSum(msg.getBytes(), msg.length());
            msg += checkSum;
            msg += '\r';
            msg += '\n';
            txtStatus.setText("Getting profile...");
            progressBar.setVisibility(View.VISIBLE);
            warningImg.setVisibility(View.GONE);
            pairedList.setVisibility(View.GONE);
            myThreadConnected.write(msg.getBytes());
        }
        /*
        Testing code
        */
        /*
        final Handler handler = new Handler();
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //textView1.setText(profiles[index]);
                        //textView2.setText(GlobalConstant.PROFILE_DESC + profiles[index]);
                        bluetoothDialog.cancel();
                        mCurProfileIndex = 2;
                        activeFragment.setProfile(2);
                    }
                });
            }
        };
        handler.postDelayed(run,1000);
        */
    }

    private class ThreadConnectBTdevice extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        public ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            pairedList.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            warningImg.setVisibility(View.GONE);
            txtStatus.setText(R.string.msg_connecting_bluetooth_module);
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                //textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                setErrorMsg(R.string.msg_socket_create_error);
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
                connectFlag = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textStatus.setText("Cannot connect!\n" + eMessage + "\n");
                        setErrorMsg(R.string.msg_connection_fail);
                        pairedList.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
                startThreadConnected(bluetoothSocket);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "connecting to OCTANE device is success!",
                                Toast.LENGTH_LONG).show();
                        textStatus.setText("Getting profile...");
                        progressBar.setVisibility(View.VISIBLE);
                        warningImg.setVisibility(View.GONE);
                        pairedList.setVisibility(View.GONE);
                        getProfile();
                    }
                });


            } else {
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(getApplicationContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
                connectFlag = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void startThreadConnected(BluetoothSocket socket) {

        myThreadConnected = new HomeActivity.ThreadConnected(socket);
        myThreadConnected.start();
    }

    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {

            int bytes;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //textStatus.setText("Waiting for device message...");
                }
            });
            while (true) {
                try {
                    byte[] buffer = new byte[100];
                    bytes = connectedInputStream.read(buffer);
                    final String msg = new String(buffer);


                    //$MA,05,01,AA,*XX\r\n
                    String checkSum = CheckSum(buffer, bytes);
                    String header = msg.substring(0, 9);

                    if (header.equals("$MA,05,01")) {
                        String msgCheck = msg.substring(14, 16);
                        if (msgCheck.equals(checkSum)) {
                            final String profile = msg.substring(10, 12);
                            final String msgReceived = "Current Profile is 0x" + profile;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int i;
                                    for (i = 0; i < profiles.size(); i++) {
                                        if (profiles.get(i).getProfileCode().equals(profile))
                                            break;
                                    }


                                    if (i == profiles.size()) {
                                        Toast.makeText(getApplicationContext(),
                                                "There is no such profile",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        bluetoothDialog.cancel();
                                        mCurProfileIndex = i;
                                        activeFragment.setProfile(i);
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Error: CheckSum error!",
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        // this packet is to get profile

                    } else if (msg.equals("$MA,10,01,*20\r\n") || msg.substring(0, 13).equals("$MA,10,01,*20")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (pinCode != null) {
                                    savePINToServer(pinCode);
                                } else {
                                    processSuccess();
                                }
                            }
                        });
                    }
            } catch(Exception e){
                // TODO Auto-generated catch block
                e.printStackTrace();

                final String msgConnectionLost = "Connection lost:\n"
                        + e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textStatus.setText(msgConnectionLost);
                    }
                });
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            connectedOutputStream.write(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            connectedBluetoothSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
}
