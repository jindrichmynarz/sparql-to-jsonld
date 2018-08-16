(ns sparql-to-jsonld.core
  (:require [sparql-to-jsonld.jsonld :as jsonld]
            [sparql-to-jsonld.sparql :as sparql]
            [sparql-to-jsonld.spec :as spec]
            [clojure.data.csv :as csv])
  (:import (java.io Reader)))

(defn csv->seq
  "Convert `reader` with CSV to a sequence of maps."
  [^Reader reader]
  (let [[head & data] (csv/read-csv reader)
        header (map keyword head)]
    (map (partial zipmap header) data)))

(defn main
  [{::spec/keys [context remove-jsonld-context? sleep]
    describe-file ::spec/describe
    frame-file ::spec/frame
    :as params}]
  (let [describe-query (slurp describe-file)
        frame (jsonld/string->jsonld (slurp frame-file))
        describe (fn [data]
                   (when-not (zero? sleep) (Thread/sleep (* sleep 1000)))
                   (sparql/describe-query describe-query data))
        frame-fn (partial jsonld/frame-jsonld frame)
        compact-fn (partial jsonld/compact-jsonld frame)
        convert-fn (comp compact-fn frame-fn jsonld/model->jsonld)
        jsonld-empty? (fn [data] (-> (.keySet data)
                                     set
                                     (disj "@context")
                                     empty?))
        serialize-fn (fn [data] (jsonld/jsonld->string data
                                                       :remove-jsonld-context? remove-jsonld-context?
                                                       :context context))]
    (dorun (->> (csv->seq *in*)
                (pmap describe)
                (remove (memfn isEmpty))
                (pmap convert-fn)
                (remove jsonld-empty?)
                (map (comp println serialize-fn))))))
