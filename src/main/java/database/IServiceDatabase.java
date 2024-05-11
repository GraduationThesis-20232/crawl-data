package database;

import java.util.ArrayList;
import java.util.Map;

public interface IServiceDatabase<T> {
    public Map<String, T> getListData(String collection);

    public T getAData(String id, String collection);

    public void save(T t, String collection);

    public void update(T t, String collection);

    public void delete(T t, String collection);

}
