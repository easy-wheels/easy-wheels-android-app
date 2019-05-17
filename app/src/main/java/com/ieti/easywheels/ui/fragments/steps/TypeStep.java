package com.ieti.easywheels.ui.fragments.steps;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ieti.easywheels.R;
import com.ieti.easywheels.ui.fragments.ProgramTripFragment;

import ernestoyaquello.com.verticalstepperform.Step;

public class TypeStep extends Step<String> {


    private RadioButton passangerRadioButton;
    private RadioButton driverRadioButton;
    private final int DRIVER_RADIO_BUTTON_ID = 7;
    private final int PASSENGER_RADIO_BUTTON_ID = 8;
    private int checkedRadioButtonId;
    private ProgramTripFragment stepper;
    private Spinner capacitySpinner;

    public TypeStep(ProgramTripFragment st, String stepTitle) {
        super(stepTitle);
        this.stepper = st;

    }

    @NonNull
    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        RadioGroup radioGroup = new RadioGroup(getContext());

        passangerRadioButton = new RadioButton(getContext());
        passangerRadioButton.setId(PASSENGER_RADIO_BUTTON_ID);
        passangerRadioButton.setText(getContext().getResources().getString(R.string.passanger_radiobutton_text));
        passangerRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.addView(passangerRadioButton);

        driverRadioButton = new RadioButton(getContext());
        driverRadioButton.setId(DRIVER_RADIO_BUTTON_ID);
        driverRadioButton.setText(getContext().getResources().getString(R.string.driver_radiobutton_text));
        driverRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.addView(driverRadioButton);

        TextView capacityText = new TextView(getContext());
        capacityText.setText(getContext().getString(R.string.capacity_text_type_step));
        radioGroup.addView(capacityText);

        capacitySpinner = new Spinner(new ContextThemeWrapper(getContext(), R.style.Theme_MaterialComponents_CompactMenu));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.capacity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        capacitySpinner.setAdapter(adapter);
        capacitySpinner.setEnabled(false);
        radioGroup.addView(capacitySpinner);
        capacitySpinner.setSelection(0);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                checkedRadioButtonId = checkedId;
                if (checkedRadioButtonId == DRIVER_RADIO_BUTTON_ID) {
                    capacitySpinner.setEnabled(true);
                } else {
                    capacitySpinner.setEnabled(false);
                }
                markAsCompletedOrUncompleted(true);
            }
        });


        return radioGroup;
    }

    @Override
    public String getStepData() {
        String selectedData = null;

        if (checkedRadioButtonId == DRIVER_RADIO_BUTTON_ID) {
            int cp = capacitySpinner.getSelectedItemPosition() + 1;
            selectedData = "driver " + cp;
        } else if (checkedRadioButtonId == PASSENGER_RADIO_BUTTON_ID) {
            selectedData = "passenger";
        }
        return selectedData != null ? selectedData : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {

        String type = getStepData();
        if (type.equals("passenger")) {
            type = getContext().getResources().getString(R.string.passanger_radiobutton_text);
        } else if (type.startsWith("driver")) {
            String capacity = type.split(" ")[1];
            type = "Soy conductor y cuento con " + capacity + " cupos";
        }
        return !type.isEmpty() ? type : getContext().getString(R.string.empty_step);
    }

    @Override
    public void restoreStepData(String stepData) {
        passangerRadioButton.setChecked(false);
        driverRadioButton.setChecked(false);
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        if (checkedRadioButtonId != PASSENGER_RADIO_BUTTON_ID && checkedRadioButtonId != DRIVER_RADIO_BUTTON_ID) {
            String titleError = getContext().getString(R.string.type_of_usser_step_error);
            return new IsDataValid(false, titleError);
        } else {
            return new IsDataValid(true);
        }
    }


    @Override
    protected void onStepOpened(boolean animated) {
    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {

    }


}
