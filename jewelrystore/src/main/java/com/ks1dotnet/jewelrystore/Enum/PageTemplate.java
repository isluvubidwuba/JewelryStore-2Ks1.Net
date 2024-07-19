package com.ks1dotnet.jewelrystore.Enum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PageTemplate {
        LOGIN("login", "Login", Role.ADMIN, Role.MANAGER, Role.STAFF), DASHBOARD("dashboard",
                        "DashboardAdmin", Role.ADMIN,
                        Role.MANAGER), COUNTER_MANAGER("counterManager", "CounterManager",
                                        Role.ADMIN, Role.MANAGER), COUNTER("counter",
                                                        "CounterStaff", Role.ADMIN, Role.MANAGER,
                                                        Role.STAFF), GOLD_PRICE_PAGE("goldPrice",
                                                                        "goldPrice", Role.UNKNOW,
                                                                        Role.ADMIN, Role.MANAGER,
                                                                        Role.STAFF), IMPORT_INVOICE(
                                                                                        "importInvoice",
                                                                                        "ImportInvoice",
                                                                                        Role.ADMIN,
                                                                                        Role.MANAGER,
                                                                                        Role.STAFF), INVOICE_DEFAULT(
                                                                                                        "defaultInvoice",
                                                                                                        "InvoiceDefault",
                                                                                                        Role.ADMIN,
                                                                                                        Role.MANAGER,
                                                                                                        Role.STAFF), INVOICE_RESALE(
                                                                                                                        "resaleInvoice",
                                                                                                                        "ResaleInvoice",
                                                                                                                        Role.ADMIN,
                                                                                                                        Role.MANAGER,
                                                                                                                        Role.STAFF), INVOICE_VIEW(
                                                                                                                                        "viewInvoice",
                                                                                                                                        "ViewInvoice",
                                                                                                                                        Role.ADMIN,
                                                                                                                                        Role.MANAGER,
                                                                                                                                        Role.STAFF), EMPLOYEE_MANAGER(
                                                                                                                                                        "employeeManager",
                                                                                                                                                        "ManagerEmployee",
                                                                                                                                                        Role.ADMIN,
                                                                                                                                                        Role.MANAGER), CUSTOMER_MANAGER(
                                                                                                                                                                        "customerManager",
                                                                                                                                                                        "ManangerUser",
                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                        Role.MANAGER), POLICY_EMPLOYEE(
                                                                                                                                                                                        "policy",
                                                                                                                                                                                        "PolicyEmp",
                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                        Role.STAFF), POLICY_BUYBACK(
                                                                                                                                                                                                        "policyBuyBack",
                                                                                                                                                                                                        "PolicyEmpBuyBack",
                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                                        Role.STAFF), PRODUCT_MANAGER(
                                                                                                                                                                                                                        "productManager",
                                                                                                                                                                                                                        "product",
                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                        Role.MANAGER), PRODUCT(
                                                                                                                                                                                                                                        "product",
                                                                                                                                                                                                                                        "ProductStaff",
                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                                                                        Role.STAFF), POLICY_MANAGER(
                                                                                                                                                                                                                                                        "policyManager",
                                                                                                                                                                                                                                                        "Promotion",
                                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                                        Role.MANAGER), SHCEDULE_MANAGER(
                                                                                                                                                                                                                                                                        "scheduleManager",
                                                                                                                                                                                                                                                                        "Sche",
                                                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                                                        Role.MANAGER), SCHEDULE(
                                                                                                                                                                                                                                                                                        "schedule",
                                                                                                                                                                                                                                                                                        "ScheStaff",
                                                                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                                                                                                                        Role.STAFF), STAFF_SCREEN(
                                                                                                                                                                                                                                                                                                        "home",
                                                                                                                                                                                                                                                                                                        "StaffScreen",
                                                                                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                                                                                                                                        Role.STAFF), NOT_FOUND(
                                                                                                                                                                                                                                                                                                                        "404",
                                                                                                                                                                                                                                                                                                                        "404",
                                                                                                                                                                                                                                                                                                                        Role.UNKNOW,
                                                                                                                                                                                                                                                                                                                        Role.ADMIN,
                                                                                                                                                                                                                                                                                                                        Role.MANAGER,
                                                                                                                                                                                                                                                                                                                        Role.STAFF);

        private final String pageName;
        private final String templateFile;
        private final List<Role> roles;

        PageTemplate(String pageName, String templateFile, Role... roles) {
                this.pageName = pageName;
                this.templateFile = templateFile;
                this.roles = Arrays.asList(roles);
        }

        public String getPageName() {
                return pageName;
        }

        public String getTemplateFile() {
                return templateFile;
        }

        public List<Role> getRoles() {
                return roles;
        }

        private static final Map<String, PageTemplate> PAGE_MAP = new HashMap<>();

        static {
                for (PageTemplate pageTemplate : values()) {
                        PAGE_MAP.put(pageTemplate.getPageName(), pageTemplate);
                }
        }

        public static String getTemplate(String pageName, String roleStr) {
                Role role = Role.fromString(roleStr);
                return PAGE_MAP.values().stream()
                                .filter(pageTemplate -> pageTemplate.getPageName().equals(pageName)
                                                && pageTemplate.getRoles().contains(role))
                                .findFirst().orElse(NOT_FOUND).getTemplateFile();
        }

        public static String getTemplate(String pageName) {
                return PAGE_MAP.getOrDefault(pageName, NOT_FOUND).getTemplateFile();
        }

        public static List<Role> getRoles(String pageName) {
                return PAGE_MAP.getOrDefault(pageName, NOT_FOUND).getRoles();
        }
}
