package com.yjy.examonline.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component("dateFormatter")
public class DateFormatter implements Converter<String, Date> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Date convert(String source) {
        //yyyy-MM-dd'T'HH:mm
        log.info("date format [{}]", source);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        try {
            return df.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("date format error");
            return null;
        }
    }
}
