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

The tool reads CSV from standard input and produces JSON-LD serialized as [Newline Delimited JSON](http://ndjson.org). The CSV is interpreted as bindings for the template provided via the `--describe` argument.

You will need to provide the tool with several parameters. 

* `--context` (optional): IRI to be used as the value of `@context` in the output.
* `-d`/`--describe`: A Mustache template for CONSTRUCT or DESCRIBE SPARQL query to describe a resource to download.
* `-e`/`--endpoint`: URL of the SPARQL endpoint to query.
* `-f`/`--frame`: A [JSON-LD frame](http://json-ld.org/spec/latest/json-ld-framing) to apply to the resource's description. See [this example](examples/frame.jsonld).
* `-h`/`--help` (optional): Show how to use the tool.
* `--remove-jsonld-context` (optional): Remove the [JSON-LD context](https://www.w3.org/TR/json-ld/#the-context) from the provided JSON-LD frame from the output. By default, the JSON-LD context is preserved.
* `--retries` (optional, default = 5): Maximum number of attempts at retrying failed queries.
* `--sleep` (optional, default = 0): Time in seconds to wait in between requests to the SPARQL endpoint.
* `-v`/`--verbose` (optional): Switch on logging to the standard error.

## Caveats

* If the describe query outputs an empty RDF, it is silently skipped.

## License

Copyright © 2016-2018 Jindřich Mynarz

Distributed under the Eclipse Public License version 1.0.
