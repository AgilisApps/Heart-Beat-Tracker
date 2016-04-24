package com.example.anandundavia.heatbeattracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Settings extends Fragment
{

    public static final String MODES[] = {"Normal", "Running", "Sleeping"};

    EditText softLimitET, hardLimitET, messageET, nameET;
    Spinner modeSpinner;
    Button saveBTN;

    public Settings()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        modeSpinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.modes,android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setOnItemSelectedListener(new ModeSelectionListener());

        softLimitET = (EditText) rootView.findViewById(R.id.softet);
        hardLimitET = (EditText) rootView.findViewById(R.id.hardet);
        messageET = (EditText) rootView.findViewById(R.id.msget);
        nameET = (EditText) rootView.findViewById(R.id.nameet);
        saveBTN = (Button) rootView.findViewById(R.id.savebtn);

        nameET.setText(Database.LOCALDB.getName());
        softLimitET.setText(Integer.toString(Database.LOCALDB.getSoftLimitFor(MODES[0])));
        hardLimitET.setText(Integer.toString(Database.LOCALDB.getHardLimitFor(MODES[0])));
        messageET.setText(Database.LOCALDB.getMessageFor(MODES[0]));


        saveBTN.setOnClickListener(new SaveButtonListener());

        return rootView;
    }


    class ModeSelectionListener implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            softLimitET.setText(Integer.toString(Database.LOCALDB.getSoftLimitFor(MODES[position])));
            hardLimitET.setText(Integer.toString(Database.LOCALDB.getHardLimitFor(MODES[position])));
            messageET.setText(Database.LOCALDB.getMessageFor(MODES[position]));

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }

    class SaveButtonListener implements AdapterView.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            String name = nameET.getText().toString();
            String mode = modeSpinner.getSelectedItem().toString();
            String softLimit = softLimitET.getText().toString();
            String hardLimit = hardLimitET.getText().toString();
            String message = messageET.getText().toString();
            boolean valid = validate(Integer.parseInt(softLimit), Integer.parseInt(hardLimit));
            if(valid) {
                Database.LOCALDB.insertName(name);
                Database.LOCALDB.setSoftLimitFor(mode, softLimit);
                Database.LOCALDB.setHardLimitFor(mode, hardLimit);
                Database.LOCALDB.setMessageFor(mode, message);
                Toast.makeText(getContext(), "Changes Saved Successfully!", Toast.LENGTH_SHORT).show();
            }

        }
        public boolean validate(int SoftLimit, int HardLimit){


            if(SoftLimit>=HardLimit){
                Toast.makeText(getContext(), "Lower limit cannot be more than Higher Limit", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(SoftLimit<0 || HardLimit<0){
                Toast.makeText(getContext(), "Negative values not allowed", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;

        }
    }

}