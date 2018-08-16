(defproject sparql-to-jsonld "0.1.3"
  :description "Serializes RDF from a SPARQL endpoint to JSON-LD documents"
  :url "http://github.com/jindrichmynarz/sparql-to-jsonld"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [stencil "0.5.0"]
                 [mount "0.1.13"]
                 [expound "0.7.1"]
                 [org.apache.jena/jena-core "3.8.0"]
                 [org.apache.jena/jena-arq "3.8.0"]
                 [com.github.jsonld-java/jsonld-java "0.12.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.25"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [sparclj "0.2.5"]
                 [slingshot "0.12.2"]
                 [org.clojure/data.csv "0.1.4"]]
  :main sparql-to-jsonld.cli
  :profiles {:dev {:plugins [[lein-binplus "0.4.2"]]}
             :uberjar {:aot :all
                       :uberjar-name "sparql_to_jsonld.jar"}}
  :bin {:name "sparql_to_jsonld"})
