(ns sparql-to-jsonld.sparql
  (:require [sparql-to-jsonld.endpoint :refer [endpoint]]
            [sparql-to-jsonld.util :as util]
            [clj-http.client :as client]
            [stencil.core :refer [render-string]]
            [slingshot.slingshot :refer [throw+ try+]]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zip-xml])
  (:import (java.io ByteArrayInputStream)
           (org.apache.jena.rdf.model Model ModelFactory)))

; ----- Private functions -----

(defn- xml->zipper
  "Take XML string `s`, parse it, and return XML zipper."
  [s]
  (->> s
       .getBytes
       ByteArrayInputStream.
       xml/parse
       zip/xml-zip))

(defn- execute-query
  "Execute SPARQL query from `sparql-string`."
  [sparql-string & {:keys [accept attempts]
                    :or {attempts 0}}]
  (let [{:keys [max-attempts sparql-endpoint]} endpoint
        params (cond-> {:query-params {"query" sparql-string}
                        :throw-entire-message? true}
                 accept (assoc :accept accept))]
    (try+ (:body (client/get sparql-endpoint params))
          (catch [:status 404] _
            (if (> attempts max-attempts)
              (throw+)
              (do (Thread/sleep (* attempts 1000))
                  (execute-query sparql-string :accept accept :attempts (inc attempts))))))))

; ----- Public functions -----

(defn ^Model construct-query
  "Execute SPARQL CONSTRUCT `sparql-string`." 
  [sparql-string]
  (let [results (ByteArrayInputStream. (.getBytes (execute-query sparql-string :accept "text/ntriples")))]
    (doto (ModelFactory/createDefaultModel)
      (.read results "" "NTRIPLES"))))

(defn select-query
  "Execute SPARQL SELECT query.
  Returns an empty sequence when the query has no results."
  [sparql-string]
  (let [results (xml->zipper (execute-query sparql-string))
        sparql-variables (map keyword (zip-xml/xml-> results :head :variable (zip-xml/attr :name)))
        sparql-results (zip-xml/xml-> results :results :result)
        get-bindings (comp (partial zipmap sparql-variables) #(zip-xml/xml-> % :binding zip-xml/text))]
    (map get-bindings sparql-results)))

(defn select-query-unlimited
  "Execute a SPARQL query rendered from `template` using `data`
  repeatedly with paging until empty results are returned." 
  [template]
  (let [{:keys [page-size start-from]} endpoint
        paged-select (fn [offset]
                       (select-query (render-string template {:limit page-size :offset offset})))]
    (->> (iterate (partial + page-size) start-from)
         (map paged-select)
         (take-while seq)
         util/lazy-cat')))

(defn describe-query
  "Execute a SPARQL describe query for `resource` from `template`."
  [template resource]
  (construct-query (render-string template {:resource resource})))
