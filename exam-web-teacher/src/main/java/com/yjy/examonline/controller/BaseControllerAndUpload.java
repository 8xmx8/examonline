package com.yjy.examonline.controller;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;

public abstract class BaseControllerAndUpload {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected ResponseEntity<byte[]> downTemplateEXCLX(String path, String downXLSX) throws IOException {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(path);
            byte[] bs = new byte[is.available()];
            IOUtils.read(is, bs);

            HttpHeaders headers = new HttpHeaders();
            headers.add("content-disposition", "attachment;filename=" + downXLSX);
            headers.add("content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            return new ResponseEntity<byte[]>(bs, headers, HttpStatus.OK);
        } catch (NullPointerException e) {
            log.debug("[{}],路径出现问题，请到控制层或CommonDate中进行调试", path);
            throw new RuntimeException(e);
        }


    }

}
