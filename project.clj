(defproject manymuch "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [swiss-arrows "0.6.0"]
                 [cheshire "5.2.0"]
                 [midje "1.6.0" :exclusions [org.clojure/clojure]]
                 [com.stuartsierra/component "0.2.1"]
                 [com.datomic/datomic-free "0.9.4384"]
                 [prismatic/schema "0.1.8"]]
  :main manymuch.core)
