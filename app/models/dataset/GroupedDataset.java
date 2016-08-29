package models.dataset;

import exceptions.InvalidDatasetException;
import exceptions.InvalidRequest;
import models.transform.aggregate.AggregateFunction;

import java.util.*;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Generic representation of a dataset which is grouped by number of fields.
 * Has a form of field_value1#field_value2..: list of records with those field values
 */
public class GroupedDataset {

    public static final String KEY_DELIM = "#";
    private Map<String, Dataset> keyDatasetMap; //Key of type fieldValue1#fieldValue2#... e.g rohan#benkar, value is list of records
    private String[] groupByFields; //Fields that are used to group by e.g last_name
    private List<String> fieldNames; //column names of records inside dataset

    public GroupedDataset(String[] groupByFields, List<String> fieldNames) {
        keyDatasetMap = new HashMap<>();
        this.groupByFields = groupByFields;
        this.fieldNames = fieldNames;
    }

    /**
     * Creates a token of form field_value1#field_value2#..
     * @param record - Record to process
     * @return token
     * @throws Exception
     */
    private String getGroupByKeyToken(Record record) throws InvalidRequest {
        String delim = "";
        StringBuilder groupByFieldKeySB = new StringBuilder();
        for (String groupByField: groupByFields) {
            String groupByFieldValue = record.getField(groupByField); //fieldValues e.g rohan
            groupByFieldKeySB.append(delim).append(groupByFieldValue);
            delim = KEY_DELIM;
        }
        return groupByFieldKeySB.toString();
    }

    public void addRecord(Record record) throws InvalidRequest, InvalidDatasetException {
        //generate a key token of type groupByFieldValue1#groupByFieldValue2#groupByFieldValue3... e.g rohan#benkar
        String groupByFieldKey = getGroupByKeyToken(record);
        //if key token doesn't exist then create a new group dataset
        if (!keyDatasetMap.containsKey(groupByFieldKey)) {
            keyDatasetMap.put(groupByFieldKey, new Dataset(fieldNames));
        }
        //add record to the group dataset
        keyDatasetMap.get(groupByFieldKey).addRecord(record);
    }

    /**
     * Aggregates dataset and returns resultant dataset
     * @param fieldToAggregateOn - field within dataset on which aggregation is to be done e.g. age
     * @param fieldNameForAggregatedColumn - name to be given to field after aggregation e.g age_sum
     * @param aggregateFunction - e.g sum, count
     * @return Resultant dataset
     * @throws Exception
     */
    public Dataset aggregate(String fieldToAggregateOn, String fieldNameForAggregatedColumn, AggregateFunction aggregateFunction) throws InvalidRequest, InvalidDatasetException {

        if (aggregateFunction == null) {
            throw new InvalidRequest("Invalid aggregate function");
        }

        //generate fieldnames for the aggregated dataset
        List<String> fieldNamesForAggregatedDataset = new ArrayList<>();
        Collections.addAll(fieldNamesForAggregatedDataset, groupByFields); //copy group by fields first
        fieldNamesForAggregatedDataset.add(fieldNameForAggregatedColumn);

        //add records to the aggregated dataset using the aggregate function
        Dataset aggregatedDataset = new Dataset(fieldNamesForAggregatedDataset);
        for( Map.Entry<String, Dataset> entry: keyDatasetMap.entrySet()) {
            Record record = getAggregateRecord(fieldToAggregateOn, fieldNameForAggregatedColumn, aggregateFunction, entry);
            aggregatedDataset.addRecord(record);
        }
        return aggregatedDataset;
    }

    /**
     * Given a map entry returns aggregated record.
     * E.g given benkar: (rohan,benkar,28) (ron,benkar,24).. field age, function sum -> returns benkar: 52
     * @param fieldToAggregateOn - field within dataset on which aggregation is to be done e.g. age
     * @param fieldNameForAggregatedColumn - name to be given to field after aggregation e.g age_sum
     * @param aggregateFunction - e.g sum, count
     * @param entry
     * @return
     * @throws Exception
     */
    private Record getAggregateRecord(String fieldToAggregateOn, String fieldNameForAggregatedColumn, AggregateFunction aggregateFunction, Map.Entry<String, Dataset> entry) throws InvalidRequest {
        Record record = new Record();
        String[] groupByFieldValues = entry.getKey().split(KEY_DELIM); //de-tokenize key e.g rohan#benkar to [rohan, benkar]

        //Defensive check. Ideally this should never happen
        if (groupByFieldValues.length != groupByFields.length) {
            throw new InvalidRequest("Error while generating aggregated record");
        }

        //Add group by fields to the record
        for (int i=0; i<groupByFieldValues.length; i++) {
            String groupByFieldName = groupByFields[i]; //e.g first_name
            String groupByFieldValue = groupByFieldValues[i]; // e.g. rohan
            record.addField(groupByFieldName, groupByFieldValue);
        }

        //Add aggregate value
        String aggregatedValue = aggregateFunction.aggregate(entry.getValue(), fieldToAggregateOn);
        record.addField(fieldNameForAggregatedColumn, aggregatedValue);

        return record;
    }
}
