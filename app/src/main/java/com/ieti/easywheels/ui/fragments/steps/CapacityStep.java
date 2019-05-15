package com.ieti.easywheels.ui.fragments.steps;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ieti.easywheels.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class CapacityStep extends Step<Integer> {

    private Spinner materialSpinner;

    public CapacityStep(String title) {
        super(title);
    }

    @Override
    protected View createStepContentLayout() {


        materialSpinner = new Spinner(new ContextThemeWrapper(getContext(), R.style.Theme_MaterialComponents_CompactMenu));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(adapter);


        return materialSpinner;
    }

    @Override
    public Integer getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.

        return null;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String selection = null;
        return selection != null ? selection : getContext().getString(R.string.empty_step);
    }

    @Override
    public void restoreStepData(Integer data) {

    }

    @Override
    protected IsDataValid isStepDataValid(Integer stepData) {
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
