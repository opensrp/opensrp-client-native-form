package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hornet.dateconverter.Model;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.DateConverter;
import com.vijay.jsonwizard.utils.DateUtil;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.NativeFormLangUtils;
import com.vijay.jsonwizard.utils.NativeFormsProperties;
import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 25/01/2017
 */
public class DatePickerFactory implements FormWidgetFactory {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String DATE_FORMAT_REGEX = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)|\\s*";
    public static final SimpleDateFormat DATE_FORMAT_LOCALE_INDEPENDENT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private static final String TAG = "DatePickerFactory";
    private final FormUtils formUtils = new FormUtils();



    @VisibleForTesting
    protected void showDatePickerDialog(final AppCompatActivity context, DatePickerDialog datePickerDialog, final MaterialEditText editText, final TextView duration) {
        NativeFormsProperties nativeFormsProperties = JsonFormFragment.getNativeFormProperties();

        if(!nativeFormsProperties.isTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NEPAL)) {
            FragmentTransaction ft = context.getFragmentManager().beginTransaction();
            Fragment prev = context.getFragmentManager().findFragmentByTag(TAG);

            if (!(prev != null && prev.isAdded())) {

                datePickerDialog.show(ft, TAG);

                //Fragments are committed asynchronously, force commit
                context.getFragmentManager().executePendingTransactions();

                String text = editText.getText().toString();
                Calendar date = FormUtils.getDate(StringUtils.isNoneBlank(Form.getDatePickerDisplayFormat()) ?
                        Utils.formatDateToPattern(text, Form.getDatePickerDisplayFormat(), DATE_FORMAT.toPattern())
                        : text);
                if (text.isEmpty()) {
                    Object defaultValue = datePickerDialog.getArguments().get(JsonFormConstants.DEFAULT);
                    if (defaultValue != null)
                        datePickerDialog.setDate(FormUtils.getDate(defaultValue.toString()).getTime());
                    else
                        datePickerDialog.setDate(date.getTime());
                } else {
                    datePickerDialog.setDate(date.getTime());
                }
            }
        }
        else
        {
            long minDate = datePickerDialog.getMinDate();
            long maxDate = datePickerDialog.getMaxDate();
            String date = editText.getText().toString();
            Model model = new Model();
            com.hornet.dateconverter.DatePicker.DatePickerDialog dpd;
            com.hornet.dateconverter.DatePicker.DatePickerDialog.OnDateSetListener datePickerCallback =  new com.hornet.dateconverter.DatePicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.hornet.dateconverter.DatePicker.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                String BSDate;
                String yearString = ""+year;
                String monthString = monthOfYear <=9 ? "0"+monthOfYear : ""+monthOfYear;
                String dayString = dayOfMonth <= 9 ? "0"+dayOfMonth : ""+dayOfMonth;

                BSDate = yearString+"-"+monthString+"-"+dayString;
                updateDateText(context,editText,duration,BSDate);
            }
        };
            if(StringUtils.isNotBlank(date))
            {
                String[] dateString = StringUtils.split(date, "-");
                model.setDay(Integer.parseInt(dateString[2]));
                model.setMonth(Integer.parseInt(dateString[1]));
                model.setYear(Integer.parseInt(dateString[0]));
                dpd = com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance(datePickerCallback,model);

            }
            else
                dpd = com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance(datePickerCallback);

            DateConverter dateConverter = new DateConverter();
            try {
                if (minDate != -1) {
                    Date minRangeAD = new Date(minDate);
                    String minRangeADString = Utils.getStringFromDate(minRangeAD);
                    String bsMinRange = dateConverter.convertAdToBs(minRangeADString);
                    String[] bsDateMin = bsMinRange.split("-");
                    Model minModel = new Model();
                    minModel.setYear(Integer.parseInt(bsDateMin[0]));
                    minModel.setMonth(Integer.parseInt(bsDateMin[1])-1);
                    minModel.setDay(Integer.parseInt(bsDateMin[2]));
                    dpd.setMinDate(minModel);
                    Log.d("min date", bsMinRange);
                    Log.d("min date AD",minRangeADString);


                }
                if (maxDate != -1) {
                    Date maxRangeAD = new Date(maxDate);
                    String maxRangeADString = Utils.getStringFromDate(maxRangeAD);
                    String bsMaxRange = dateConverter.convertAdToBs(maxRangeADString);


                    String[] bsDateMax = bsMaxRange.split("-");
                    Model maxModel = new Model();
                    maxModel.setYear(Integer.parseInt(bsDateMax[0]));
                    maxModel.setMonth(Integer.parseInt(bsDateMax[1])-1);
                    maxModel.setDay(Integer.parseInt(bsDateMax[2]));
                    dpd.setMaxDate(maxModel);

                    Log.d("max date", bsMaxRange);
                    Log.d("max date AD",maxRangeADString);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

              dpd.show(context.getSupportFragmentManager(),"ssdsd");
        }
    }

    private void updateDateText(Context context, final MaterialEditText editText, final TextView duration, final String date) {
        NativeFormsProperties nativeFormsProperties = JsonFormFragment.getNativeFormProperties();
        final boolean bikramSambatEnabled = nativeFormsProperties.isTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NEPAL);

        ((JsonApi) context).getAppExecutors().mainThread().execute(new Runnable() {
            @Override
            public void run() {

                if(bikramSambatEnabled)
                {
                    if(!date.isEmpty())
                    {

                            editText.setText(date);
                    }
                }
                else
                editText.setText(StringUtils.isNoneBlank(Form.getDatePickerDisplayFormat()) ?
                        Utils.formatDateToPattern(date, DATE_FORMAT.toPattern(), Form.getDatePickerDisplayFormat())
                        : date);
            }
        });

        String durationString = date;

        if(bikramSambatEnabled) {
            try {
                DateConverter dateConverter = new DateConverter();
                String[] dateString = StringUtils.split(date, "-");
                String day = dateString[2];
                int month = Integer.parseInt(dateString[1])+1;
                String monthString = month <=9 ? "0"+month : ""+month;
                String year = dateString[0];
                Date BSDate = dateConverter.convertBsToAd(day + monthString + year);
                String ADDate = Utils.getStringFromDate(BSDate);
                durationString = ADDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(duration!= null) {
            String durationLabel = (String) duration.getTag(R.id.label);
            if (StringUtils.isNotBlank(durationLabel)) {
                Locale locale = getSetLanguage(context);
                String durationText = getDurationText(context, durationString, locale);
                if (StringUtils.isNotBlank(durationText)) {
                    durationText = String.format("(%s: %s)", durationLabel, durationText);
                }
                final String finalDurationText = durationText;
                ((JsonApi) context).getAppExecutors().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        duration.setText(finalDurationText);
                    }
                });
            }
        }

    }

    @NonNull
    @VisibleForTesting
    protected String getDurationText(Context context, String date, Locale locale) {
        return DateUtil.getDuration(DateUtil.getDurationTimeDifference(date, null), locale.getLanguage().equals("ar") ? Locale.ENGLISH : locale, context);
    }

    @NonNull
    @VisibleForTesting
    protected Locale getSetLanguage(Context context) {
        return new Locale(NativeFormLangUtils.getLanguage(context));
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject,
                                       CommonListener listener, boolean popup) {
        return attachJson(stepName, context, formFragment, jsonObject, popup, listener);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment,
                                       JSONObject jsonObject, CommonListener listener) throws Exception {
        return attachJson(stepName, context, formFragment, jsonObject, false, listener);
    }

    @Override
    @NonNull
    public Set<String> getCustomTranslatableWidgetFields() {
        Set<String> customTranslatableWidgetFields = new HashSet<>();
        customTranslatableWidgetFields.add(DatePickerFactory.KEY.DURATION + "." + JsonFormConstants.LABEL);
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL_INFO_TITLE);
        customTranslatableWidgetFields.add(JsonFormConstants.LABEL_INFO_TEXT);
        return customTranslatableWidgetFields;
    }

    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                    boolean popup, CommonListener listener) {
        List<View> views = new ArrayList<>(1);
        try {

            RelativeLayout dateViewRelativeLayout = getRelativeLayout(context);
            MaterialEditText editText = dateViewRelativeLayout.findViewById(R.id.edit_text);
            TextView duration = dateViewRelativeLayout.findViewById(R.id.duration);

            attachLayout(stepName, context, formFragment, jsonObject, editText, duration);

            JSONArray canvasIds = new JSONArray();
            dateViewRelativeLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(dateViewRelativeLayout.getId());
            editText.setTag(R.id.canvas_ids, canvasIds.toString());
            editText.setTag(R.id.extraPopup, popup);

            ((JsonApi) context).addFormDataView(editText);
            views.add(dateViewRelativeLayout);
            attachInfoIcon(stepName, jsonObject, dateViewRelativeLayout, canvasIds, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    @VisibleForTesting
    protected RelativeLayout getRelativeLayout(Context context) {
        return (RelativeLayout) LayoutInflater.from(context).inflate(getLayout(), null);
    }

    protected void attachLayout(String stepName, final Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                final MaterialEditText editText, final TextView duration) {

        try {
            String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
            String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
            String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
            String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
            String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
            String calculations = jsonObject.optString(JsonFormConstants.CALCULATION);

            duration.setTag(R.id.key, jsonObject.getString(KEY.KEY));
            duration.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            duration.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            duration.setTag(R.id.openmrs_entity, openMrsEntity);
            duration.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            if (jsonObject.has(DatePickerFactory.KEY.DURATION) && jsonObject.getJSONObject(DatePickerFactory.KEY.DURATION) != null) {
                duration.setTag(R.id.label, jsonObject.getJSONObject(DatePickerFactory.KEY.DURATION).getString(JsonFormConstants.LABEL));
            }

            updateEditText(editText, jsonObject, stepName, context, duration);
            editText.setTag(R.id.json_object, jsonObject);

            final DatePickerDialog datePickerDialog = createDateDialog(context, duration, editText, jsonObject);
            if (formFragment != null) {
                NativeFormsProperties nativeFormsProperties = JsonFormFragment.getNativeFormProperties();
                if (nativeFormsProperties != null) {
                    datePickerDialog.setNumericDatePicker(nativeFormsProperties.isTrue(NativeFormsProperties.KEY.WIDGET_DATEPICKER_IS_NUMERIC));
                }
            }

            if (jsonObject.has(JsonFormConstants.EXPANDED) && jsonObject.getBoolean(JsonFormConstants.EXPANDED)
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.setCalendarViewShown(true);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.setCalendarViewShown(false);
            }

            GenericTextWatcher genericTextWatcher = getGenericTextWatcher(stepName, (Activity) context, formFragment,
                    editText, datePickerDialog);
            editText.addTextChangedListener(genericTextWatcher);
            addRefreshLogicView(context, editText, relevance, constraints, calculations);
            Bundle datePickerArgs = new Bundle();
            datePickerArgs.putString(JsonFormConstants.DEFAULT, jsonObject.optString(JsonFormConstants.DEFAULT));
            datePickerDialog.setArguments(datePickerArgs);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog((AppCompatActivity) context, datePickerDialog, editText,duration);
                }
            });

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    updateDateText(context, editText, duration, "");
                    return true;
                }
            });
            editText.setFocusable(false);
        } catch (Exception e) {
            Timber.e(e.getMessage(), TAG);
        }

    }

    private void attachInfoIcon(String stepName, JSONObject jsonObject, RelativeLayout rootLayout, JSONArray canvasIds,
                                CommonListener listener) throws JSONException {
        if (jsonObject.has(JsonFormConstants.LABEL_INFO_TEXT)) {
            ImageView infoIcon = rootLayout.findViewById(R.id.date_picker_info_icon);
            formUtils.showInfoIcon(stepName, jsonObject, listener, FormUtils.getInfoDialogAttributes(jsonObject), infoIcon, canvasIds);
        }

    }

    @NonNull
    private GenericTextWatcher getGenericTextWatcher(String stepName, final Activity context, JsonFormFragment formFragment,
                                                     final MaterialEditText editText,
                                                     final DatePickerDialog datePickerDialog) {
        GenericTextWatcher genericTextWatcher = new GenericTextWatcher(stepName, formFragment, editText);
        genericTextWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.setArguments(new Bundle());
                    showDatePickerDialog((AppCompatActivity) context, datePickerDialog, editText,null);
                }
            }
        });
        return genericTextWatcher;
    }

    private void addRefreshLogicView(Context context, MaterialEditText editText, String relevance, String constraints,
                                     String calculations) {
        if (StringUtils.isNotBlank(relevance) && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }

        if (StringUtils.isNotBlank(constraints) && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }

        if (StringUtils.isNotBlank(calculations) && context instanceof JsonApi) {
            editText.setTag(R.id.calculation, calculations);
            ((JsonApi) context).addCalculationLogicView(editText);
        }
    }

    private void updateEditText(MaterialEditText editText, JSONObject jsonObject, String stepName, Context context, TextView duration) throws JSONException {

        Locale locale = getCurrentLocale(context);
        final SimpleDateFormat DATE_FORMAT_LOCALE = new SimpleDateFormat("dd-MM-yyyy", locale);

        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String dateValue = "";
        if (StringUtils.isNotBlank(jsonObject.optString(KEY.VALUE))) {
            dateValue = StringUtils.isNoneBlank(Form.getDatePickerDisplayFormat())
                    ? Utils.formatDateToPattern(jsonObject.optString(KEY.VALUE), Form.getDatePickerDisplayFormat(), FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN)
                    : jsonObject.optString(KEY.VALUE);
        }

        editText.setHint(jsonObject.getString(KEY.HINT));
        editText.setFloatingLabelText(jsonObject.getString(KEY.HINT));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString(KEY.KEY));
        editText.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString(KEY.KEY));
        editText.setTag(R.id.locale_independent_value, dateValue);

        if (jsonObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject requiredObject = jsonObject.optJSONObject(JsonFormConstants.V_REQUIRED);
            boolean requiredValue = requiredObject.getBoolean(KEY.VALUE);
            if (Boolean.TRUE.equals(requiredValue)) {
                editText.addValidator(new RequiredValidator(requiredObject.getString(JsonFormConstants.ERR)));
                FormUtils.setRequiredOnHint(editText);
            }
        }

        if (StringUtils.isNotBlank(dateValue)) {
            updateDateText(context, editText, duration, DATE_FORMAT_LOCALE.format(FormUtils.getDate(dateValue).getTime()));
        }

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            boolean readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
            editText.setEnabled(!readOnly);
            editText.setFocusable(!readOnly);
        }
    }

    @VisibleForTesting
    protected Locale getCurrentLocale(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage().equals("ar") ? Locale.ENGLISH : context.getResources().getConfiguration().locale;//Arabic should render normal numbers/numeric digits
    }

    protected DatePickerDialog createDateDialog(final Context context, final TextView duration, final MaterialEditText editText,
                                                JSONObject jsonObject) throws JSONException {
        final DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.setContext(context);

        Locale locale = getCurrentLocale(context);
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", locale);

        datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                boolean isBSDate = true;
                Calendar calendarDate = Calendar.getInstance();
                calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarDate.set(Calendar.MONTH, monthOfYear);
                calendarDate.set(Calendar.YEAR, year);

                editText.setTag(R.id.locale_independent_value, DATE_FORMAT_LOCALE_INDEPENDENT.format(calendarDate.getTime()));


//                if(isBSDate)
//                {
//                    DateConverter dateConverter = new DateConverter();
//                    int month = monthOfYear+1;
//                    String BSDay = dayOfMonth+"";
//                    String BSMonth = monthOfYear+"";
//
//                    try {
//                        if(dayOfMonth<=9)
//                        {
//                            BSDay = "0"+BSDay;
//                        }
//                        if(month <= 9)
//                        {
//                            BSMonth = "0"+month;
//                        }
//
//                        String date =   dateConverter.convertAdToBs(BSDay+"-"+BSMonth+"-"+year);
//                        Log.d("BS date",date);
//                        updateDateText(context,editText,duration,date);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }

               // }
                //else {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                            && calendarDate.getTimeInMillis() >= view.getMinDate()
                            && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                        updateDateText(context, editText, duration,
                                DATE_FORMAT.format(calendarDate.getTime()));
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        updateDateText(context, editText, duration, "");
                    }

            }
          //  }
        });

        if (jsonObject.has(JsonFormConstants.MIN_DATE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Calendar minDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MIN_DATE));
            minDate.set(Calendar.HOUR_OF_DAY, 0);
            minDate.set(Calendar.MINUTE, 0);
            minDate.set(Calendar.SECOND, 0);
            minDate.set(Calendar.MILLISECOND, 0);
            datePickerDialog.setMinDate(minDate.getTimeInMillis());
        }

        if (jsonObject.has(JsonFormConstants.MAX_DATE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Calendar maxDate = FormUtils.getDate(jsonObject.getString(JsonFormConstants.MAX_DATE));
            maxDate.set(Calendar.HOUR_OF_DAY, 23);
            maxDate.set(Calendar.MINUTE, 59);
            maxDate.set(Calendar.SECOND, 59);
            maxDate.set(Calendar.MILLISECOND, 999);
            datePickerDialog.setMaxDate(maxDate.getTimeInMillis());
        }

        return datePickerDialog;
    }


    protected int getLayout() {
        return R.layout.native_form_item_date_picker;
    }

    public static class KEY {
        public static final String DURATION = "duration";
        public static final String HINT = "hint";
        public static final String KEY = "key";
        public static final String VALUE = (JsonFormConstants.VALUE);
        public static final String DEFAULT = "default";
    }
}
