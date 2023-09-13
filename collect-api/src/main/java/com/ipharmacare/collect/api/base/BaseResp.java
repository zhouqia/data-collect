package com.ipharmacare.collect.api.base;

import com.ipharmacare.collect.api.result.IRestResult;
import com.ipharmacare.collect.api.result.RestResult;
import lombok.Getter;

@Getter
public class BaseResp {
    private Integer code;
    private String message;

    BaseResp() {
        setResult(RestResult.OK);
    }

    public final void setResult(IRestResult result) {
        setResult(result, (String) null);
    }

    public final void setResult(IRestResult result, String message) {
        this.code = result.getCode();
        this.message = message == null ? result.getMessage() : message;
    }
}