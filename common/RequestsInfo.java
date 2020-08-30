package com.epam.parking.common;

public class RequestsInfo {

    public static final String DRIVERS = "/drivers";
    public static final String VEHICLES = "/vehicles";
    public static final String VEHICLES_BRANDS = "/vehiclesBrands";
    public static final String FULL = "/full";
    public static final String OFFICES = "/offices";
    public static final String LOCATIONS = "/locations";
    public static final String APPLICATIONS = "/applications";
    public static final String PERMISSIONS = "/permissions";
    public static final String PERMIT_URDATES = "/permitUpdates";
    public static final String PERMIT_TYPE = "/permitType";
    public static final String AUTHORITIES = "/authorities";
    public static final String TOKEN = "/token";
    public static final String FILTERED_DRIVERS = "/filteredDrivers";
    public static final String STATISTICS = "/statistics";
    public static final String ALL = "/all";
    public static final String SORTED_DRIVERS = "/sorted";
    public static final String FILTERED_APPLICATIONS = "/filteredApplications";
    public static final String SPOTS = "/spots";
    public static final String CHECK_EMAIL = "/checkEmail";

    public final static String CAPACITY = "/capacity";
    public final static String TITLE = "/title";
    public final static String VEHICLES_MODELS = "/vehiclesModels";
    public final static String APPLICATION_TYPE = "/type";
    public final static String NEW_APPLICATIONS_COUNT = "/newApplicationsCount";

    public static final String ID = "/{id}";
    public static final String MODEL_ID = "/{modelId}";
    public static final String OFFICE_ID = "/{officeId}";
    public static final String LOCATION_ID = "/{locationId}";
    public static final String SPOT_ID = "/{spotId}";
    public static final String MANAGER_ID = "/{managerId}";

    public static final String DEFAULT_OFFSET = "0";
    public static final String DEFAULT_LIMIT = "20";
    public static final String DEFAULT_SEARCH_VALUE = "";

    private RequestsInfo() {
    }
}
