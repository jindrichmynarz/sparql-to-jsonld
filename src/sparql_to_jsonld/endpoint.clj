(ns sparql-to-jsonld.endpoint
  (:require [mount.core :as mount :refer [defstate]]
            [sparclj.core :refer [init-endpoint]]))

(defstate endpoint
  :start (init-endpoint (mount/args)))
