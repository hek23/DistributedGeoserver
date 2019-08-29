package cl.diinf.usach.DistributedGeoserverAPI.Model;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    //Optional<T> get(long id);
    //T get();

    List<T> getAll();

    //Return HTTP State
    int create(String body);

    void update(T t, String[] params);

    void delete(T t);

    //T getByName(String name);
}