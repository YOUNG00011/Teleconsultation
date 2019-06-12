package com.wxsoft.teleconsultation.entity.responsedata;

import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;

import java.util.List;

public class QueryResponseData<T> {

    private QueryRequestBody queryObject;

    private List<T> resultData;

    public QueryRequestBody getQueryObject() {
        return queryObject;
    }

    public List<T> getResultData() {
        return resultData;
    }
}
