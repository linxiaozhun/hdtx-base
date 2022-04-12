/*    */ package com.hdtx.base.apiutils.api;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CommonEnum
/*    */   implements BaseErrorInfo
/*    */ {
/* 11 */   SUCCESS(200, "成功!"),
/* 12 */   SYSTEM_ERROR(500, "系统异常"),
/* 13 */   METHOD_NOT_ALLOWED(403, "服务器拒绝响应"),
/* 14 */   BAD_METHOD(405, "请重新登陆"),
/* 15 */   NO_ACCESS(401, "无访问权限");
/*    */ 
/*    */ 
/*    */   
/*    */   private int resultCode;
/*    */ 
/*    */ 
/*    */   
/*    */   private String resultMsg;
/*    */ 
/*    */ 
/*    */   
/*    */   CommonEnum(int resultCode, String resultMsg) {
/* 28 */     this.resultCode = resultCode;
/* 29 */     this.resultMsg = resultMsg;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getResultCode() {
/* 34 */     return this.resultCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getResultMsg() {
/* 39 */     return this.resultMsg;
/*    */   }
/*    */ }


/* Location:              D:\hdtx-base\apiutils\target\apiutils-2.0-SNAPSHOT.jar!\com\hdtx\base\apiutils\api\CommonEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */