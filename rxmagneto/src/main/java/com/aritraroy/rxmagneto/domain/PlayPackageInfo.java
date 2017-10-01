package com.aritraroy.rxmagneto.domain;

import java.util.List;

/**
 * Container to hold data of a particular package available on Play Store
 */
public class PlayPackageInfo {

    private final String packageName;
    private final String packageUrl;
    private final boolean isUrlValid;
    private final String packageVersion;
    private final String downloads;
    private final String publishedDate;
    private final String osRequirements;
    private final String contentRating;
    private final String appRating;
    private final String appRatingCount;
    private final List<String> changelogArray;

    private PlayPackageInfo(Builder builder) {
        packageName = builder.packageName;
        packageUrl = builder.packageUrl;
        isUrlValid = builder.isUrlValid;
        packageVersion = builder.packageVersion;
        downloads = builder.downloads;
        publishedDate = builder.publishedDate;
        osRequirements = builder.osRequirements;
        contentRating = builder.contentRating;
        appRating = builder.appRating;
        appRatingCount = builder.appRatingCount;
        changelogArray = builder.changelogArray;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public Boolean isUrlValid() {
        return isUrlValid;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public String getDownloads() {
        return downloads;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getOsRequirements() {
        return osRequirements;
    }

    public String getContentRating() {
        return contentRating;
    }

    public String getAppRating() {
        return appRating;
    }

    public String getAppRatingCount() {
        return appRatingCount;
    }

    public List<String> getChangelogArray() {
        return changelogArray;
    }

    public static class Builder {

        private String packageName;
        private String packageUrl;
        private Boolean isUrlValid;
        private String packageVersion;
        private String downloads;
        private String publishedDate;
        private String osRequirements;
        private String contentRating;
        private String appRating;
        private String appRatingCount;
        private List<String> changelogArray;

        public Builder(String packageName, String packageUrl) {
            this.packageName = packageName;
            this.packageUrl = packageUrl;
        }

        public Builder setIsUrlValid(boolean isUrlValid) {
            this.isUrlValid = isUrlValid;
            return this;
        }

        public Builder setPackageVersion(String packageVersion) {
            this.packageVersion = packageVersion;
            return this;
        }

        public Builder setDownloads(String downloads) {
            this.downloads = downloads;
            return this;
        }

        public Builder setPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder setOsRequirements(String osRequirements) {
            this.osRequirements = osRequirements;
            return this;
        }

        public Builder setContentRating(String contentRating) {
            this.contentRating = contentRating;
            return this;
        }

        public Builder setAppRating(String appRating) {
            this.appRating = appRating;
            return this;
        }

        public Builder setAppRatingCount(String appRatingCount) {
            this.appRatingCount = appRatingCount;
            return this;
        }

        public Builder setChangelogArray(List<String> changelogArray) {
            this.changelogArray = changelogArray;
            return this;
        }

        public PlayPackageInfo build() {
            return new PlayPackageInfo(this);
        }
    }
}
