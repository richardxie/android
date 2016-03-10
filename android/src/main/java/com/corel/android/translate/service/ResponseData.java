package com.corel.android.translate.service;

import java.util.List;

import lombok.Data;

/**
 * Created by 强 on 3/10 0010.
 */
@Data
public class ResponseData {
    int errNum;
    String errMsg;
    RetData retData;

    @Data
    public static class RetData {
        String from;
        String to;
        List<Trans_result> trans_result;

        @Data
        public static class Trans_result {
            String src;
            String dst;
        }
    }
}
