package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.InvalidDatasetException;
import exceptions.InvalidRequest;
import models.dataset.Dataset;
import models.dataset.GroupedDataset;
import models.input.CSVInputHandler;
import models.input.InputHandler;
import models.output.CSVOutputHandler;
import models.output.JsonOutputHandler;
import models.output.OutputHandler;
import models.transform.aggregate.AggregateFunction;
import models.transform.aggregate.Count;
import models.transform.aggregate.Sum;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by Rohan Benkar on 8/28/16.
 */
public class DataFlowController extends Controller {

    public static final String REQ_INPUT_FORMAT_TAG = "inputFormat";
    public static final String REQ_FILE_FORMAT_TAG = "file";
    public static final String REQ_FILE_PARAM_TAG = "file";
    public static final String REQ_GROUP_BY_QUERY_TAG = "groupBy";
    public static final String REQ_AGGREGATE_TAG = "aggregate";
    public static final String REQ_OUTPUT_FORMAT_TAG = "outputFormat";
    public static final String REQ_JSON_OUTPUT_TAG = "json";
    public static final String REQ_AGGREGATE_QUERY_COUNT = "count";
    public static final String REQ_AGGREGATE_QUERY_SUM = "sum";

    public static Result index() {
        return ok("Your new application is ready.");
    }

    public static Result processCsv() {

        try {

            /*-----LOAD PHASE-----*/
            //InputType can be file or raw text
            String textBody;
            String inputFormat = request().getQueryString(REQ_INPUT_FORMAT_TAG);
            if (inputFormat!=null && inputFormat.equalsIgnoreCase(REQ_FILE_FORMAT_TAG)) {
                Http.MultipartFormData bodyMultipart = request().body().asMultipartFormData();
                Http.MultipartFormData.FilePart csvFile = bodyMultipart!= null ? bodyMultipart.getFile(REQ_FILE_PARAM_TAG) : null;
                if (csvFile == null) {
                    return badRequest("File not uploaded");
                }
                textBody = FileUtils.readFileToString(csvFile.getFile());
            } else {
                Http.RequestBody body = request().body();
                textBody = body.asText();
            }

            if (textBody == null || textBody.isEmpty()) {
                return badRequest("Expecting text/plain request body");
            }
            //Load data
            InputHandler inputHandler = new CSVInputHandler(textBody);
            Dataset dataset = inputHandler.load();

            /*-----TRANSFORM PHASE-----*/
            //Handle groupBy
            String groupByQuery = request().getQueryString(REQ_GROUP_BY_QUERY_TAG);
            if (groupByQuery!=null && !groupByQuery.isEmpty()) {
                GroupedDataset groupedDataset = dataset.groupBy(groupByQuery.split(","));

                //handle aggregation
                String aggregateQuery = request().getQueryString(REQ_AGGREGATE_TAG);
                if (aggregateQuery!=null && !aggregateQuery.isEmpty()) {
                    dataset = handleAggregateQuery(aggregateQuery, groupedDataset);
                } else {
                    return badRequest("Please specify aggregate query");
                }
            }

            /*-----STORE PHASE-----*/
            String outputFormat = request().getQueryString(REQ_OUTPUT_FORMAT_TAG);
            if (outputFormat!=null && outputFormat.equalsIgnoreCase(REQ_JSON_OUTPUT_TAG)) {
                OutputHandler<JsonNode> outputHandler = new JsonOutputHandler();
                return ok(outputHandler.store(dataset));
            } else {
                OutputHandler<String> outputHandler = new CSVOutputHandler();
                return ok(outputHandler.store(dataset));
            }
        } catch (InvalidDatasetException e) {
            Logger.error("Exception ", e);
            return badRequest(e.getMessage());
        } catch (InvalidRequest invalidRequest) {
            Logger.error("Exception ", invalidRequest);
            return badRequest(invalidRequest.getMessage());
        } catch (Exception e) {
            Logger.error("Exception ", e);
            return internalServerError("Error while processing your request. " +e.getMessage());
        }
    }

    /**
     * @param aggregateQuery - Of the form count(fieldname)
     * @param groupedDataset - Dataset to be aggregated
     * @return Aggregated dataset
     * @throws Exception
     */
    private static Dataset handleAggregateQuery(String aggregateQuery, GroupedDataset groupedDataset) throws InvalidRequest, InvalidDatasetException {
        String[] fields = aggregateQuery.split("\\(");
        if (fields.length != 2) {
            throw new InvalidRequest("Invalid aggregate query. Aggregate query should be of form <function>(field_name)");
        }
        AggregateFunction func;
        String op = fields[0].trim().toLowerCase();
        switch (op) {
            case REQ_AGGREGATE_QUERY_COUNT:
                func = new Count();
                break;
            case REQ_AGGREGATE_QUERY_SUM:
                func = new Sum();
                break;
            default:
                throw new InvalidRequest("Invalid aggregate query. Currently supporting SUM and COUNT");
        }
        String field = fields[1].replace(")", "").trim().toLowerCase();
        return groupedDataset.aggregate(field, field, func);
    }
}
