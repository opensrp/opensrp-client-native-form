package com.vijay.jsonwizard.fragments;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonWizardFormFragmentPresenter(this, JsonFormInteractor.getInstance());
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

        switch (item.getItemId()) {
            case MENU_NAVIGATION:
                Toast.makeText(getActivity(), "Right navigation item clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        Form form = getForm();
        if (form != null && form.isWizard()) {
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(false);
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
        } else {
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(next);
            getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
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
            Log.e(TAG, e.getMessage(), e);
        }

    }

    protected void save() {
        try {
            Boolean skipValidation = ((JsonFormActivity) mMainView.getContext()).getIntent()
                    .getBooleanExtra(JsonFormConstants
                            .SKIP_VALIDATION, false);
            save(skipValidation);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            save(false);
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
    }

    public TextView getStepName() {
        return stepName;
    }
    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    protected class BottomNavigationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.next || v.getId() == R.id.next_icon) {
                Object tag = v.getTag(R.id.NEXT_STATE);
                if (tag == null) {
                    next();
                } else {
                    boolean next = (boolean) tag;
                    if (next) {
                        next();
                    } else {
                        save();
                    }
                }

            } else if (v.getId() == R.id.previous || v.getId() == R.id.previous_icon) {
                assert getFragmentManager() != null;
                presenter.checkAndStopCountdownAlarm();
                getFragmentManager().popBackStack();
            }
        }
    }

}


