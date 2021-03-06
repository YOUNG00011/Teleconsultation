package com.wxsoft.telereciver.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hospital implements Serializable {

    private String id;
    private String name;
    private String introduce;
    private String telphone;
    private String imageUrl;
    private String level;
    private String address;
    @SerializedName("py")
    private String PY;
    @SerializedName("wb")
    private String WB;

    public static List<Hospital> getHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        Hospital hospital1 = new Hospital();
        hospital1.id = "11111111";
        hospital1.name = "芜湖市第二人民医院";
        hospital1.introduce = "由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。由于家庭背景优渥，何鸿燊从小过着衣食无忧的少爷生活，就读于香港最好的学校——皇仁书院。小时候的何鸿燊不爱学习，当时成绩很差，后家道没落，何鸿燊尝尽了世态炎凉，发奋读书，以优秀的成绩考入香港大学，并获得奖学金。 [2] \n" +
                "由于抗战爆发，香港失守，何鸿燊于1941年香港大学理科学院肄业后，来到澳门，进入澳门联昌贸易公司工作。因一口流利的英语，他在公司担任了秘书职务。何鸿燊的记忆力非常出众，当时澳门的两千多个电话号码他能倒背如流，再加上善于交际，周旋四方，他很快成了这家公司的得力干将，并为公司立下汗马功劳。1943年，他从公司分到了100万澳元红利。";
        hospital1.level = "三级甲等";
        hospital1.address = "芜湖市镜湖区九华山路1号";
        hospitals.add(hospital1);

        Hospital hospital2 = new Hospital();
        hospital2.id = "222222222";
        hospital2.name = "芜湖市第三人民医院";
        hospital2.introduce = "获中国内地多个城市如北京、广州等授予荣誉市民衔，" +
                "屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、" +
                "罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、" +
                "澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。" +
                "获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。" +
                "其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、" +
                "法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、" +
                "香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、" +
                "广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、" +
                "英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、" +
                "马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，" +
                "第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，" +
                "屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、" +
                "罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、" +
                "澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。" +
                "获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。" +
                "其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、" +
                "法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、" +
                "香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、" +
                "广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、" +
                "英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、" +
                "马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，" +
                "第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。获中国内地多个城市如北京、广州等授予荣誉市民衔，屡获海外各地政府颁授荣誉勋衔。其荣誉勋衔包括葡国大十字勋章勋爵、英国O.B.E衔、罗马教廷圣额我略一世剑袍爵士、法国骑士级勋章、日本瑞宝勋章、马来西亚拿督斯里荣誉勋衔、澳门东亚大学社会科学系荣誉博士、香港大学社会科学系荣誉博士，第九届全国政协常委等等。";
        hospital2.address = "芜湖市镜湖区九华山路1号";
        hospitals.add(hospital2);
        return hospitals;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getTelphone() {
        return telphone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLevel() {
        return level;
    }

    public String getAddress() {
        return address;
    }

    public String getPY() {
        return PY;
    }

    public String getWB() {
        return WB;
    }


}
