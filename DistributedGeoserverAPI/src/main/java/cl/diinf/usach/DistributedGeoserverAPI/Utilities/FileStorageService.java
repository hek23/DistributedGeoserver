package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import com.google.gson.JsonElement;
import spark.resource.ExternalResource;
import spark.resource.Resource;
import spark.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileStorageService {

    private final Path fileStorageLocation;


    public Path getFileStorageLocation() {
        return this.fileStorageLocation;
    }

        public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        System.out.println("PATH");
        System.out.println(this.fileStorageLocation);

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            System.out.println("Fail Autowired");
            //throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);

        }
    }

    public String storeFile(File file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getAbsolutePath());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                //throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
                System.out.println("storeFile fail");
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(new FileInputStream(file), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            //throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
            System.out.println("storefile exception fail");
        }

        return fileName;
    }

    public Resource loadFileAsResource(String fileName) {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new ExternalResource(filePath.toUri().toString());
        //filePath.toUri());
        if(resource.exists()) {
            return resource;
        } else {
            //throw new MyFileNotFoundException("File not found " + fileName);
            System.out.println("load as resource fail");
        }
        return null;
    }
}
