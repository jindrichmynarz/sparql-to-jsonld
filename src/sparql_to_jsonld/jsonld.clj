(ns sparql-to-jsonld.jsonld
  (:import (java.util LinkedHashMap)
           (java.io ByteArrayOutputStream)
           (com.github.jsonldjava.core JsonLdOptions JsonLdProcessor)
           (com.github.jsonldjava.utils JsonUtils)
           (org.apache.jena.rdf.model Model)))

(defonce ^:private
  jsonld-options
  (doto (JsonLdOptions.) (.setUseNativeTypes true)))

(defn ^LinkedHashMap compact-jsonld
  "Compact JSON-LD with `context` using optional `options`."
  [^LinkedHashMap context
   ^LinkedHashMap jsonld
   & {:keys [options]
      :or {options jsonld-options}}]
  (JsonLdProcessor/compact jsonld context options))

(defn ^LinkedHashMap frame-jsonld
  "Frame JSON-LD with `frame` using optional `options`."
  [^LinkedHashMap frame
   ^LinkedHashMap jsonld
   & {:keys [options]
      :or {options jsonld-options}}]
  (JsonLdProcessor/frame jsonld frame options))

(defn ^LinkedHashMap string->jsonld
  "Convert `string` in a JSON-LD hash map."
  [^String string]
  (JsonUtils/fromString string))

(defn ^String jsonld->string
  "Convert `jsonld` to string."
  [^LinkedHashMap jsonld & {:keys [context remove-jsonld-context?]}]
  (JsonUtils/toString (cond-> jsonld
                        remove-jsonld-context? (doto (.remove "@context"))
                        context (doto (.put "@context" context)))))

(defn ^LinkedHashMap model->jsonld
  "Convert RDF `model` to JSON-LD."
  [^Model model]
  (with-open [output (ByteArrayOutputStream.)]
    (.write model output "JSONLD")
    (JsonUtils/fromString (str output))))
