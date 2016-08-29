package models.dataset;

import exceptions.InvalidRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Generic representation of single data record. Equivalent to a row of table in SQL.
 */
public class Record {

    private Map<String, String> data; //Map of <field_name>:<value> e.g <last_name>:<benkar>

    public Record() {
        data = new HashMap<>();
    }

    public void addField(String key, String value) {
        data.put(key, value); //Will replace if key already present
    }

    public int numFields() {
        return data.size();
    }

    public String getField(String fieldName) throws InvalidRequest {
        if (!data.containsKey(fieldName)) {
            throw new InvalidRequest("Error while getting a field from record. Field does not exist:" + fieldName);
        }
        return data.get(fieldName);
    }
}
