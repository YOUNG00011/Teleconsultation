package com.wxsoft.telereciver.entity.responsedata;

import com.wxsoft.telereciver.entity.requestbody.QueryRequestBody;

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
