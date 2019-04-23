package cl.diinf.usach.DistributedGeoserverAPI;

import cl.diinf.usach.Model.Utility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DistributedGeoserverApiApplication {

	public static void main(String[] args) {
		/*for (String name : Utility.getFileName("../../../SHPFiles/")){
			Utility.unzipFile(name,"../../../SHPFiles/");
		}*/

		SpringApplication.run(DistributedGeoserverApiApplication.class, args);
	}

}
