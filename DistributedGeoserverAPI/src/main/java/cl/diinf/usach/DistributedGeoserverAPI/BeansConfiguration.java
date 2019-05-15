package cl.diinf.usach.DistributedGeoserverAPI;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeansConfiguration {

    @Bean("GEOSERVER_USER")
    //@Scope("SCOPE_SINGLETON")
    public UsernamePasswordCredentials loginGeoserver(@Value("${geoserver.user}") String user, @Value("${geoserver.pass}") String pass) {
        return new UsernamePasswordCredentials(user, pass);

    }

}
