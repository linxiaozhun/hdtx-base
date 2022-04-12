/*    */ package com.hdtx.base.apiutils.api;
 public class BooleanWrapper
/*    */ {
/*    */   private boolean success;
/*    */   private String message;
/*    */   
/*    */   public BooleanWrapper(boolean success) {
/* 17 */     this(success, null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    *
/*    */   public BooleanWrapper( boolean success, String message) {
/* 24 */     this.success = success;
/* 25 */     this.message = message;
/*    */   }
/*    */   
/*    */  public boolean isSuccess() {
/* 29 */     return this.success;
/*    */   }
/*    */   
 public String getMessage() {
   return this.message;
  }
}


