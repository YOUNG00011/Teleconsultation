package com.wxsoft.teleconsultation.http.api;

import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMent;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMentReceive;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface TransferTreatmentApi {

    @POST("api/TransferTreatment/QueryTransferTreatmentByPage?isMobile=true")
    Observable<BaseResp<QueryResponseData<TreatMent>>> getTreats(@Body QueryRequestBody body);

    /**
     *获取咨询详情
     * @param id 咨询号id
     * @return
     */
    @GET("api/TransferTreatment/GetTransferTreatmentById?isMobile=true")
    Observable<BaseResp<TreatMent>> getDetail(@Query("recordId") String id);



    /**
     *取消转诊
     * @param id 咨询号id
     * @return
     */
    @GET("api/TransferTreatment/CancelTransferTreatment?isMobile=true")
    Observable<BaseResp<TreatMent>> cancel(@Query("recordId") String id,@Query("reason")String reason);

    /**
     *拒绝转诊
     * @param id 咨询号id
     * @return
     */
    @GET("api/TransferTreatment/RefuseTransferTreatment?isMobile=true")
    Observable<BaseResp<TreatMent>> refuse(@Query("recordId") String id,@Query("reason")String reason);

    /**
     *完成转诊
     * @param id 咨询号id
     * @return
     */
    @GET("api/TransferTreatment/CompleteTransferTreatment?isMobile=true")
    Observable<BaseResp<TreatMent>> submit(@Query("recordId") String id,@Query("summary")String reason);


    /**
     *获取咨询详情
     * @param id 咨询号id
     * @return
     */
    @POST("api/TransferTreatment/RecivedTransferTreatment?isMobile=true")
    Observable<BaseResp<TreatMentReceive>> receive(@Body TreatMentReceive receive);
}
