/*    */ package com.hdtx.base.apiutils.api;



/*    */ public class ErrorInfo
/*    */ {
/*    */   private String code;
/*    */   private String message;
/*    */   private String requestUri;
/*    */   private int status;
/*    */   
/*    */   @Deprecated
/*    */
/*    */   public ErrorInfo( String code,  String requestUri, String message) {
/* 24 */     this(code, requestUri, message, 500);
/*    */   }
/*    */   
/*    */   public ErrorInfo(ErrorCode errorCode, String requestUri) {
/* 28 */     this(errorCode, requestUri, (String)null);
/*    */   }
/*    */   
/*    */   public ErrorInfo(ErrorCode errorCode, String requestUri, String message) {
/* 32 */     this(errorCode.getCode(), requestUri, (message == null) ? errorCode.getMessage() : message, errorCode.getStatus());
/*    */   }
/*    */   
/*    */   public ErrorInfo(String code, String requestUri, String message, int status) {
/* 36 */     this.code = code;
/* 37 */     this.requestUri = requestUri;
/* 38 */     this.message = message;
/* 39 */     this.status = status;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getCode() {
/* 46 */     return this.code;
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 50 */     return this.message;
/*    */   }
/*    */   
/*    */   public String getRequestUri() {
/* 54 */     return this.requestUri;
/*    */   }
/*    */   
/*    */   public int getStatus() {
/* 58 */     return this.status;
/*    */   }
/*    */   
/*    */   public void setCode(String code) {
/* 62 */     this.code = code;
/*    */   }
/*    */   
/*    */   public void setMessage(String message) {
/* 66 */     this.message = message;
/*    */   }
/*    */   
/*    */   public void setRequestUri(String requestUri) {
/* 70 */     this.requestUri = requestUri;
/*    */   }
/*    */   
/*    */   public void setStatus(int status) {
/* 74 */     this.status = status;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     return "ErrorInfo{code='" + this.code + '\'' + ", message='" + this.message + '\'' + ", requestUri='" + this.requestUri + '\'' + ", status=" + this.status + '}';
/*    */   }
/*    */ }

