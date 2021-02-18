package com.vijay.jsonwizard.utils;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;


/**
 * Created by keyman on 25/04/2017.
 */
public class DatePickerUtils {

    private static final int SPINNER_COUNT = 3;

    public static void themeDatePicker(DatePickerDialog dialog, char[] ymdOrder) {
        if (!dialog.isShowing()) {
            throw new IllegalStateException("Dialog must be showing");
        }

        themeDatePicker(dialog.getDatePicker(), ymdOrder);
    }

    public static void preventShowingKeyboard(DatePicker datePicker) {
        datePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    public static void themeDatePicker(DatePicker datePicker, char[] ymdOrder) {
        preventShowingKeyboard(datePicker);
        themeDatePickerCore(datePicker, ymdOrder, Resources.getSystem());
    }

    private static void themeDatePickerCore(DatePicker datePicker, char[] ymdOrder, Resources resources) {

        String appPackage = "android";

        final int idYear = resources.getIdentifier("year", "id", appPackage);
        final int idMonth = resources.getIdentifier("month", "id", appPackage);
        final int idDay = resources.getIdentifier("day", "id", appPackage);
        final int idLayout = resources.getIdentifier("pickers", "id", appPackage);

        final NumberPicker spinnerYear = datePicker.findViewById(idYear);
        final NumberPicker spinnerMonth = datePicker.findViewById(idMonth);
        final NumberPicker spinnerDay = datePicker.findViewById(idDay);
        final LinearLayout layout = datePicker.findViewById(idLayout);

        if (layout != null) {
            layout.removeAllViews();
            for (int i = 0; i < SPINNER_COUNT; i++) {
                switch (ymdOrder[i]) {
                    case 'y':
                        layout.addView(spinnerYear);
                        setImeOptions(spinnerYear, i);
                        break;
                    case 'm':
                        layout.addView(spinnerMonth);
                        setImeOptions(spinnerMonth, i);
                        break;
                    case 'd':
                        layout.addView(spinnerDay);
                        setImeOptions(spinnerDay, i);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid char[] ymdOrder");
                }
            }
        }
    }

    private static void setImeOptions(NumberPicker spinner, int spinnerIndex) {
        final int imeOptions;
        if (spinnerIndex < SPINNER_COUNT - 1) {
            imeOptions = EditorInfo.IME_ACTION_NEXT;
        } else {
            imeOptions = EditorInfo.IME_ACTION_DONE;
        }
        int idPickerInput = Resources.getSystem().getIdentifier("numberpicker_input", "id", "android");
        TextView input = (TextView) spinner.findViewById(idPickerInput);
        input.setImeOptions(imeOptions);
    }
}
