package models.dataset;

import exceptions.InvalidDatasetException;
import exceptions.InvalidRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Generic representation of data. Equivalent to a table in SQL.
 */
public class Dataset {

    private List<Record> records; //Rows
    private List<String> fieldNames; //Column names

    public Dataset(List<String> fieldNames) {
        records = new ArrayList<>();
        this.fieldNames = fieldNames;
    }

    public void addRecord(Record record) throws InvalidDatasetException, InvalidRequest {

        if (record == null) {
            throw new InvalidDatasetException("Error while adding a record in the dataset. Record is null");
        }

        //Check number of fields
        if (record.numFields() != fieldNames.size()) {
            throw new InvalidDatasetException("Error while adding a record in the dataset. Invalid number of fields in a record");
        }

        //Check field names
        for(String fieldName: fieldNames) {
           if (record.getField(fieldName) == null) {
               throw new InvalidDatasetException("Error while adding a record in the dataset. Invalid fields in a record");
           }
        }

        records.add(record);
    }

    public List<Record> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public List<String> getFieldNames() {
        return Collections.unmodifiableList(fieldNames);
    }

    public Dataset filter(String query) {
        //TODO implement
        return null;
    }

    public Dataset select(String[] fields) {
        //TODO implement
        return null;
    }

    public Dataset orderBy(String field, boolean asc) {
        //TODO implement
        return null;
    }

    /**
     * Group by number of fields and return grouped dataset
     * @param groupByFields
     * @return
     * @throws Exception
     */
    public GroupedDataset groupBy(String[] groupByFields) throws InvalidRequest, InvalidDatasetException {

        //Check if all group by fields exist in the dataset
        for (String groupByField: groupByFields) {
            groupByField = groupByField.trim().toLowerCase();
            if (!fieldNames.contains(groupByField)) {
                throw new InvalidRequest("Invalid group by field " +groupByField);
            }
        }

        GroupedDataset groupedDataset = new GroupedDataset(groupByFields, fieldNames);
        for (Record record: records) {
            groupedDataset.addRecord(record);
        }
        return groupedDataset;
    }
}
