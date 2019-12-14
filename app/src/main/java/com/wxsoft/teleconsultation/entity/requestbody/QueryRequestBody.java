package com.wxsoft.teleconsultation.entity.requestbody;

import com.wxsoft.teleconsultation.AppContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryRequestBody implements Serializable {

    /**
     * 每页包含的数据数量.
     */
    private int sizeOfPage;

    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 数据总数.
     */
    private int totalCount;

    /**
     * 查询条件.
     */
    private List<Condition> conditions;

    private List<SortInfo> sortInfos;

    public static QueryRequestBody getPatientsRequestBody(String doctId,
                                                          String queryName,
                                                          int sizeOfPage,
                                                          int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctId", doctId));
        body.conditions.add(new Condition("Name", queryName));
        return body;
    }

    public static QueryRequestBody getConsultationWaitHandleRequestBody(String doctId,
                                                                        int sizeOfPage,
                                                                        int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        //body.conditions.add(new Condition("DoctId", doctId));
        return body;
    }

    public static QueryRequestBody getDiseaseCounselingRequestBody(String doctId,
                                                                   String status,
                                                                        int sizeOfPage,
                                                                        int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        body.conditions.add(new Condition("SheetIndex", status));
        return body;
    }

    public static QueryRequestBody getIntegralHistory1RequestBody(String doctId,
                                                                   int sizeOfPage,
                                                                   int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        return body;
    }

    public static QueryRequestBody getLiveRequestBody(String doctId,
                                                                  int sizeOfPage,
                                                                  int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        return body;
    }

    public static QueryRequestBody getTransferTreatmentRequestBody(String doctId,
                                                                   String queryType,
                                                                   String status,
                                                                   int sizeOfPage,
                                                                   int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        body.conditions.add(new Condition("QueryType", queryType));
        body.conditions.add(new Condition("Status", status));
        return body;
    }

    public static QueryRequestBody getCloudClincHistoryRequestBody(String doctId,
                                                                   String queryType,
                                                                   String status,
                                                                   int sizeOfPage,
                                                                   int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        body.conditions.add(new Condition("QueryType", queryType));
        if(status!=null)
            body.conditions.add(new Condition("Status", status));
        return body;
    }


    /**
     * 医院排班表
     * @param departmentId
     * @param hospitalId
     * @param date
     * @param sizeOfPage
     * @param currentPage
     * @return
     */
    public static QueryRequestBody getRegisterScheduDateMapByDate(String departmentId,
                                                                   String hospitalId,
                                                                   String date,
                                                                   int sizeOfPage,
                                                                   int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("ScheduDate", date));
        body.conditions.add(new Condition("DepartmentId", departmentId));
        body.conditions.add(new Condition("SelfHospitalId", hospitalId));
        return body;
    }

    public static QueryRequestBody getMyApplyRequestBody(String doctId,
                                                         String queryType,
                                                         String status,
                                                         int sizeOfPage,
                                                         int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctId", doctId));
        body.conditions.add(new Condition("QueryType", queryType));
        body.conditions.add(new Condition("Status", status));
        return body;
    }

    public static QueryRequestBody getPrescriptionConRequestBody(String doctId,
                                                         int sizeOfPage,
                                                         int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        return body;
    }

    public static QueryRequestBody getPrescriptionRequestBody(String doctId,
                                                                 boolean done,
                                                                 int sizeOfPage,
                                                                 int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        body.conditions.add(new Condition("SheetIndex", done?"1":"0"));
        return body;
    }


    public static QueryRequestBody getRegisterRequestBody(String doctId,
                                                         String queryType,
                                                         String status,
                                                         String bookType,
                                                         int sizeOfPage,
                                                         int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        body.conditions.add(new Condition("QueryType", queryType));
        body.conditions.add(new Condition("Status", status));
        body.conditions.add(new Condition("BookType", bookType));
        return body;
    }

    public static QueryRequestBody getIntegrationRequestBody(String doctId,
                                                          int sizeOfPage,
                                                          int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctorId", doctId));
        return body;
    }

    public static QueryRequestBody getDoctorsByConditionRequestBody(String cityId,
                                                                    String name,
                                                                    String goodAt,
                                                                    String departmentId,
                                                                    String positionTitle,
                                                                    int sizeOfPage,
                                                                    int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("CityId", cityId));
        body.conditions.add(new Condition("Name", name));
        body.conditions.add(new Condition("GoodAt", goodAt));
        body.conditions.add(new Condition("DepartmentId", departmentId));
        body.conditions.add(new Condition("PositionTitle", positionTitle));
        return body;
    }

    public static QueryRequestBody getRelativeDoctorsByDoctId(String doctId,
                                                              int sizeOfPage,
                                                              int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("DoctId", doctId));
        return body;
    }

    public static QueryRequestBody getQueryNotificationRequestBody(String receiveId,
                                                                   int sizeOfPage,
                                                                   int currentPage) {
        QueryRequestBody body = getCommBody(sizeOfPage, currentPage);
        body.conditions.add(new Condition("ReceiveId", receiveId));
        return body;
    }

    public static QueryRequestBody getCommBody(int sizeOfPage, int currentPage) {
        QueryRequestBody body = new QueryRequestBody();
        body.sizeOfPage = sizeOfPage;
        body.currentPage = currentPage;
        body.conditions = new ArrayList<>();
        return body;
    }

    public static QueryRequestBody getOnlineBody(String orgId,String name,String hospital,int sizeOfPage, int currentPage) {
        QueryRequestBody body = new QueryRequestBody();
        body.sizeOfPage = sizeOfPage;
        body.currentPage = currentPage;
        body.conditions = new ArrayList<>();
        if(orgId!=null)
            body.conditions.add(new Condition("OrgId",orgId));
        if(name!=null)
        body.conditions.add(new Condition("DoctorName",name));
        if(hospital!=null)
        body.conditions.add(new Condition("HospitalId",hospital));
        return body;
    }


    public static QueryRequestBody getOnlineDutyBody(String docId,String date,String hospital,int sizeOfPage, int currentPage) {
        QueryRequestBody body = new QueryRequestBody();
        body.sizeOfPage = sizeOfPage;
        body.currentPage = currentPage;
        body.conditions = new ArrayList<>();
        body.conditions.add(new Condition("DoctorId", docId));
        if(hospital!=null)
            body.conditions.add(new Condition("HospitalId",hospital));
            body.conditions.add(new Condition("DepartmentId", ""));

        if(date!=null)
            body.conditions.add(new Condition("Date",date));
        return body;
    }
    public int getSizeOfPage() {
        return sizeOfPage;
    }

    public void setSizeOfPage(int sizeOfPage) {
        this.sizeOfPage = sizeOfPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<SortInfo> getSortInfos() {
        return sortInfos;
    }

    public void setSortInfos(List<SortInfo> sortInfos) {
        this.sortInfos = sortInfos;
    }

    private static class Condition implements Serializable {

        private String fieldColumn;

        private String queryValue;

        public Condition(String fieldColumn, String queryValue) {
            this.fieldColumn = fieldColumn;
            this.queryValue = queryValue;
        }
    }

    private class SortInfo implements Serializable{

        private String sortColumn;

        private int sortDirection;
    }

}
