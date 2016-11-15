(ns sparql-to-jsonld.endpoint
  (:require [mount.core :as mount :refer [defstate]]
            [clj-http.client :as client]))

(defn init-endpoint
  "Ping endpoint to test if it is up." 
  [{:keys [sparql-endpoint]
    :as config}]
  (client/head sparql-endpoint {:throw-entire-message? true})
  config)

(defstate endpoint
  :start (init-endpoint (mount/args)))
