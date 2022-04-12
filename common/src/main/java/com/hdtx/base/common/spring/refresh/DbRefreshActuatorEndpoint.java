package com.hdtx.base.common.spring.refresh;

import com.hdtx.base.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RestControllerEndpoint(id = "dbRefresh")
public class DbRefreshActuatorEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DbRefreshActuatorEndpoint.class);

    @Autowired
    DbRefreshService dbRefreshService;

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String refreshDb(@RequestBody DbRefreshCommand dbRefreshCommand) {

        List<String> refreshSuccessDbKeyList = dbRefreshService.refreshDb(dbRefreshCommand);
        return JsonUtils.object2Json(refreshSuccessDbKeyList);

    }


}