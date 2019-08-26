package com.octane.app.fragment;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.octane.app.R;

public class BluetoothDialog extends DialogFragment {

    TextView txtStatus;
    ListView pairedList;
    ProgressBar progressBar;
    ImageView warningImg;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(false);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bluetooth, null);
        builder.setView(view);



        return builder.create();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // R.layout.my_layout - that's the layout where your textview is placed
        View view = inflater.inflate(R.layout.dialog_bluetooth, container, false);
        txtStatus = (TextView) view.findViewById(R.id.txt_bluetooth_status);
        pairedList = (ListView) view.findViewById(R.id.list_paired_devices);
        progressBar = (ProgressBar) view.findViewById(R.id.img_progress);
        warningImg = (ImageView)view.findViewById(R.id.img_error);
        // you can use your textview.
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public TextView getTxtStatus() {
        return txtStatus;
    }

    public ListView getPairedList() {
        return pairedList;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public ImageView getWarningImg() {
        return warningImg;
    }

    public void setErrorMsg(int errorMsgId){
        //if(progressBar == null || warningImg == null || txtStatus == null)
        //    return;
        progressBar.setVisibility(View.GONE);
        //warningImg.setVisibility(View.VISIBLE);
        //txtStatus.setText(errorMsgId);
    }
    public void setPairedDevices(ArrayAdapter<BluetoothDevice> adapter){
        pairedList.setAdapter(adapter);
        pairedList.setVisibility(View.VISIBLE);
    }
}
