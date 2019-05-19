package com.ieti.easywheels.ui.fragments.steps;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ieti.easywheels.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class DayStep extends Step<String> {


    public DayStep(String title) {
        super(title);
    }

    private static String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private Spinner daySpinner;

    @Override
    protected View createStepContentLayout() {
        daySpinner = new Spinner(new ContextThemeWrapper(getContext(), R.style.Theme_MaterialComponents_CompactMenu));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);

        return daySpinner;
    }

    @Override
    public String getStepData() {

        return DAYS[daySpinner.getSelectedItemPosition()];
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String selection = daySpinner.getSelectedItem().toString();
        return selection != null ? selection : getContext().getString(R.string.empty_step);
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
