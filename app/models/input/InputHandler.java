package models.input;

import exceptions.InvalidDatasetException;
import exceptions.InvalidRequest;
import models.dataset.Dataset;

/**
 * Created by rbenkar on 8/27/16.
 */
public interface InputHandler {
    public Dataset load() throws InvalidDatasetException, InvalidRequest;
}
