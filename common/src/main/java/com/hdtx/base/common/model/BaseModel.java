package com.hdtx.base.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: ghx
 * @date 2021/8/13
 * @describe: 基类
 */
@Data
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建日期
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    /**
     * 修改日期
     */
    @ApiModelProperty(hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedDate;

    /**
     * 创建人id
     */
    @ApiModelProperty(hidden = true)
    private String createdBy;

    /**
     * 修改人id
     */
    @ApiModelProperty(hidden = true)
    private String updatedBy;

    /**
     * 删除标志 0:未删除 1：已删除
     */
    @ApiModelProperty(hidden = true)
    @TableLogic
    @TableField(value = "delete_flag", fill = FieldFill.INSERT) // 新增执行
    private Integer deleteFlag;
}
