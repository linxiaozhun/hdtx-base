/*    */ package com.hdtx.base.apiutils.api;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ErrorCode
/*    */ {
/*    */   public static final int MIN_BUSINESS_ERROR_STATUS = 600;
/*    */   public static final int MAX_BUSINESS_ERROR_STATUS = 999;
/*    */   
/*    */   static boolean isBusinessStatus(int httpStatus) {
/* 14 */     return (httpStatus >= 600 && httpStatus <= 999);
/*    */   }
/*    */   
/*    */   @Deprecated
/*    */   String getCode();
/*    */   
/*    */   int getStatus();
/*    */   
/*    */   String getMessage();
/*    */ }


/* Location:              D:\hdtx-base\apiutils\target\apiutils-2.0-SNAPSHOT.jar!\com\hdtx\base\apiutils\api\ErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */