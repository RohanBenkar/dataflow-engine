package models.output;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.InvalidRequest;
import models.dataset.Dataset;
import models.dataset.Record;
import play.libs.Json;

/**
 * Created by Rohan Benkar on 8/27/16.
 * Generates JSON from dataset
 */
public class JsonOutputHandler implements OutputHandler<JsonNode>{

    @Override
    public JsonNode store(Dataset dataset) throws InvalidRequest {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode recordArray = mapper.createArrayNode();
        for(Record record: dataset.getRecords()) {
            ObjectNode recordObj = Json.newObject();
            for(String fieldName: dataset.getFieldNames()) {
                recordObj.put(fieldName, record.getField(fieldName));
            }
            recordArray.add(recordObj);
        }

        ObjectNode result = Json.newObject();
        result.put("total", dataset.getRecords().size());
        result.put("data", recordArray);
        return result;
    }
}
