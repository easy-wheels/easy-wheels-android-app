package com.ieti.easywheels.ui.fragments.steps;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ieti.easywheels.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class HourStep extends Step<String> {
    public HourStep(String title) {
        super(title);
    }

    private static String[] HOURS = {"7:00", "8:30", "10:00", "11:30", "13:00", "14:30", "16:00", "17:30", "19:00"};
    private Spinner hoursSpinner;

    @Override
    protected View createStepContentLayout() {

        hoursSpinner = new Spinner(new ContextThemeWrapper(getContext(), R.style.Theme_MaterialComponents_CompactMenu));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.hours_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(adapter);


        return hoursSpinner;
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        String selected = HOURS[hoursSpinner.getSelectedItemPosition()];
        return selected;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String hour = hoursSpinner.getSelectedItem().toString();
        return !hour.isEmpty() ? hour : getContext().getString(R.string.empty_step);
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.

    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return null;
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
