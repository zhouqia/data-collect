package com.ipharmacare.collect.common.constants.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author : fujin
 * @date : 2020/12/22
 */
@Getter
public enum SexEnum {

    NONE(0, "未知"),
    MAN(1, "男"),
    WOMEN(2, "女");

    private final Integer gender;

    private final String desc;

    SexEnum(Integer gender, String desc) {
        this.gender = gender;
        this.desc = desc;
    }

    public static SexEnum of(Integer genderCode) {
        return Stream.of(SexEnum.values()).filter(sexEnum ->
                sexEnum.getGender().equals(genderCode))
                .findFirst().orElse(NONE);
    }

    public static SexEnum of(String desc) {
        return Stream.of(SexEnum.values()).filter(sexEnum ->
                sexEnum.desc.equals(desc))
                .findFirst().orElse(NONE);
    }
}
