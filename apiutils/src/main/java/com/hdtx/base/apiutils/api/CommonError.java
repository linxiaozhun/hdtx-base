/*    */ package com.hdtx.base.apiutils.api;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CommonError
/*    */   implements ErrorCode
/*    */ {
/*  9 */   BAD_REQUEST(400, "请求的参数个数或格式不符合要求"),
/* 10 */   INVALID_ARGUMENT(400, "非法参数"),
/* 11 */   UNAUTHORIZED(401, "无权访问"),
/* 12 */   FORBIDDEN(403, "禁止访问"),
/* 13 */   NOT_FOUND(404, "请求的地址不正确"),
/* 14 */   METHOD_NOT_ALLOWED(405, "不支持的HTTP请求方法"),
/* 15 */   NOT_ACCEPTABLE(406, "不接受的请求"),
/* 16 */   CONFLICT(409, "资源冲突"),
/* 17 */   UNSUPPORTED_MEDIA_TYPE(415, "不支持的Media Type"),
/* 18 */   INTERNAL_ERROR(500, "服务器内部错误"),
/* 19 */   REQUEST_SERVICE_ERROR(500, "请求服务失败"),
/* 20 */   SERVICE_UNAVAILABLE(500, "服务不可用"),
/* 21 */   GATEWAY_TIMEOUT(500, "请求服务超时"),
/*    */   
/* 23 */   SERVICE_UNAUTH(491, "客户端没有有效的AccessToken"),
/* 24 */   SERVICE_FORBIDDEN(493, "客户端AccessToken权限不足");
/*    */   
/*    */   private int status;
/*    */   
/*    */   private String message;
/*    */   
/*    */   CommonError(int status, String message) {
/* 31 */     this.status = status;
/* 32 */     this.message = message;
/*    */   }
/*    */   
/*    */   public static CommonError fromHttpStatus(int httpStatus) {
/* 36 */     for (CommonError errorCode : values()) {
/* 37 */       if (errorCode.getStatus() == httpStatus) {
/* 38 */         return errorCode;
/*    */       }
/*    */     } 
/* 41 */     return INTERNAL_ERROR;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCode() {
/* 47 */     return name();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStatus() {
/* 52 */     return this.status;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 57 */     return this.message;
/*    */   }
/*    */ }

