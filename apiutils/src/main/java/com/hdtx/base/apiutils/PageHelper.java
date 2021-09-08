package com.hdtx.base.apiutils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdtx.base.apiutils.exception.AppBusinessException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: ghx
 * @date 2021/8/13
 * @describe: 分页
 */
@Slf4j
@Data
public class PageHelper {
    @ApiModelProperty(value = "每页数量(不传时默认10)")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页数(不传时默认1)")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "排序字段名称：如：id,createdDate")
    private String orderFiled;

    @ApiModelProperty(value = "排序方式：desc: 倒叙排序 ,asc: 升序排序")
    private String orderType;

    @ApiModelProperty(value = "查询字段名称：userName，age")
    private String searchFiled;

    @ApiModelProperty(value = "关键字查询: 小林")
    private String search;

    @ApiModelProperty(value = "组合查询：key:要查询得字段  type: = ,>,<,<=,>=,!=(不等于),like(模糊查询)  value：值 例如：小林  组合用例：[ { \"key\":\"contacts\", \"type\":\"like\", \"value\":\"小\" }  ]")
    private List searchMul;

    @ApiModelProperty("精确查询")
    private Map<String, Object> preciseQuery;

    @ApiModelProperty(value = "excel导出文件名")
    private String fileName;


    /**
     * 设置排序、模糊搜索
     */
    public <T> void queryWrapper(QueryWrapper<T> queryWrapper, Class<?> clazz) {
        if (StrUtil.isNotEmpty(this.searchFiled) && StrUtil.isNotEmpty(this.search)) {
            boolean checkSearchField = ModelUtils.checkField(clazz, this.searchFiled);
            if (!checkSearchField) {
                log.error("参数错误，" + this.searchFiled + "不存在");
                throw new AppBusinessException("参数错误，" + this.searchFiled + "不存在");
            }
            queryWrapper.like(ModelUtils.humpToLine(this.searchFiled), this.search);
        }
        if (this.orderFiled != null && !this.orderFiled.equals("")) {
            boolean checkOrderField = ModelUtils.checkField(clazz, this.orderFiled);
            if (!checkOrderField) {
                log.error("参数错误，" + this.orderFiled + "不存在");
                throw new AppBusinessException(String.format("参数错误,%s 不存在", this.orderFiled));
            }
            if ("asc".equals(this.orderType)) {
                queryWrapper.orderByAsc(ModelUtils.humpToLine(this.orderFiled));
            } else {
                queryWrapper.orderByDesc(ModelUtils.humpToLine(this.orderFiled));
            }
        }
        if (!ObjectUtils.isEmpty(this.preciseQuery)) {
            this.preciseQuery.keySet().forEach(k -> {
                if (!ModelUtils.checkField(clazz, k)) {
                    log.warn("参数错误，字段:{} 不存在", k);
                    throw new AppBusinessException(String.format("参数错误,%s 不存在", k));
                }
                queryWrapper.eq(ModelUtils.humpToLine(k), this.preciseQuery.get(k));
            });
        }

        //组合查询
        if (this.searchMul != null && !this.searchMul.equals("")) {
            if (!JSONUtil.isJsonArray(String.valueOf(this.searchMul))) {
                throw new AppBusinessException("输入的json格式有误");
            }
            JSONArray myJsonArray = JSONUtil.parseArray(this.searchMul);
            for (int i = 0; i < myJsonArray.size(); i++) {
                String key = myJsonArray.getJSONObject(i).get("key").toString();
                String type = myJsonArray.getJSONObject(i).get("type").toString();
                String value = myJsonArray.getJSONObject(i).get("value").toString();
                if (StrUtil.isNotBlank(key) && StrUtil.isNotBlank(type) && StrUtil.isNotBlank(value)) {
                    switch (type) {
                        case "=":
                            queryWrapper.eq(ModelUtils.humpToLine(key), value);
                            break;
                        case ">":
                            queryWrapper.gt(ModelUtils.humpToLine(key), value);
                            break;
                        case ">=":
                            queryWrapper.ge(ModelUtils.humpToLine(key), value);
                            break;
                        case "<":
                            queryWrapper.lt(ModelUtils.humpToLine(key), value);
                            break;
                        case "<=":
                            queryWrapper.le(ModelUtils.humpToLine(key), value);
                            break;
                        case "!=":
                            queryWrapper.ne(ModelUtils.humpToLine(key), value);
                            break;
                        case "like":
                            queryWrapper.like(ModelUtils.humpToLine(key), value);
                            break;
                        case "or":
                            queryWrapper.or().eq(ModelUtils.humpToLine(key), value);
                            break;
                        case "between":
                            String[] str = value.split("and");
                            if (ObjectUtil.isEmpty(str)) return;
                            queryWrapper.between(ModelUtils.humpToLine(key), str[0], str[1]);
                            break;

                    }
                }

            }
        }
    }

    public <T> Page<T> page() {
        if (this.pageNum == null || this.pageNum == 0) {
            this.pageNum = 1;
        }
        if (this.pageSize == null || this.pageSize == 0) {
            this.pageSize = 10;
        }
        return new Page<>(this.pageNum, this.pageSize);
    }
}
