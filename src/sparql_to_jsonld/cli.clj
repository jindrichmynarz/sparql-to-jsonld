(ns sparql-to-jsonld.cli
  (:gen-class)
  (:require [sparql-to-jsonld.core :as core]
            [sparql-to-jsonld.spec :as spec]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [slingshot.slingshot :refer [try+]]
            [sparclj.core :as sparclj]
            [taoensso.timbre :as timbre :refer [error]]
            [taoensso.timbre.appenders.core :as appenders]))

; ----- Private functions -----

(def join-lines 
  (partial string/join \newline))

(defn- error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (join-lines errors)))

(defn- exit
  "Exit with @status and message `msg`.
  `status` 0 is OK, `status` 1 indicates error."
  [^Integer status
   ^String msg]
  {:pre [(#{0 1} status)]}
  (binding [*out* (if (pos? status) *err* *out*)]
    (println msg))
  (System/exit status))

(def ^:private die
  (partial exit 1))

(def ^:private info
  (partial exit 0))

(defn- usage
  [summary]
  (join-lines ["Serializes RDF resources from a SPARQL endpoint to JSON-LD documents."
               "Options:\n"
               summary]))
 
(defn- validate-params
  [params]
  (when-not (s/valid? ::spec/params params)
    (die (str "The provided arguments are invalid.\n\n"
              (expound/expound-str ::spec/params params)))))

(defn- main
  [{::spec/keys [endpoint sleep verbose?]
    ::sparclj/keys [retries]
    :as params}]
  (validate-params params)
  (timbre/merge-config! {:appenders {:println (if verbose?
                                                (appenders/println-appender {:stream :std-err})
                                                {:enabled? false})}})
  (try+
    (mount/start-with-args {::sparclj/retries retries
                            ::sparclj/url endpoint})
    (catch [:type ::sparclj/connect-exception] {:keys [message]}
      (die message)))
  (try (core/main params) 
       (finally (shutdown-agents))))

; ----- Private vars -----

(def ^:private cli-options
  [["-d" "--describe DESCRIBE" "Path to SPARQL file to describe resources"
    :id ::spec/describe
    :parse-fn io/as-file]
   ["-e" "--endpoint ENDPOINT" "SPARQL endpoint to query"
    :id ::spec/endpoint]
   ["-f" "--frame FRAME" "Path to a JSON-LD frame to format data"
    :id ::spec/frame
    :parse-fn io/as-file]
   ["-h" "--help" "Display help message"
    :id ::spec/help?]
   ["-v" "--verbose" "Switch on logging to the standard error."
    :id ::spec/verbose?]
   [nil "--context CONTEXT" "IRI of JSON-LD @context to be used in the output"
    :id ::spec/context]
   [nil "--remove-jsonld-context" "Remove JSON-LD context from the output"
    :id ::spec/remove-jsonld-context?]
   [nil "--retries RETRIES" "Maximum number of retries of SPARQL queries"
    :id ::sparclj/retries
    :default 5]
   [nil "--sleep SLEEP" "Seconds to sleep between SPARQL queries"
    :id ::spec/sleep
    :default 0]])

; ----- Public functions -----

(defn -main
  [& args]
  (let [{{::spec/keys [help?]
          :as options} :options
         :keys [errors summary]} (parse-opts args cli-options)
        empty-args? (not (seq (remove #{"-"} args)))]
    (cond (or help? empty-args?) (info (usage summary))
          errors (die (error-msg errors))
          :else (main options))))
