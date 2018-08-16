(ns sparql-to-jsonld.spec
  (:require [clojure.spec.alpha :as s]
            [sparclj.core :as sparclj]
            [sparclj.spec :as sparclj-spec])
  (:import (java.io File)))

(defn- file?
  [^File file]
  (and (instance? File file)
       (.exists file)
       (.isFile file)
       (.canRead file)))

(s/def ::context ::sparclj-spec/iri)

(s/def ::describe file?)

(s/def ::endpoint ::sparclj/url)

(s/def ::frame file?)

(s/def ::help? boolean?)

(s/def ::remove-jsonld-context? boolean?)

(s/def ::sleep (s/and integer? (complement neg?)))

(s/def ::verbose? boolean?)

(s/def ::params
  (s/keys :req [::describe
                ::endpoint
                ::frame]
          :opt [::context
                ::help?
                ::remove-jsonld-context?
                ::sleep
                ::sparclj/retries
                ::verbose?]))
