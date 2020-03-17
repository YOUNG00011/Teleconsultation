package com.wxsoft.telereciver.entity.requestbody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateUserInfoBody {

    private String userId;

    private List<FieldCondition> conditions;

    public static UpdateUserInfoBody updateUserEmailBody(String userId,
                                                       String email) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("Email", email));
        return body;
    }

    public static UpdateUserInfoBody updateUserDynamicBody(String userId,
                                                         String dynamic) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("Dynamic", dynamic));
        return body;
    }


    public static UpdateUserInfoBody updateUserEducationBody(String userId,
                                                           String education) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("Education", education));
        return body;
    }

    public static UpdateUserInfoBody updateUserYearWorkBody(String userId,
                                                        String yearWork		) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("YearWork", yearWork));
        return body;
    }

    public static UpdateUserInfoBody updateUserIntroduceBody(String userId,
                                                            String introduce) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("Introduce", introduce));
        return body;
    }

    public static UpdateUserInfoBody updateUserGoodatBody(String userId,
                                                          String goodat) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("GoodAt", goodat));
        return body;
    }

    public static UpdateUserInfoBody updateUserAchievementBody(String userId,
                                                             String achievement) {
        UpdateUserInfoBody body = getCommBody(userId);
        body.conditions.add(new FieldCondition("Achievement", achievement));
        return body;
    }

    private static UpdateUserInfoBody getCommBody(String userId) {
        UpdateUserInfoBody body = new UpdateUserInfoBody();
        body.userId = userId;
        body.conditions = new ArrayList<>();
        return body;
    }

    private static class FieldCondition implements Serializable {

        private String fieldColumn;

        private String value;

        public FieldCondition(String fieldColumn, String value) {
            this.fieldColumn = fieldColumn;
            this.value = value;
        }
    }
}
