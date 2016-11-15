# sparql-to-jsonld

A command-line tool to serialize RDF from a SPARQL endpoint to JSON-LD documents.

## Usage

Compile using [Leiningen](http://leiningen.org):

```sh
lein uberjar
```

Run from the command-line:

```sh
java -jar sparql_to_jsonld.jar --help
```

You will need to provide the tool with several parameters. Provide configuration as an [EDN](https://github.com/edn-format/edn) file that contains the following keys:

* `sparql-endpoint`: URL of a SPARQL query endpoint, such as `http://localhost:8890/sparql`.
* `page-size` (optional, default = 1000): Number of resources to download in one query.
* `sleep` (optional, default = 1): Time in seconds to wait in between requests to the SPARQL endpoint.
* `start-from` (optional, default = 0): Number of resources to skip that can be used to restart a previously interrupted download.
* `max-attempts` (optional, default = 5): Maximum number of attempts at retrying failed queries.

See [this example](examples/config.edn) of a configuration. Apart from the configuration you will need to provide the following parameters:

* `--sparql`: A paged SPARQL query to select IRIs of resources to download. The query must be a [Mustache](https://mustache.github.io) template that contains `limit` and `offset` variables to drive the paged execution. The query must project a single variable named `?resource`. See [this example](examples/select_query.mustache).
* `--describe`: A CONSTRUCT or DESCRIBE SPARQL query to describe a resource to download. The query must be a Mustache template that uses the `resource` variable as a placeholder for the resource's IRI. See [this example](examples/describe_query.mustache).
* `--frame`: A [JSON-LD frame](http://json-ld.org/spec/latest/json-ld-framing) to apply to the resource's description. See [this example](examples/frame.jsonld).

The tool prints the framed JSON-LD documents containing the descriptions of the selected resources to the standard output, so that it can be redirected to a file or piped to another process. Each JSON document is printed on a single line.

## Caveats

* If the describe query outputs an empty RDF, it is silently skipped.

## License

Copyright © 2016 Jindřich Mynarz

Distributed under the Eclipse Public License version 1.0.
