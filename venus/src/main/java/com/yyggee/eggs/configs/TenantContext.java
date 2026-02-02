package com.yyggee.eggs.configs;

import com.yyggee.eggs.constants.ConstantTenant;

public class TenantContext {

  private static final ThreadLocal<String> tenant = new ThreadLocal<>();

  public static void setTenant(String tenantId) {
    if (ConstantTenant.A_TENANT.equals(tenantId) || ConstantTenant.B_TENANT.equals(tenantId)) {
      tenant.set(tenantId);
    }
  }

  public static String getTenant() {
    return tenant.get();
  }

  public static void clear() {
    tenant.remove();
  }
}
