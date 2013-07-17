package com.odong.portal.util;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午2:28
 */
@Component
public class TimeHelper {


    public Date plus(Date date, int second) {
        return new DateTime(date).plusSeconds(second).toDate();
    }

    public Date nextDay(int hour) {
        return new DateTime().plusDays(1).millisOfDay().withMinimumValue().withHourOfDay(hour).toDate();
    }

    public Date max() {
        return new DateTime().withYear(9999).dayOfYear().withMaximumValue().millisOfDay().withMaximumValue().toDate();
    }
}
