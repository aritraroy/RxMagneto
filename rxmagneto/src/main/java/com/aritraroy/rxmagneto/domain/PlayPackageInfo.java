package com.aritraroy.rxmagneto.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.annotations.Nullable;

/**
 * Created by aritraroy on 16/08/17.
 *
 * Holds information about a particular Play Store package
 */
public class PlayPackageInfo {
    private final String packageName;
    private final String packageUrl;
    @Nullable
    private Boolean isUrlValid;
    @Nullable
    private String packageVersion;
    @Nullable
    private String downloads;
    @Nullable
    private String publishedDate;
    @Nullable
    private String osRequirements;
    @Nullable
    private String contentRating;
    @Nullable
    private String appRating;
    @Nullable
    private String appRatingCount;
    @Nullable
    private List<String> changelogArray;

    public PlayPackageInfo(String packageName, String packageUrl) {
        this.packageName = packageName;
        this.packageUrl = packageUrl;
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

    public void setUrlValid(Boolean urlValid) {
        isUrlValid = urlValid;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getOsRequirements() {
        return osRequirements;
    }

    public void setOsRequirements(String osRequirements) {
        this.osRequirements = osRequirements;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public String getAppRating() {
        return appRating;
    }

    public void setAppRating(String appRating) {
        this.appRating = appRating;
    }

    public String getAppRatingCount() {
        return appRatingCount;
    }

    public void setAppRatingCount(String appRatingCount) {
        this.appRatingCount = appRatingCount;
    }

    public List<String> getChangelogArray() {
        return changelogArray;
    }

    public void setChangelogArray(List<String> changelogArray) {
        this.changelogArray = changelogArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayPackageInfo that = (PlayPackageInfo) o;

        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null)
            return false;
        if (packageUrl != null ? !packageUrl.equals(that.packageUrl) : that.packageUrl != null)
            return false;
        if (isUrlValid != null ? !isUrlValid.equals(that.isUrlValid) : that.isUrlValid != null)
            return false;
        if (packageVersion != null ? !packageVersion.equals(that.packageVersion) : that.packageVersion != null)
            return false;
        return downloads != null ? downloads.equals(that.downloads) : that.downloads == null;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (packageUrl != null ? packageUrl.hashCode() : 0);
        result = 31 * result + (isUrlValid != null ? isUrlValid.hashCode() : 0);
        result = 31 * result + (packageVersion != null ? packageVersion.hashCode() : 0);
        result = 31 * result + (downloads != null ? downloads.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlayPackageInfo{" +
                "packageName='" + packageName + '\'' +
                ", packageUrl='" + packageUrl + '\'' +
                ", isUrlValid=" + isUrlValid +
                ", packageVersion='" + packageVersion + '\'' +
                ", downloads='" + downloads + '\'' +
                '}';
    }
}
