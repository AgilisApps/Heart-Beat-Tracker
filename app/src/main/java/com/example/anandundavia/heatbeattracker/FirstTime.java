package com.example.anandundavia.heatbeattracker;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.*;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.system.Os;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FirstTime extends Fragment
{
    private static final int PICK_EM1 = 1;
    private static final int PICK_EM2 = 2;
    private static final int PICK_DOC = 3;
    private static final int BLUETOOTH_REQUEST_CODE = 112;

    private static final int BLUETOOTH_CONNECT_CODE = 1101;
    private BluetoothAdapter bluetoothAdapter;
    final private int REQUEST_CODE_ASK_PERMISSIONS_CONTACTS = 110;
    private static int x_global=1;



    private View rootView;

    private Button em1Btn, em2Btn, docBtn, pairDeviceBtn, submitBtn;
    private boolean em1Selected = false, em2Selected = false, docSelected = false;
    private String em1Name, em2Name, docName, em1ContactNumber, em2ContactNumber, docContactNumber;

    private EditText nameET;

    public FirstTime()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_first_time, container, false);

        em1Btn = (Button) rootView.findViewById(R.id.em1Btn);
        em2Btn = (Button) rootView.findViewById(R.id.em2Btn);
        docBtn = (Button) rootView.findViewById(R.id.docBtn);
        pairDeviceBtn = (Button) rootView.findViewById(R.id.pair_device_btn);
        submitBtn = (Button) rootView.findViewById(R.id.submitBtn);

        nameET = (EditText) rootView.findViewById(R.id.nameET);

        em1Btn.setOnClickListener(new MyContactPicker(PICK_EM1));
        em2Btn.setOnClickListener(new MyContactPicker(PICK_EM2));
        docBtn.setOnClickListener(new MyContactPicker(PICK_DOC));
        pairDeviceBtn.setOnClickListener(new PairDeviceHandler());
        submitBtn.setOnClickListener(new SubmitHandler());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode!=BLUETOOTH_REQUEST_CODE)
        {
            Uri contactData = data.getData();
            Cursor c = getContext().getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst())
            {
                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                phones.moveToFirst();
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Toast.makeText(getContext(), number, Toast.LENGTH_SHORT).show();
                if (requestCode == PICK_EM1)
                {
                    em1Name = name;
                    em1ContactNumber = number.replaceAll("\\s+", "");
                    em1Btn.setText(name);
                    em1Selected = true;
                } else if (requestCode == PICK_EM2)
                {
                    em2Name = name;
                    em2ContactNumber = number.replaceAll("\\s+", "");
                    em2Btn.setText(name);
                    em2Selected = true;
                } else if (requestCode == PICK_DOC)
                {
                    docName = name;
                    docContactNumber = number.replaceAll("\\s+", "");
                    docBtn.setText(name);
                    docSelected = true;
                }
            }
        }
        else if(resultCode==Activity.RESULT_OK && requestCode==BLUETOOTH_REQUEST_CODE){
            seeBluetoothDeviceList();
        }
        else if(requestCode==BLUETOOTH_CONNECT_CODE){

            //getActivity().finish();

        }
    }

    class PairDeviceHandler implements View.OnClickListener{

        public PairDeviceHandler(){

        }
        @Override
        public void onClick(View view) {

            if(bluetoothAdapter!=null) {
                if (!bluetoothAdapter.isEnabled()) {

                    Intent bluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothOn, BLUETOOTH_REQUEST_CODE);


                } else {
                    seeBluetoothDeviceList();
                }
            }
            else{
                Snackbar.make(getView(), "Bluetooth not supported on this device", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void seeBluetoothDeviceList(){


        Intent pickBluetoothDevice = new Intent();
        pickBluetoothDevice.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        //startActivity(pickBluetoothDevice);
        startActivityForResult(pickBluetoothDevice,BLUETOOTH_CONNECT_CODE);

        /*
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(data.getData().getPath());
        sharingIntent.setType("image*//*");
        sharingIntent.setPackage("com.android.bluetooth");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(sharingIntent, "Share file"));*/

    }
    class MyContactPicker implements View.OnClickListener
    {
        int x;

        MyContactPicker(int ip)
        {
            x = ip;
        }

        @Override
        public void onClick(View v)
        {
            x_global = x;

            if(Build.VERSION.SDK_INT<23)
                selectContact();
            else
                checkPermissions();          //for marshmallow and above versions
        }

        @TargetApi(23)
        private void checkPermissions() {

            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CODE_ASK_PERMISSIONS_CONTACTS);
            }
            else{
                selectContact();
            }

        }

        public void selectContact(){
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, x);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 1 && requestCode == REQUEST_CODE_ASK_PERMISSIONS_CONTACTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MyContactPicker my = new MyContactPicker(x_global);
            my.selectContact();

        }
        else{

            Toast.makeText(getContext(), "Permissions Denied | Quitting", Toast.LENGTH_SHORT).show();
            getActivity().finish();

        }
    }

    class SubmitHandler implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            String enteredName = nameET.getText().toString();
            boolean allContactsPicked = em1Selected && em2Selected && docSelected;
            if (enteredName.equals(""))
            {
                Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            } else if (!allContactsPicked)
            {
                Toast.makeText(getContext(), "Please pick all the contacts", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Data will be saved", Toast.LENGTH_SHORT).show();

            /*
            String TAG = "DEBUG";
            Log.e(TAG,enteredName);
            Log.e(TAG,em1Name+" "+em1ContactNumber);
            Log.e(TAG,em2Name+" "+em2ContactNumber);
            Log.e(TAG,docName+" "+docContactNumber);
            */
            Database.LOCALDB.insertName(enteredName);
            Database.LOCALDB.insertContact(em1Name, em1ContactNumber);
            Database.LOCALDB.insertContact(em2Name, em2ContactNumber);
            Database.LOCALDB.insertContact(docName, docContactNumber);


            //new Thread(new RandomDataGen(getActivity())).start();
//            getActivity().startService(new Intent(getActivity(), RandomGenService.class));
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ftm = fm.beginTransaction();
            ftm.replace(R.id.container, new HomeFragment()).commit();


        }
    }
}
