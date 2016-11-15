(ns sparql-to-jsonld.util
  (:require [clojure.string :as string]))

(def join-lines 
  (partial string/join \newline))

(defn lazy-cat'
  "Lazily concatenates lazy sequence of sequences @colls.
  Taken from <http://stackoverflow.com/a/26595111/385505>."
  [colls]
  (lazy-seq
    (if (seq colls)
      (concat (first colls) (lazy-cat' (next colls))))))
