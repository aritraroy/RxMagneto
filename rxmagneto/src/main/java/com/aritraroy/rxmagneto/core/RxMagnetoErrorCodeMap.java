package com.aritraroy.rxmagneto.core;

/**
 * A map containing all error codes returned by RxMagneto
 */
public enum RxMagnetoErrorCodeMap {
    ERROR_GENERIC(100),
    ERROR_URL(101),
    ERROR_VERIFIED_ERROR(102),
    ERROR_VERSION(103),
    ERROR_UPDATE(104),
    ERROR_DOWNLOADS(105),
    ERROR_PUBLISHED_DATE(106),
    ERROR_OS_REQUIREMENTS(107),
    ERROR_CONTENT_RATING(108),
    ERROR_APP_RATING(109),
    ERROR_APP_RATING_COUNT(110),
    ERROR_CHANGELOG(111);

    private int errorCode;

    RxMagnetoErrorCodeMap(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
