(ns manymuch.core
  (:gen-class)
  (:require [manymuch.markets :as mm]
            [manymuch.datomic.core :as db]
            [manymuch.setup :as setup]
            [manymuch.reads :as reads]
            [manymuch.writes :as writes]
            [manymuch.utils :refer :all]))

(defn arguments->wallet [args]
  (->> args (partition 2)
       (map (fn [[v k]] {(keyword k) (Double. v)}))
       (into {})))

(defn update-db []
  (println "Updating database")
  (writes/read-sources!))

(defn -main [& args]
  (println "Connecting to the local database")
  (try (setup/connect! setup/disk-db)
      (catch Exception e (do (println "Failed! Creating a memory database")
                              (setup/init-db! setup/mem-db)
                              (update-db))))
  (->> args arguments->wallet reads/wallet->R$)
  (System/exit 0))
