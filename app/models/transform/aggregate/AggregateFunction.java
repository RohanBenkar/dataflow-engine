package models.transform.aggregate;

import exceptions.InvalidRequest;
import models.dataset.Dataset;

/**
 * Created by Rohan Benkar on 8/27/16.
 */
public abstract class AggregateFunction {
    public String aggregate(Dataset dataset, String fieldName) throws InvalidRequest {
        if (!dataset.getFieldNames().contains(fieldName)) {
            throw new InvalidRequest("Invalid aggregate field name");
        }
        return String.valueOf(calculate(dataset, fieldName));
    }

    protected abstract Object calculate(Dataset dataset, String fieldName) throws InvalidRequest;

    protected int getIntValue(String input) throws NumberFormatException {
        return Integer.parseInt(input);
    }

    protected long getLongValue(String input) throws NumberFormatException {
        return Long.parseLong(input);
    }
}
