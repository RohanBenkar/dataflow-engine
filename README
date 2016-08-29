# Api routes
## Details
    POST  /v1/dataflow/csv
    Params:
        post body (reuqired)
        inputFormat(optional)
            "file" if want to upload csv file. Use "file" form tag for file.
            by default csv is expected in body as text
        groupBy(optional)
            of the form groupBy=<comma separated fields>
        aggregate(required if groupBy is present)
            of form aggregate=<func>(<field>)
            currently support func - sum/count
        outputFormat(optional)
            "json" if want a json output
            by default returns text csv

## Postman Sample
    url: https://www.getpostman.com/collections/e666c1d668d9af42831a

## Test Url
    Host: 54.67.11.204
    port: 9000