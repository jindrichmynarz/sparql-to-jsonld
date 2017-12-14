(ns sparql-to-jsonld.sparql
  (:require [sparql-to-jsonld.endpoint :refer [endpoint]]
            [stencil.core :refer [render-string]]
            [sparclj.core :as sparclj])
  (:import (java.io ByteArrayInputStream)
           (org.apache.jena.rdf.model Model ModelFactory)))

; ----- Public functions -----

(defn ^Model construct-query
  "Execute SPARQL CONSTRUCT `sparql-string`." 
  [sparql-string]
  (let [results (-> endpoint
                    (sparclj/construct-query sparql-string ::sparclj/accept "application/n-triples")
                    .getBytes
                    ByteArrayInputStream.)]
    (doto (ModelFactory/createDefaultModel)
      (.read results "" "NTRIPLES"))))

(defn select-query-unlimited
  "Execute a SPARQL query rendered from `template` using `data`
  repeatedly with paging until empty results are returned." 
  [template & {::sparclj/keys [start-from]}]
  (let [get-query-fn (fn [[limit offset]]
                       (render-string template {:limit limit :offset offset}))]
    (sparclj/select-paged endpoint
                          get-query-fn
                          ::sparclj/start-from start-from)))

(defn describe-query
  "Execute a SPARQL describe query for `resource` from `template`."
  [template resource]
  (construct-query (render-string template {:resource resource})))
