# sparql-to-jsonld

A command-line tool to serialize RDF from a SPARQL endpoint to JSON-LD documents. You give it a SPARQL [`SELECT`](https://www.w3.org/TR/sparql11-query/#select) query to select resources you want, a SPARQL [`CONSTRUCT`](https://www.w3.org/TR/sparql11-query/#construct) or [`DESCRIBE`](https://www.w3.org/TR/sparql11-query/#describe) query to construct a description of each resource, and a [JSON-LD frame](http://json-ld.org/spec/latest/json-ld-framing) to shape the RDF graph into a predictable JSON tree.

## Usage

Compile using [Leiningen](http://leiningen.org):

```sh
lein uberjar
```

Alternatively, compile using [lein-binplus](https://github.com/BrunoBonacci/lein-binplus) to produce an executable file:

```sh
lein bin
```

Run from the command-line:

```sh
java -jar sparql_to_jsonld.jar --help
```

Or, when compiled with lein-binplus:

```sh
target/sparql_to_jsonld --help
```

You will need to provide the tool with several parameters. Provide configuration as an [EDN](https://github.com/edn-format/edn) file that contains the following keys:

* `sparql-endpoint`: URL of a SPARQL query endpoint, such as `http://localhost:8890/sparql`.
* `page-size` (optional, default = 1000): Number of resources to download in one query.
* `sleep` (optional, default = 1): Time in seconds to wait in between requests to the SPARQL endpoint.
* `start-from` (optional, default = 0): Number of resources to skip that can be used to restart a previously interrupted download.
* `max-attempts` (optional, default = 5): Maximum number of attempts at retrying failed queries.

See [this example](examples/config.edn) of a configuration. Apart from the configuration you will need to provide the following parameters:

* `-s`/`--sparql`: A paged SPARQL query to select IRIs of resources to download. The query must be a [Mustache](https://mustache.github.io) template that contains `limit` and `offset` variables to drive the paged execution. The query must project a single variable named `?resource`. See [this example](examples/select_query.mustache).
* `-d`/`--describe`: A CONSTRUCT or DESCRIBE SPARQL query to describe a resource to download. The query must be a Mustache template that uses the `resource` variable as a placeholder for the resource's IRI. See [this example](examples/describe_query.mustache).
* `-f`/`--frame`: A [JSON-LD frame](http://json-ld.org/spec/latest/json-ld-framing) to apply to the resource's description. See [this example](examples/frame.jsonld).
* `-o`/`--output` (optional, default = standard output): A path to file to which the JSON-LD documents will be written.
* `--remove-jsonld-context` (optional): Remove the [JSON-LD context](https://www.w3.org/TR/json-ld/#the-context) from the provided JSON-LD frame from the output. By default, the JSON-LD context is preserved.

By default, the tool prints the framed JSON-LD documents containing the descriptions of the selected resources to the standard output, so that it can be redirected to a file or piped to another process. JSON-LD is output as [Newline Delimited JSON](http://ndjson.org).

## Caveats

* If the describe query outputs an empty RDF, it is silently skipped.

## License

Copyright © 2016 Jindřich Mynarz

Distributed under the Eclipse Public License version 1.0.
