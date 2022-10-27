package Local_impl;

import OperationsAndExtensions.Extensions;
import Storage.StorageAndCfg.Cfg;

import java.util.List;

public class LocalStorageDirectory {
    private String root;
    private long maxByteSize;
    private int maxNumberOfFiles;
    private List<Extensions> forbidenExtensionList;
    private Cfg config;
    private String downloadFolder;
    private boolean fileSizeSet;
    private boolean maxNumberOfFilesSet;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public long getMaxByteSize() {
        return maxByteSize;
    }

    public void setMaxByteSize(long maxByteSize) {
        this.maxByteSize = maxByteSize;
    }

    public int getMaxNumberOfFiles() {
        return maxNumberOfFiles;
    }

    public void setMaxNumberOfFiles(int maxNumberOfFiles) {
        this.maxNumberOfFiles = maxNumberOfFiles;
    }

    public List<Extensions> getForbidenExtensionList() {
        return forbidenExtensionList;
    }

    public void setForbidenExtensionList(List<Extensions> forbidenExtensionList) {
        this.forbidenExtensionList = forbidenExtensionList;
    }

    public Cfg getConfig() {
        return config;
    }

    public void setConfig(Cfg config) {
        this.config = config;
    }

    public String getDownloadFolder() {
        return downloadFolder;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder = downloadFolder;
    }

    public boolean isFileSizeSet() {
        return fileSizeSet;
    }

    public void setFileSizeSet(boolean fileSizeSet) {
        this.fileSizeSet = fileSizeSet;
    }

    public boolean isMaxNumberOfFilesSet() {
        return maxNumberOfFilesSet;
    }

    public void setMaxNumberOfFilesSet(boolean maxNumberOfFilesSet) {
        this.maxNumberOfFilesSet = maxNumberOfFilesSet;
    }
}
