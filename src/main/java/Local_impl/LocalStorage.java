package Local_impl;

import Exceptions.*;
import OperationsAndExtensions.Extensions;
import Storage.StorageAndCfg.Cfg;
import Storage.StorageAndCfg.StorageSpec;

import java.util.Collection;

public class LocalStorage extends StorageSpec {

    @Override
    public void saveConfig(Cfg cfg) {

    }

    @Override
    public void createStorage(String s) {

    }

    @Override
    public void createStorage(String s, String s1) throws FileNotFoundException {

    }

    @Override
    public void createDirectories(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void createDirectoriesFormated(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void loadFiles(String s) throws FileNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {

    }

    @Override
    public void deleteFiles(String... strings) throws FileNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void deleteDirectories(String... strings) throws FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void moveFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {

    }

    @Override
    public void downloadFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void renameFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, InvalidNameException, UnsupportedOperationException {

    }

    @Override
    public Collection<String> retFiles(String s) throws FolderNotFoundException {
        return null;
    }

    @Override
    public Collection<String> retSubdirFiles(String s) throws FolderNotFoundException {
        return null;
    }

    @Override
    public Collection<String> retDirFilesAndSubdirFiles(String s) throws FolderNotFoundException {
        return null;
    }

    @Override
    public Collection<String> retFilesWithExtension(Extensions extensions) throws ForbidenExtensionException {
        return null;
    }

    @Override
    public Collection<String> containsString(String s) {
        return null;
    }

    @Override
    public boolean containsFiles(String... strings) {
        return false;
    }

    @Override
    public String parentFolderName(String s) throws FileNotFoundException {
        return null;
    }

    @Override
    public Collection<String> selectByDate(String s, String s1) {
        return null;
    }
}
