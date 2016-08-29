package models.output;

import exceptions.InvalidRequest;
import models.dataset.Dataset;
import models.dataset.Record;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Generates CSV from dataset
 */
public class CSVOutputHandler implements OutputHandler<String>{

    private String fieldSeperator;

    public CSVOutputHandler() {
        fieldSeperator = ",";
    }

    public CSVOutputHandler(String fieldSeperator) {
        this.fieldSeperator = fieldSeperator;
    }

    @Override
    public String store(Dataset dataset) throws InvalidRequest {

        StringBuilder result = new StringBuilder();

        //Header
        result.append(generateHeader(dataset));

        //Records
        for(Record record: dataset.getRecords()) {
            StringBuilder recordString = generateRecordString(dataset, record);
            result.append(recordString);
        }
        return result.toString();
    }

    private StringBuilder generateHeader(Dataset dataset) {
        String delim = "";
        StringBuilder header = new StringBuilder();
        for(String fieldName: dataset.getFieldNames()) {
            header.append(delim).append(fieldName);
            delim = fieldSeperator;
        }
        header.append("\n");
        return header;
    }

    private StringBuilder generateRecordString(Dataset dataset, Record record) throws InvalidRequest {
        String delim = "";
        StringBuilder recordString = new StringBuilder();
        for(String fieldName: dataset.getFieldNames()) {
            recordString.append(delim).append(record.getField(fieldName));
            delim = fieldSeperator;
        }
        recordString.append("\n");
        return recordString;
    }
}
