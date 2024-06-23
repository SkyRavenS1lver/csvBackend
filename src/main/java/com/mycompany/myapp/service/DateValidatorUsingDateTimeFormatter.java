package com.mycompany.myapp.service;

import com.mycompany.myapp.constant.Constant;
import com.mycompany.myapp.domain.Patient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.Locale;

public interface DateValidatorUsingDateTimeFormatter {
    DateTimeFormatter dateFormatterForCSV = DateTimeFormatter.ofPattern(Constant.DATE_VALID_FORMAT).withResolverStyle(ResolverStyle.STRICT);

    default void isValidAndConvert(Patient data) {
        try {
            String tempDate = data.getDob();
            this.dateFormatterForCSV.parse(tempDate);
            SimpleDateFormat formatter = new SimpleDateFormat(Constant.DATE_VALID_FORMAT, Locale.ENGLISH);
            Date date = formatter.parse(tempDate);
            DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_VALIDATOR_FORMAT);
            data.setDob(dateFormat.format(date));
        } catch (Exception ignored) {
            data.setDob("  ");
        }
    }
}
