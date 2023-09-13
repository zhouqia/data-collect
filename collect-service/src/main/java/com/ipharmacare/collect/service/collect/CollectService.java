package com.ipharmacare.collect.service.collect;


import com.ipharmacare.collect.api.collect.request.CollectRequest;



public interface CollectService {

    void collect(CollectRequest req);


    void collectString(String string);
}
