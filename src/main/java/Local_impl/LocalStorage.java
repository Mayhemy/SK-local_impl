package Local_impl;

import Exceptions.*;
import OperationsAndExtensions.Extensions;
import Storage.StorageAndCfg.Cfg;
import Storage.StorageAndCfg.StorageSpec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LocalStorage extends StorageSpec {

    private LocalStorageDirectory localStorageDirectory;
    private List<LocalStorageDirectory> storageList;

    public LocalStorage() {
        this.storageList = new ArrayList<>();
    }

    @Override
    public void saveConfig(Cfg cfg) {
        if(localStorageDirectory != null){
            localStorageDirectory.setConfig(cfg);
        }
    }

    @Override
    public void createStorage(String s) throws FolderNotFoundException{
        File fileToCreate = new File(s);
        File[] filesInFile = fileToCreate.listFiles();
        boolean exists = false;
        for(File f : filesInFile){
            if(f.getName().equalsIgnoreCase("Config.txt")){
                exists = true;
            }
        }
        if(!exists){
            localStorageDirectory = new LocalStorageDirectory(s);
            localStorageDirectory.setConfig(Cfg.getInstance());
            storageList.add(localStorageDirectory);
            System.out.println("Napravljen storage");
        }
    }

    @Override
    public void createStorage(String s, String s1) throws FileNotFoundException {
        File fileToCreate = new File(s);
        File[] filesInFile = fileToCreate.listFiles();
        boolean exists = false;
        for(File f : filesInFile){
            if(f.getName().equalsIgnoreCase("Config.txt")){
                exists = true;
            }
        }
        if(!exists){
            localStorageDirectory = new LocalStorageDirectory(s);
            if(s1.isEmpty()){
                throw new FileNotFoundException();
            }
            Cfg.getInstance().loadCfg(s1);
            localStorageDirectory.setConfig(Cfg.getInstance());
            storageList.add(localStorageDirectory);
            System.out.println("Napravljen storage sa custom cfgom");
        }
    }

    @Override
    public void createDirectories(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {
        String fullFolderPath = localStorageDirectory.getRoot() + "/" + s;
        File directory = new File(fullFolderPath);
        if(!directory.isDirectory()){
            throw new FolderNotFoundException();
        }
        String[] files = directory.list();
        List<String> listOfFiles = Arrays.asList(files);

        for(String path : strings) {
            if(!listOfFiles.contains(path)) {
                new File(fullFolderPath+"/"+path).mkdirs();
            }else{
                //treba dodati exception da fajl sa tim nazivom vec postoji
            }
        }
    }

    @Override
    public void createDirectoriesFormated(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void loadFiles(String s,String... strings) throws FileNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {
        String fullPath = localStorageDirectory.getRoot() + "/"+ s;
        File file= new File(fullPath);
        if(!file.exists()){
            throw new FileNotFoundException();
        }
        Path path = Paths.get(fullPath);
        long size = 0;
        try {
            int numberOfFiles = 0;
            size = Files.size(path);
            if(file.listFiles() != null) {
                numberOfFiles = file.listFiles().length;
            }
            if(strings.length + numberOfFiles > localStorageDirectory.getMaxNumberOfFiles()){// da li se number of files racuna za ceo storage ili pojedinacno za foldere
                throw new MaxNumberOfFilesExceededException();
            }
            if( size + getFolderSize(new File(localStorageDirectory.getRoot())) > localStorageDirectory.getMaxByteSize()){ //da li se size racuna za ceo storage ili svaki folder pojedinacno
                throw new MaxStorageSizeException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String filePath : strings){
            try {
                Files.copy(Paths.get(fullPath), Paths.get(localStorageDirectory.getRoot() + "/" + filePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();

        int count = files.length;

        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    @Override
    public void deleteFiles(String... strings) throws FileNotFoundException, UnsupportedOperationException {
        for(String path : strings) {
            String fullPath = localStorageDirectory.getRoot() + "/" + path;
            File fileToDel = new File(fullPath);
            if (!fileToDel.exists()) {
                throw new FileNotFoundException();
            }
            fileToDel.delete();
        }
    }

    @Override
    public void deleteDirectories(String... strings) throws FolderNotFoundException, UnsupportedOperationException {
        for(String path : strings){
            String fullPath = localStorageDirectory.getRoot() + "/" + path;
            File folderToDel = new File(fullPath);
            if (!folderToDel.exists() && !folderToDel.isDirectory()) {
                throw new FolderNotFoundException();
            }
            folderToDel.delete();
        }
    }

    @Override
    public void moveFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {
        String fullPath = localStorageDirectory.getRoot() + "/" + s;
        File fileToMove = new File(fullPath);
        if(!fileToMove.exists()){
            throw new FileNotFoundException();
        }
        File folderPath = new File(localStorageDirectory.getRoot() + "/" + s1);
        if(!folderPath.exists() || !folderPath.isDirectory()){
            throw new FolderNotFoundException();
        }
        Path pathToFile = Paths.get(fileToMove.getPath());
        try {
            long fileSize = Files.size(pathToFile);
            int numberOfFiles = 0;
            if(fileSize + getFolderSize(new File(localStorageDirectory.getRoot()))  > localStorageDirectory.getMaxByteSize()){
                throw new MaxStorageSizeException();
            }
            if(folderPath.listFiles()!= null){
                numberOfFiles = folderPath.listFiles().length;
            }
            if(numberOfFiles + 1 > localStorageDirectory.getMaxNumberOfFiles()){
                throw new MaxStorageSizeException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.move(pathToFile, Paths.get(folderPath.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, UnsupportedOperationException {
        String fullPath = localStorageDirectory.getRoot() + "/" + s;
        File fileToDownload = new File(fullPath);
        if(!fileToDownload.exists()){
            throw new FileNotFoundException();
        }
        File folderToDownloadInto = new File(localStorageDirectory.getRoot() + "/" + s1);
        if(!folderToDownloadInto.exists() || !folderToDownloadInto.isDirectory()){
            throw new FolderNotFoundException();
        }
        try {
            moveFile(fileToDownload.getPath(), folderToDownloadInto.getPath());
        } catch (MaxStorageSizeException e) {
            e.printStackTrace();
        } catch (MaxNumberOfFilesExceededException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renameFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, InvalidNameException, UnsupportedOperationException {
        String fullPath = localStorageDirectory.getRoot() + "/" + s;
        File fileToRename = new File(fullPath);
        if(!fileToRename.exists()){
            throw new FileNotFoundException();
        }

        File renamedFile = new File(localStorageDirectory.getRoot() + "/" + s1);

        if (renamedFile.exists())
            throw new InvalidNameException();

        // Rename file (or directory)
        boolean success = fileToRename.renameTo(renamedFile);

        if (!success) {
            System.out.println("Ime fajla nije promenjeno");
        }
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
