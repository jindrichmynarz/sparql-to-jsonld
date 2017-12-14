(defproject sparql-to-jsonld "0.1.0"
  :description "Serializes RDF from a SPARQL endpoint to JSON-LD documents"
  :url "http://github.com/jindrichmynarz/sparql-to-jsonld"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/tools.cli "0.3.5"]
                 [stencil "0.5.0"]
                 [prismatic/schema "1.1.3"]
                 [commons-validator/commons-validator "1.5.1"]
                 [mount "0.1.10"]
                 [org.apache.jena/jena-core "3.1.1"]
                 [org.apache.jena/jena-arq "3.1.1"]
                 [com.github.jsonld-java/jsonld-java "0.8.3"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
                                                    javax.jms/jms
                                                    com.sun.jmdk/jmxtools
                                                    com.sun.jmx/jmxri]]
                 [sparclj "0.1.9"]]
  :main sparql-to-jsonld.core
  :profiles {:dev {:plugins [[lein-binplus "0.4.2"]]}
             :uberjar {:aot :all
                       :uberjar-name "sparql_to_jsonld.jar"}}
  :aliases {"file" ["run" "-m" "sparql-to-jsonld.file"]}
  :bin {:name "sparql_to_jsonld"})
