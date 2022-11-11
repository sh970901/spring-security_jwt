package com.ll.exam.springsecurityjwt.app.base.dto;

import com.ll.exam.springsecurityjwt.app.member.security.entity.MemberContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//ResponseEntity의 헤더, 바디, 상태코드 중 바디를 커스텀하기 위한 ResultData 클래스
@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    public static <T> RsData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    public static <T> RsData<T> successOf(T data) {
        return of("S-1", "성공", data);
    }

    public static <T> RsData<T> failOf(T data) {
        return of("F-1", "실패", data);
    }

    public boolean isSuccess() {
        return resultCode.startsWith("S-1");
    }

    public boolean isFail() {
        return isSuccess() == false;
    }
}
