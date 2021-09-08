package com.hdtx.base.apiutils;

import lombok.Getter;

@Getter
public class BasePageReq {
    private int pageNum = 1;
    private int pageSize = 10;
    
    // 用于分页
    protected int offset = 0;
    
    
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
		
		caculateOffset();
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		
		caculateOffset();
	}

	
	private void caculateOffset() {
		this.offset = (pageNum - 1) * pageSize;
		
	}
    
    
}
