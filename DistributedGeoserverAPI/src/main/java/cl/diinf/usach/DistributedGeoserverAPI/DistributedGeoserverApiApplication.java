package cl.diinf.usach.DistributedGeoserverAPI;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class DistributedGeoserverApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(DistributedGeoserverApiApplication.class, args);
	}

}
