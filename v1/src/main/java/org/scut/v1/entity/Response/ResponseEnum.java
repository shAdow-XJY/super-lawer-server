package org.scut.v1.entity.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ResponseEnum {
    SUCCESS(1, "请求成功"),
    ERROR(0, "发生错误"),

    //1xx 服务器错误
    SERVLET_ERROR(102, "servlet请求异常"),//-2xx 参数校验
    FILE_UPLOAD_FAILED(103, "文件上传失败"),

    //2xx 用户错误
    USER_NOT_LOGIN(201, "用户未登陆"),
    NOT_LAWER_ERROR(202, "用户非律师"),
    CHECK_CODE_ERROR(203, "验证码不正确"),
    CHECK_CODE_EXPIRED(204, "验证码已过期"),
    MAIL_IS_NULL(205, "用户邮箱为空"),
    USER_HAS_REGISTER(206, "账号已经被注册"),
    USER_LOGIN_EXPIRE(207, "本次登陆已过期，请重新登陆"),
    PASSPORT_NOT_EXIST(208, "用户账号不存在"),
    PASSPORT_IS_NULL(209, "用户账号为空"),
    PASSWORD_IS_NULL(210, "用户密码为空"),
    PASSWORD_ERROR(211, "账号密码不正确"),
    ID_NUMBER_ERROR(212, "身份证号码错误"),
    USER_HAD_REGISTER(213, "用户已经认证，不可重复认证"),
    PHONE_FORMAT_ERROR(214, "手机号码格式错误"),
    NOT_ENTERPRISE_ERROR(215, "非企业用户禁止申请项目"),
    NOT_ADMIN_ERROR(216, "非管理员禁止操作"),
    ERROR_UNAUTH(217, "未认证用户禁止操作"),


    //3xx案件错误
    CASE_ID_IS_NULL(301, "案件id为空"),
    CASE_NOT_EXISt(302, "案件不存在"),
    CASE_STATUS_NULL(303, "案件状态为空"),
    CASE_PARTY_EXIT(304,"当事人在案件中已经存在"),
    AUTH_ILLEGAL(305,"非案件创建人，请求非法"),
    CASE_FILE_UPLOAD_FAIL(306,"案件文件上传失败"),
    CASE_FILE_EXIST(307,"案件文件已经存在"),
    CASE_FILE_DELETE_FAIL(308,"案件文件删除失败"),
    CASE_FILE_DOWNLOAD_FAIL(308,"案件文件删除失败"),
    CASE_RECORD_NOT_EXIST(309,"案件记录不存在"),
    CASE_STATE_ERROR(310,"案件状态错误"),
    NOT_ASSIGN_LAWER(311,"非分配律师，禁止处理"),;

    private final Integer code;
    private final String message;

}
