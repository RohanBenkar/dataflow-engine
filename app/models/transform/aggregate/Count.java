package models.transform.aggregate;

import exceptions.InvalidRequest;
import models.dataset.Dataset;

/**
 * Created by rbenkar on 8/27/16.
 */
public class Count extends AggregateFunction{
    @Override
    protected Object calculate(Dataset dataset, String fieldName) throws InvalidRequest, NumberFormatException {
        return dataset.getRecords().size();
    }
}
