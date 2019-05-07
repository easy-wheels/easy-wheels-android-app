package com.ieti.easywheels.ui.fragments.steps;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;

import ernestoyaquello.com.verticalstepperform.Step;

public class DayStep extends Step<String> {


    public DayStep(String title) {
        super(title);
    }

    private EditText userNameView;
    private MaterialSpinner materialSpinner;

    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        userNameView = new EditText(getContext());
        userNameView.setSingleLine(true);
        userNameView.setHint("Your Name");

        userNameView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked automatically by the form with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSpinner = new MaterialSpinner(getContext());
        materialSpinner.setItems("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado");
        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });


        return materialSpinner;
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        String selected = materialSpinner.getItems().get(materialSpinner.getSelectedIndex()).toString();
        return selected;
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String userName = getStepData();
        return !userName.isEmpty() ? userName : "(Empty)";
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        userNameView.setText(stepData);
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
