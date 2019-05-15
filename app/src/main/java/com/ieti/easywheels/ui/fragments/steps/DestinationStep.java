package com.ieti.easywheels.ui.fragments.steps;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ieti.easywheels.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class DestinationStep extends Step<String> {
    private final int TOU_RADIO_BUTTON_ID = 7;
    private final int FROMU_RADIO_BUTTON_ID = 8;
    private RadioButton fromUniversityRadioButton;
    private RadioButton toUniversityRadioButton;
    private int checkedRadioButtonId;

    public DestinationStep(String title) {
        super(title);
    }

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        RadioGroup radioGroup = new RadioGroup(getContext());

        fromUniversityRadioButton = new RadioButton(getContext());
        fromUniversityRadioButton.setId(FROMU_RADIO_BUTTON_ID);
        fromUniversityRadioButton.setText(getContext().getString(R.string.from_university_radiobutton_text));
        fromUniversityRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.addView(fromUniversityRadioButton);

        toUniversityRadioButton = new RadioButton(getContext());
        toUniversityRadioButton.setId(TOU_RADIO_BUTTON_ID);
        toUniversityRadioButton.setText(getContext().getString(R.string.to_univerdsity_radiobutton_text));
        toUniversityRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radioGroup.addView(toUniversityRadioButton);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                checkedRadioButtonId = checkedId;
                markAsCompletedOrUncompleted(true);
            }
        });


        return radioGroup;
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        String checkedRadioButtonText = null;
        if (checkedRadioButtonId == TOU_RADIO_BUTTON_ID) {
            checkedRadioButtonText = getContext().getResources().getString(R.string.to_univerdsity_radiobutton_text);
        } else if (checkedRadioButtonId == FROMU_RADIO_BUTTON_ID) {
            checkedRadioButtonText = getContext().getResources().getString(R.string.from_university_radiobutton_text);
        }
        return checkedRadioButtonText != null ? checkedRadioButtonText : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String type = getStepData();
        return !type.isEmpty() ? type : getContext().getString(R.string.empty_step);
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        fromUniversityRadioButton.setChecked(false);
        toUniversityRadioButton.setChecked(false);
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        if (checkedRadioButtonId != FROMU_RADIO_BUTTON_ID && checkedRadioButtonId != TOU_RADIO_BUTTON_ID) {
            String titleError = getContext().getString(R.string.destination_step_error);
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
