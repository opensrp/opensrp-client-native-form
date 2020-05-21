package com.vijay.jsonwizard.fragments;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by keyman on 04/12/2018.
 */
public class JsonWizardFormFragment extends JsonFormFragment {
    public static final String TAG = JsonWizardFormFragment.class.getName();
    private static final int MENU_NAVIGATION = 100001;
    private BottomNavigationListener navigationListener = new BottomNavigationListener();
    private Button previousButton;
    private Button nextButton;
    private ImageView previousIcon;
    private ImageView nextIcon;
    private TextView stepName;
    private Toolbar navigationToolbar;
    private View bottomNavLayout;
    private JsonWizardFormFragment jsonFormFragment;
    private boolean nextStepHasNoRelevance;

    public static JsonWizardFormFragment getFormFragment(String stepName) {
        JsonWizardFormFragment jsonFormFragment = new JsonWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.native_json_form_fragment_wizard, null);

        this.mMainView = rootView.findViewById(R.id.main_layout);
        this.mScrollView = rootView.findViewById(R.id.scroll_view);

        setupNavigation(rootView);
        setupCustomUI();

        return rootView;
    }

    protected void setupNavigation(View rootView) {
        previousButton = rootView.findViewById(R.id.previous);
        previousIcon = rootView.findViewById(R.id.previous_icon);

        previousButton.setVisibility(View.INVISIBLE);
        previousIcon.setVisibility(View.INVISIBLE);

        previousButton.setOnClickListener(navigationListener);
        previousIcon.setOnClickListener(navigationListener);

        nextButton = rootView.findViewById(R.id.next);
        nextIcon = rootView.findViewById(R.id.next_icon);

        nextButton.setOnClickListener(navigationListener);
        nextIcon.setOnClickListener(navigationListener);

        stepName = rootView.findViewById(R.id.step_title);

        navigationToolbar = rootView.findViewById(R.id.navigation_toolbar);

        bottomNavLayout = rootView.findViewById(R.id.bottom_navigation_layout);
    }

    protected void setupCustomUI() {
        setUpBackButton();

        try {
            Form form = getForm();
            if (form != null) {
                if (form.getHomeAsUpIndicator() != 0) {
                    getSupportActionBar().setHomeAsUpIndicator(form.getHomeAsUpIndicator());
                }

                if (form.getActionBarBackground() != 0) {
                    int actionBarColor = getResources().getColor(form.getActionBarBackground());
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));
                }

                if (form.getNavigationBackground() != 0) {
                    int navigationColor = getResources().getColor(form.getNavigationBackground());
                    if (navigationToolbar != null) {
                        navigationToolbar.setBackgroundColor(navigationColor);
                    }
                }

                if (form.isWizard()) {
                    bottomNavLayout.setVisibility(View.VISIBLE);
                    navigationToolbar.setVisibility(View.VISIBLE);
                } else {
                    bottomNavLayout.setVisibility(View.GONE);
                    navigationToolbar.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(form.getPreviousLabel())) {
                    previousButton.setText(form.getPreviousLabel());
                }

                if (form.getBackIcon() > 0) {
                    getSupportActionBar().setHomeAsUpIndicator(form.getBackIcon());
                }

            }
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "%s setupCustomUI()", this.getClass().getCanonicalName());
        }

    }

    private Form getForm() {
        if (getActivity() != null && getActivity() instanceof JsonFormActivity) {
            return ((JsonFormActivity) getActivity()).getForm();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        processSkipSteps();
        setJsonFormFragment(this);
    }

    public void processSkipSteps() {
        if (getJsonApi().isPreviousPressed()) {
            skipStepOnPreviousPressed();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.add(Menu.NONE, MENU_NAVIGATION, 1, "Menu").setIcon(R.drawable.ic_action_menu).setShowAsAction(MenuItem
        // .SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == MENU_NAVIGATION) {
            Toast.makeText(getActivity(), "Right navigation item clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonWizardFormFragmentPresenter(this, JsonFormInteractor.getInstance());
    }

    @Override
    public void setActionBarTitle(String title) {
        Form form = getForm();
        if (form != null && !TextUtils.isEmpty(form.getName())) {
            super.setActionBarTitle(form.getName());
            if (stepName != null) {
                stepName.setText(title);
            }
        } else {
            super.setActionBarTitle(title);
        }
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        Form form = getForm();
        if (form != null && form.isWizard()) {
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(false);
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        } else {
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(next);
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }

        if (next || !save) {
            nextButton.setTag(R.id.NEXT_STATE, true);
            nextButton.setText(getString(R.string.next));

            if (form != null && !TextUtils.isEmpty(form.getNextLabel())) {
                nextButton.setText(form.getNextLabel());
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setTitle(form.getNextLabel());
            }

            nextIcon.setVisibility(View.VISIBLE);
        }

        if (save || !next) {
            nextButton.setTag(R.id.NEXT_STATE, false);
            nextButton.setText(getString(R.string.submit));

            if (form != null && !TextUtils.isEmpty(form.getSaveLabel())) {
                nextButton.setText(form.getSaveLabel());
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setTitle(form.getSaveLabel());
            }


            nextIcon.setVisibility(View.INVISIBLE);
        }

        if (getFragmentManager() != null) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                previousButton.setVisibility(View.INVISIBLE);
                previousIcon.setVisibility(View.INVISIBLE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
                previousIcon.setVisibility(View.VISIBLE);

                if (form != null && !TextUtils.isEmpty(form.getPreviousLabel())) {
                    previousButton.setText(form.getPreviousLabel());
                }
            }

            if (form != null && form.isHideNextButton()) {
                nextButton.setVisibility(View.GONE);
            }
            if (form != null && form.isHidePreviousButton()) {
                previousButton.setVisibility(View.GONE);
            }
        }

        //hide wizard nav bar if stepcount is zero
        if (Integer.valueOf(getCount()) < 2) {
            navigationToolbar.setVisibility(View.GONE);
        }
    }

    /**
     * @deprecated use {@link #skipStepsOnNextPressed(String)}
     * Skips blank by relevance steps when next is clicked on the json wizard forms.
     */
    public void skipStepsOnNextPressed() {
        if (skipBlankSteps()) {
            JSONObject formStep = getStep(getArguments().getString(JsonFormConstants.STEPNAME));
            String next = formStep.optString(JsonFormConstants.NEXT, "");
            if (StringUtils.isNotEmpty(next)) {
                checkIfStepIsBlank(formStep);
                if (shouldSkipStep()) { //this check is insufficient
                    markStepAsSkipped(formStep);
                    next();
                }
            }
        }
    }

    /***
     * Adds a property 'skipped=true' to a step object if the step is skipped
     * @param formStep {@link JSONObject}
     */
    private void markStepAsSkipped(JSONObject formStep) {
        try {
            formStep.put("skipped", true);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /***
     * Skips blank steps when next is clicked on the json wizard forms.
     * @param step {@link JSONObject}
     */

    public void skipStepsOnNextPressed(String step) {
        if (skipBlankSteps()) {
            JSONObject formStep = getJsonApi().getmJSONObject().optJSONObject(step);
            String next = formStep.optString(JsonFormConstants.NEXT, "");
            if (StringUtils.isNotEmpty(next)) {
                if (!getJsonApi().isNextStepRelevant() && !nextStepHasNoSkipLogic()) {
                    markStepAsSkipped(formStep);
                    getJsonApi().setNextStep(next);
                    next();
                }
            }
        }
    }

    /**
     * Skips blank by relevance steps when previous is clicked on the json wizard forms.
     */
    public void skipStepOnPreviousPressed() {
        if (skipBlankSteps()) {
            JSONObject currentFormStep = getStep(getArguments().getString(JsonFormConstants.STEPNAME));
            String next = currentFormStep.optString(JsonFormConstants.NEXT, "");
            int currentFormStepNumber = getFormStepNumber(next);
            for (int i = currentFormStepNumber; i >= 1; i--) {
                JSONObject formStep = getJsonApi().getmJSONObject().optJSONObject(JsonFormConstants.STEP + i);
                if (formStep != null) {
                    checkIfStepIsBlank(formStep);
                    if (shouldSkipStep()) {
                        getFragmentManager().popBackStack();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param formStep {@link JSONObject}
     * @deprecated use a combination of {@link #!getJsonApi().isNextStepRelevant and !getJsonApi().nextStepHasNoSkipLogic}
     * Checks if a given step is blank due to relevance hidding all the widgets
     */
    private void checkIfStepIsBlank(JSONObject formStep) {
        try {
            if (formStep.has(JsonFormConstants.FIELDS)) {
                JSONArray fields = formStep.getJSONArray(JsonFormConstants.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);
                    if (field.has(JsonFormConstants.TYPE) && !JsonFormConstants.HIDDEN.equals(field.getString(JsonFormConstants.TYPE))) {
                        boolean isVisible = field.optBoolean(JsonFormConstants.IS_VISIBLE, false);
                        if (isVisible) {
                            setShouldSkipStep(false);
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "%s --> checkIfStepIsBlank", this.getClass().getCanonicalName());
        }
    }

    /**
     * Returns the current form step number when given than steps next step number.
     * This number is used to figure out which steps to pop when previous is clicked.
     *
     * @param nextFormNumber {@link String}
     * @return formNumber {@link Integer}
     */
    private int getFormStepNumber(String nextFormNumber) {
        int formNumber = 0;
        if (StringUtils.isNotBlank(nextFormNumber)) {
            int currentFormNumber = Integer.parseInt(nextFormNumber.substring(4, 5)) - 1;
            if (currentFormNumber > 0) {
                formNumber = currentFormNumber;
            } else if (currentFormNumber == 0) {
                formNumber = 1;
            }
        }
        return formNumber;
    }

    protected void save() {
        try {
            Boolean skipValidation = ((JsonFormActivity) mMainView.getContext()).getIntent()
                    .getBooleanExtra(JsonFormConstants
                            .SKIP_VALIDATION, false);
            save(skipValidation);
        } catch (Exception e) {
            Timber.e(e, "%s save()", this.getClass().getCanonicalName());
            save(false);
        }
    }

    public TextView getStepName() {
        return stepName;
    }

    public JsonWizardFormFragment getJsonFormFragment() {
        return jsonFormFragment;
    }

    public void setJsonFormFragment(JsonWizardFormFragment jsonFormFragment) {
        this.jsonFormFragment = jsonFormFragment;
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    protected class BottomNavigationListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view != null) {
                if (view.getId() == R.id.next || view.getId() == R.id.next_icon) {
                    getJsonApi().setPreviousPressed(false);
                    Object nextStateTag = view.getTag(R.id.NEXT_STATE);
                    if (nextStateTag == null) {
                        getJsonApi().getAppExecutors().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                next();
                            }
                        });
                    } else {
                        boolean next = (boolean) nextStateTag;
                        if (next) {
                            getJsonApi().getAppExecutors().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    next();
                                }
                            });
                        } else {
                            save();
                        }
                    }

                } else if (view.getId() == R.id.previous || view.getId() == R.id.previous_icon) {
                    assert getFragmentManager() != null;
                    presenter.checkAndStopCountdownAlarm();
                    getJsonApi().setPreviousPressed(true);
                    getFragmentManager().popBackStack();
                }
            }
        }
    }

    public boolean nextStepHasNoSkipLogic() {
        return nextStepHasNoRelevance;
    }

    public void setNextStepHasNoSkipLogic(boolean value) {
        this.nextStepHasNoRelevance = value;
    }

}


