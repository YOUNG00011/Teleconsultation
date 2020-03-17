package com.wxsoft.telereciver.http.api;

import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.Confrence;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface SmcApi {

    /**
     * 获取预约挂号记录详情
     * @param id
     * @return
     */
    @POST("api/SMC/AddScheduleConf?isMobile=true")
    Observable<BaseResp> createVideoCon(@Body Confrence confrence);

    @POST("api/SMC/AddCloudClincScheduleConf?isMobile=true")
    Observable<BaseResp> createCloudVideoCon(@Body Confrence confrence);

}
