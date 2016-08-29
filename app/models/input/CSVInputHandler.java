package models.input;

import exceptions.InvalidDatasetException;
import exceptions.InvalidRequest;
import models.dataset.Dataset;
import models.dataset.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Handles CSV in text format. Assumes first line to be a header.
 */
public class CSVInputHandler implements InputHandler {

    private String rawData;
    private String fieldSeparator;

    public CSVInputHandler(String rawData) {
        this.rawData = rawData;
        this.fieldSeparator = ",";
    }

    public CSVInputHandler(String rawData, String fieldSeparator) {
        this.rawData = rawData;
        this.fieldSeparator = fieldSeparator;
    }

    private List<String> processHeader(String header) {
        List<String> fieldNames = new ArrayList<>();
        String[] rawHeaderFields = header.split(fieldSeparator);
        //Consider only non empty fields. Clean up by trimming and lower case
        for(String field: rawHeaderFields) {
            if (!field.isEmpty()) {
                field = field.trim().toLowerCase();
                fieldNames.add(field);
            }
        }
        return fieldNames;
    }

    private Record generateRecord(String dataline, List<String> fieldNames) throws InvalidDatasetException {
        String[] fields = dataline.split(fieldSeparator);
        Record record = new Record();
        int fieldCounter = 0;
        //Process each field. Consider only non empty field. Cleanup by trimming and lower case. Use fieldNames for name of the field
        for(String field: fields) {
            if (field.isEmpty()) {
                continue;
            }

            if (fieldCounter >= fieldNames.size()) {
                throw new InvalidDatasetException("Error while parsing the CSV. Invalid number of fields in a record " +dataline);
            }

            field = field.trim().toLowerCase();
            record.addField(fieldNames.get(fieldCounter), field);
            fieldCounter++;
        }
        return record;
    }

    /**
     * Parses the raw csv text. Generates header and records from it.
     * @return Dataset
     * @throws InvalidDatasetException
     */
    @Override
    public Dataset load() throws InvalidDatasetException, InvalidRequest {
        String[] dataLines = rawData.split("\\n");

        if (dataLines.length == 0) {
            throw new InvalidDatasetException("Error while loading dataset. Dataset is empty");
        }

        //Process Header
        List<String> fieldNames = processHeader(dataLines[0]);
        Dataset dataset = new Dataset(fieldNames);
        //Process each record. Consider only no empty lines.
        for(int i=1; i < dataLines.length; i++) {
            String dataline = dataLines[i];
            if(!dataline.isEmpty()){
                Record record = generateRecord(dataline, fieldNames);
                dataset.addRecord(record);
            }
        }
        return dataset;
    }
}
