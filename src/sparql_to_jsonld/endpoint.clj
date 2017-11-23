(ns sparql-to-jsonld.endpoint
  (:require [mount.core :as mount :refer [defstate]]
            [clj-http.client :as client]
            [clojure.string :as string]))

(defn init-endpoint
  "Ping endpoint to test if it is up." 
  [{:keys [sparql-endpoint]
    :as config}]
  (let [test-query "ASK { [] ?p [] . }"
        virtuoso? (-> sparql-endpoint
                      (client/get {:query-params {"query" test-query}
                                   :throw-entire-message? true})
                      (get-in [:headers "Server"] "")
                      (string/includes? "Virtuoso"))]
    (assoc config :virtuoso? virtuoso?)))

(defstate endpoint
  :start (init-endpoint (mount/args)))
