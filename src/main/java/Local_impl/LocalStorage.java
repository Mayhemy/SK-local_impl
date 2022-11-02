package Local_impl;

import Exceptions.*;
import OperationsAndExtensions.Extensions;
import Storage.StorageAndCfg.Cfg;
import Storage.StorageAndCfg.StorageSpec;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class LocalStorage extends StorageSpec {

    private LocalStorageDirectory localStorageDirectory;

    @Override
    public void saveConfig(Cfg cfg) {

    }

    @Override
    public void createStorage(String s) throws FolderNotFoundException{

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
        //Takes path to folder within storage and returns a list of files within
        //Comment: Maybe we should return a Collection of Files and not Strings since the request was:
        // vratiti sve fajlove u zadatom direktorijumu (vraća se naziv i metapodaci
        File folder = new File(s);
        String[] files = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return !(new File(current, name).isDirectory());
            }
        });
        return Arrays.asList(files);
    }

    @Override
    public Collection<String> retSubdirFiles(String s) throws FolderNotFoundException {
        //Takes path to folder within storage and returns a list of subfolders within
        File folder = new File(s);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        File[] subfolders = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Collection<String> filePaths = new ArrayList<>();
        for(File f:subfolders){
            getSubdirFiles(f,filePaths);
        }
        return filePaths;
    }

    @Override
    public Collection<String> retDirFilesAndSubdirFiles(String s) throws FolderNotFoundException {
        File folder = new File(s);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        Collection<String> filePaths = new ArrayList<>();
        getSubdirFiles(folder,filePaths);
        return filePaths;
    }

    private void getSubdirFiles(File dir,Collection<String> filePaths){
        if(dir.isDirectory()){
            for(File f:dir.listFiles()){
                getSubdirFiles(f,filePaths);
            }
        }else{
            filePaths.add(dir.getAbsolutePath());
        }
    }

    @Override
    public Collection<String> retFilesWithExtension(String dirPath, final Extensions extensions) throws ForbidenExtensionException,FolderNotFoundException {
        File folder = new File(dirPath);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        Collection<String> filePaths;
        String[] fileNames = folder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("."+extensions); // <-- IDK if this works
            }
        });
        filePaths = Arrays.asList(fileNames);
        return filePaths;
    }

    @Override
    public Collection<String> containsString(String dirPath, final String substring) throws FolderNotFoundException{
        File folder = new File(dirPath);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        Collection<String> filePaths;
        String[] fileNames = folder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains(substring);
            }
        });
        filePaths = Arrays.asList(fileNames);
        return filePaths;
    }

    @Override
    public boolean containsFiles(String dirPath,String... fileNames) throws FolderNotFoundException{
        File folder = new File(dirPath);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        String[] fileNamesRaw = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return !(new File(current, name).isDirectory());
            }
        });
        for(String name : fileNamesRaw){
            for(String fileName : fileNames){
                if(name.equals(fileName))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String parentFolderPath(String filePath) throws FileNotFoundException {
        String parentFolderPath;
        File file = new File(filePath);
        parentFolderPath = file.getParent();
        return parentFolderPath;
    }

    @Override
    public Collection<String> selectByDateCreated(String dirPath,String startDate,String endDate) throws FolderNotFoundException{
        Date dateStart,dateEnd;
        dateStart = Date.valueOf(startDate);
        dateEnd = Date.valueOf(endDate);
        FileTime timeStart,timeEnd;
        timeStart = FileTime.from(dateStart.getTime(), TimeUnit.MILLISECONDS);
        timeEnd = FileTime.from(dateEnd.getTime(), TimeUnit.MILLISECONDS);
        File folder = new File(dirPath);
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        Collection<String> filePaths = new ArrayList<>();
        File[] filesRaw = folder.listFiles();

        for(File f:filesRaw){
            try {
                Path path = Paths.get(f.getPath());
                FileTime creationTime = (FileTime) Files.getAttribute(path, "creationTime");
                if(creationTime.compareTo(timeStart) > 0 && creationTime.compareTo(timeEnd) < 0){
                    filePaths.add(f.getPath());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePaths;
    }
    public Collection<String> selectByDateModified(String dirPath,String startDate,String endDate)throws FolderNotFoundException{
        File folder = new File(dirPath);
        long longStart = Date.valueOf(startDate).getTime();
        long longEnd = Date.valueOf(endDate).getTime();
        if(!folder.isDirectory()){
            throw new FolderNotFoundException();
        }
        Collection<String> filePaths = new ArrayList<>();
        File[] filesRaw = folder.listFiles();
        long longLastModified;
        for(File f:filesRaw){
            longLastModified = f.lastModified();
            if(longLastModified>longStart && longLastModified<longEnd){
                filePaths.add(f.getPath());
            }
        }
        return filePaths;
    }
}
