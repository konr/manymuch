(defproject shibeshibe "0.1.0-SNAPSHOT"
  :description "Figure out how much money you have in crypto currencies"
  :url "http://github.com/konr/shibeshibe"
  :license {:name "GPL"
            :url "http://www.gnu.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.16"]
                 [swiss-arrows "0.6.0"]
                 [cheshire "5.2.0"]
                 [midje "1.6.0" :exclusions [org.clojure/clojure]]
                 [com.stuartsierra/component "0.2.1"]
                 [com.datomic/datomic-free "0.9.4384"]
                 [prismatic/schema "0.1.8"]]
  :main shibeshibe.core)
