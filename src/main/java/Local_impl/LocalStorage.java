package Local_impl;

import Exceptions.*;
import Exceptions.FileNotFoundException;
import OperationsAndExtensions.Extensions;
import Storage.StorageAndCfg.Cfg;
import Storage.StorageAndCfg.StorageManager;
import Storage.StorageAndCfg.StorageSpec;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalStorage extends StorageSpec {
    static{
        StorageManager.registerStorage(new LocalStorage());
    }


    public LocalStorage() {
    }


    @Override
    public void saveConfig(String path) {
        Cfg.getInstance().loadCfg(path);
        super.updateCfg(Cfg.getInstance());
    }

    @Override
    public void loadConfig(String s, String s1, String... strings) {
        Cfg.getInstance().setFileSize(Integer.parseInt(s));
        Cfg.getInstance().setMaxNumberOfFiles(Integer.parseInt(s1));
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(strings));
        Cfg.getInstance().setForbidenExtensionList(list);
        super.updateCfg(Cfg.getInstance());
    }

    @Override
    public void createStorage(String s) throws FolderNotFoundException{
        File fileToCreate = new File(s);
        File[] filesInFile = fileToCreate.listFiles();
        boolean exists = false;
        if(filesInFile != null) {
            for (File f : filesInFile) {
                if (f.getName().equalsIgnoreCase("Config.txt")) {
                    exists = true;
                }
            }
        }
        if(!exists){
            File folder = new File(s);
            if(!folder.exists()){
                folder.mkdir();
            }
            File downloadFolder = new File("C:\\DownloadFolderSK");
            if(!downloadFolder.exists()){
                downloadFolder.mkdirs();
            }
            super.setPathToStorage(s);
            System.out.println("Napravljen storage");
            System.out.println(super.getPathToStorage());
        }else{
            throw new FolderNotFoundException();
        }
    }

    @Override
    public void createStorage(String s, String s1) throws FileNotFoundException {
        File fileToCreate = new File(s);
        File[] filesInFile = fileToCreate.listFiles();
        boolean exists = false;
        if(filesInFile!= null) {
            for (File f : filesInFile) {
                if (f.getName().equalsIgnoreCase("Config.txt")) {
                    exists = true;
                }
            }
        }
        if(!exists){
            File folder = new File(s);
            if(!folder.exists()){
                folder.mkdir();
            }
            File downloadFolder = new File("C:\\DownloadFolderSK");
            if(!downloadFolder.exists()){
                downloadFolder.mkdirs();
            }
            super.setPathToStorage(s);
            File cfg = new File(s1);
            if(cfg.exists() && cfg.isFile()) {
                Cfg.getInstance().loadCfg(s1);
                super.updateCfg(Cfg.getInstance());
            }else{
                throw new FileNotFoundException();
            }
            System.out.println("Napravljen storage sa custom cfgom");
        }
    }

    @Override
    public void createDirectories(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {
        String fullFolderPath;
        if(!s.isEmpty()){
            fullFolderPath = super.getPathToStorage() + "/" + s;
        }else{
            fullFolderPath = super.getPathToStorage();
        }
        File directory = new File(fullFolderPath);
        if(!directory.isDirectory()){
            throw new FolderNotFoundException();
        }
        String[] files = directory.list();
        List<String> listOfFiles = new ArrayList<>();
        if(files != null) {
            listOfFiles.addAll(Arrays.asList(files));
        }

        for(String path : strings) {
            if(!listOfFiles.contains(path)) {
                new File(fullFolderPath+"/"+path).mkdirs();
            }else{
                //treba dodati exception da fajl sa tim nazivom vec postoji ali i ne mora ako je copy and replace
            }
        }
    }

    @Override
    public void createDirectoriesFormated(String s, String... strings) throws FolderNotFoundException, UnsupportedOperationException {

    }

    @Override
    public void loadFiles(String s,String s1,String... strings) throws FileNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {
        String fullPath;
        if(!s.isEmpty()) {
            fullPath = super.getPathToStorage() + "/" + s;
        }else{
            fullPath = super.getPathToStorage();
        }
        File file= new File(fullPath);
        if(!file.exists()){
            System.out.println("TU SMO1");
            System.out.println(fullPath);
            throw new FileNotFoundException();
        }
        File file1 = new File(s1);
        if(!file1.exists()){
            System.out.println("TU SMO");
            throw new FileNotFoundException();
        }
        long size = 0;
        for(String filePath : strings){
            File checker = new File(s1+filePath);
            if(!checker.exists()){
                throw new FileNotFoundException();
            }
            if(checker.isDirectory()){
                size += folderSize(checker);
            }else{
                size += checker.length();
            }
        }
        int numberOfFiles = 0;
        if(file.listFiles() != null) {
            numberOfFiles = file.listFiles().length;
        }
        if(strings.length + numberOfFiles > super.getCfg().getMaxNumberOfFiles()){// da li se number of files racuna za ceo storage ili pojedinacno za foldere
            throw new MaxNumberOfFilesExceededException();
        }
        if( size + new File(super.getPathToStorage()).length() > super.getCfg().getFileSize()){ //da li se size racuna za ceo storage ili svaki folder pojedinacno
            throw new MaxStorageSizeException();
        }

        for(String filePath : strings){
            copyFolder(new File(s1+"/"+filePath), new File(fullPath + "/" + filePath));
        }
    }
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    public static void copyFolder(File source, File destination)
    {
        if (source.isDirectory())
        {
            if (!destination.exists())
            {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files)
            {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = null;
            OutputStream out = null;

            try
            {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }
    @Override
    public void deleteFiles(String... strings) throws FileNotFoundException, UnsupportedOperationException {
        for(String path : strings) {
            String fullPath = super.getPathToStorage() + "/" + path;
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
            String fullPath = super.getPathToStorage() + "/" + path;
            File folderToDel = new File(fullPath);
            if (!folderToDel.exists() && !folderToDel.isDirectory()) {
                throw new FolderNotFoundException();
            }
            deleteDir(folderToDel);
        }
    }
    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    @Override
    public void moveFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, MaxStorageSizeException, MaxNumberOfFilesExceededException, UnsupportedOperationException {
        String fullPath = super.getPathToStorage() + "/" + s;
        File fileToMove = new File(fullPath);
        if(!fileToMove.exists()){
            throw new FileNotFoundException();
        }
        File folderPath = new File(super.getPathToStorage() + "/" + s1);
        if(!folderPath.exists() || !folderPath.isDirectory()){
            throw new FolderNotFoundException();
        }
        Path pathToFile = Paths.get(fileToMove.getPath());
        try {
            long fileSize = Files.size(pathToFile);
            int numberOfFiles = 0;
            if(fileSize + folderSize(folderPath) > super.getCfg().getFileSize()){
                throw new MaxStorageSizeException();
            }
            if(folderPath.listFiles()!= null){
                numberOfFiles = folderPath.listFiles().length;
            }
            if(numberOfFiles + 1 > super.getCfg().getFileSize()){
                throw new MaxStorageSizeException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String [] arrayForFileName = s.split("\\\\");
            Files.move(pathToFile, Paths.get(folderPath.getPath() +"/" + arrayForFileName[arrayForFileName.length-1]), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downloadFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, UnsupportedOperationException {
        String fullPath = super.getPathToStorage() + "/" + s;
        File fileToDownload = new File(fullPath);
        if(!fileToDownload.exists()){
            throw new FileNotFoundException();
        }
        if(s1.isEmpty()){
            s1 = "C:\\DownloadFolderSK";
        }
        File folderToDownloadInto = new File(s1);
        if(!folderToDownloadInto.exists() || !folderToDownloadInto.isDirectory()){
            throw new FolderNotFoundException();
        }
        try {
            String [] arrayForFileName = s.split("/");
            System.out.println(folderToDownloadInto.getPath()+ "/" + arrayForFileName[arrayForFileName.length-1]);
            System.out.println(arrayForFileName[arrayForFileName.length-1]);
            Files.move(Paths.get(fileToDownload.getPath()), Paths.get(folderToDownloadInto.getPath()+ "/" + arrayForFileName[arrayForFileName.length-1]),StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renameFile(String s, String s1) throws FileNotFoundException, FolderNotFoundException, InvalidNameException, UnsupportedOperationException {
        String fullPath = super.getPathToStorage() + "/" + s;
        File fileToRename = new File(fullPath);
        if(!fileToRename.exists()){
            throw new FileNotFoundException();
        }

        File renamedFile = new File(super.getPathToStorage() + "/" + s1);

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
