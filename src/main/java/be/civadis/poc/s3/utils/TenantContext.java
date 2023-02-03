package be.civadis.poc.s3.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {
    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class.getName());
    private static final ThreadLocal<String> currentTenant = new ThreadLocal();

    private TenantContext() {
    }

    public static String getCurrentTenant() {
        return (String)currentTenant.get();
    }

    public static void setCurrentTenant(String tenant) {
        clearCurrentTenant();
        currentTenant.set(tenant);
    }

    public static void clearCurrentTenant() {
        logger.debug("Clear tenant");
        currentTenant.remove();
    }
}
