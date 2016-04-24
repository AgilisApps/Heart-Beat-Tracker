package com.example.anandundavia.heatbeattracker;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.RunnableFuture;


public class HomeFragment extends Fragment
{
    public static String CURRENT_SELECTED_MODE="Normal";
    Spinner currentModeSpin;
    TextView bannerBottomLeft, bannerBottomRight,bannerTop;
    Button declareEmergency;
    private String Numbers[], Message, currentMode;
    public Thread workerThread;



            /*
            The toast is for degugging. Closing the acitivity, it will give null pointer
            Toast.makeText(getActivity(),"Got BPM : "+message,Toast.LENGTH_SHORT).show();
            */

            /*int soft = Database.LOCALDB.getSoftLimitFor(CURRENT_SELECTED_MODE);
            int hard = Database.LOCALDB.getHardLimitFor(CURRENT_SELECTED_MODE);
            int received = Integer.parseInt(message);
            if (received < soft || received > hard)
            {
                String numbers[] = Database.LOCALDB.getAllNumbers();
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numbers[0]));
                startActivity(i);
*/
    public HomeFragment()
    {

    }

    public boolean Update(){
        Random r = new Random();
        int low = Database.LOCALDB.getSoftLimitFor(CURRENT_SELECTED_MODE) - 5;
        int high = Database.LOCALDB.getHardLimitFor(CURRENT_SELECTED_MODE) + 5;
        int result = r.nextInt(high - low) + low;

        int soft = Database.LOCALDB.getSoftLimitFor(HomeFragment.CURRENT_SELECTED_MODE);
        int hard = Database.LOCALDB.getHardLimitFor(HomeFragment.CURRENT_SELECTED_MODE);
        bannerTop.setText(String.valueOf(result));
        if (result < soft || result > hard) {
            Log.e("Anand", "Should call");
            String numbers[] = Database.LOCALDB.getAllNumbers();
            Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numbers[0]));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Numbers = Database.LOCALDB.getAllNumbers();
            Message = Database.LOCALDB.getMessageFor(currentMode);
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(Numbers[0], null, Message, null,null);

//            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }

            return true;
        }
        return false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


       // LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(RandomDataGen.DATA_BROADCAST));

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        currentModeSpin = (Spinner) rootView.findViewById(R.id.currentmodespin);
        bannerBottomLeft = (TextView)rootView.findViewById(R.id.banner_bottom_left_textview);
        bannerBottomRight = (TextView)rootView.findViewById(R.id.banner_bottom_right_textview);
        bannerTop = (TextView) rootView.findViewById(R.id.banner_top_textview);
        declareEmergency = (Button)rootView.findViewById(R.id.emergencyButton);

        declareEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Numbers = Database.LOCALDB.getAllNumbers();
                Message = Database.LOCALDB.getMessageFor(currentMode);
                SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(Numbers[0], null, Message, null,null);
                Log.d("Message", Message);

                Snackbar.make(getView(), "Message Sent!", Snackbar.LENGTH_LONG).show();



            }
        });
       /* currentModeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.modes, android.R.layout.simple_spinner_dropdown_item);
        currentModeSpin.setAdapter(adapter);
        currentModeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                CURRENT_SELECTED_MODE = Settings.MODES[position];
                updateSpinner();
                //Log.d("SelectedOn","Selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        updateSpinner();
     //   RandomDataGen randomDataGen = new RandomDataGen(this.getContext(),bannerTop);

        Runner r = new Runner(getActivity(),this);

        workerThread = new Thread(r);
        workerThread.start();

        return rootView;
    }

    public void updateSpinner(){
        currentMode = Database.MODES[currentModeSpin.getSelectedItemPosition()];

        bannerBottomLeft.setText(String.valueOf(Database.LOCALDB.getSoftLimitFor(currentMode)));
        bannerBottomRight.setText(String.valueOf(Database.LOCALDB.getHardLimitFor(currentMode)));
    }


    class Runner implements Runnable {

        Activity activity;
        HomeFragment frag;
        public Runner(Activity activity,HomeFragment frag){
            this.activity = activity;
            this.frag = frag;
        }
        boolean data = false;
        @Override
        public void run() {

            while (true){
                Log.i("DATA","Normal");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data = frag.Update();
                    }
                });

                if (data == true) {
                    break;
                }
            }

        }
    }

}