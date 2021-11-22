package com.vijay.jsonwizard.comparisons;

import com.vijay.jsonwizard.utils.Utils;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.smartregister.client.utils.domain.Form;

import java.util.Date;

public class GreaterThanComparison extends Comparison {
    @Override
    public boolean compare(String a, String type, String b) {
        try {
            switch (type) {
                case TYPE_STRING:
                    if (a == null) {
                        a = DEFAULT_STRING;
                    }
                    return a.compareTo(b) > 0;
                case TYPE_NUMERIC:
                    if (a == null) {
                        a = DEFAULT_NUMERIC;
                    }
                    if (b == null) {
                        b = DEFAULT_NUMERIC;
                    }
                    return Double.valueOf(a) > Double.valueOf(b);
                case TYPE_DATE:
                    if (a == null) {
                        a = DEFAULT_DATE;
                    }
                    if (b == null) {
                        b = DEFAULT_DATE;
                    }
                    Date dateA = DatePickerFactory.DATE_FORMAT.parse(Utils.getDateFormattedForCalculation(a,  Form.getDatePickerDisplayFormat()));
                    Date dateB = DatePickerFactory.DATE_FORMAT.parse(Utils.getDateFormattedForCalculation(b,  Form.getDatePickerDisplayFormat()));
                    return dateA.getTime() > dateB.getTime();
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getFunctionName() {
        return "greaterThan";
    }
}

