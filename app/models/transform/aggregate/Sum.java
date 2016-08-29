package models.transform.aggregate;

import exceptions.InvalidRequest;
import models.dataset.Dataset;
import models.dataset.Record;

/**
 * Created by rbenkar on 8/27/16.
 */
public class Sum extends AggregateFunction {

    @Override
    protected Object calculate(Dataset dataset, String fieldName) throws InvalidRequest {
        int sum = 0;
        for (Record record: dataset.getRecords()) {
            sum += getIntValue(record.getField(fieldName));
        }
        return sum;
    }
}
