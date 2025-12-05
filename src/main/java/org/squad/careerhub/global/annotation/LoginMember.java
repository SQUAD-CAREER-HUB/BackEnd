package org.squad.careerhub.global.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 현재 Mock 스웨거 API 작성을 위해 작동하지 않는 Custom Annotation을 생성했습니다.
 * 이 Custom Annotation은 현재 작동되지 않으며 추후 Spring Security 관련 기능 구현 후 정상적으로 작동될 예정입니다.
 */

@Parameter(hidden = true)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMember {

}