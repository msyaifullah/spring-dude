package com.yyggee.eggs.configs;

import com.yyggee.eggs.constants.ConstantTenant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String currentTenant = TenantContext.getTenant();
        return currentTenant != null ? currentTenant : ConstantTenant.DEFAULT_TENANT;
    }
}
