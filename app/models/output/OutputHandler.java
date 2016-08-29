package models.output;

import exceptions.InvalidRequest;
import models.dataset.Dataset;

/**
 * Created by rbenkar on 8/27/16.
 */
public interface OutputHandler<T> {
    public T store(Dataset dataset) throws InvalidRequest;
}
