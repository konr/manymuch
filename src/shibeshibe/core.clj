(ns shibeshibe.core
  (:gen-class)
  (:require [shibeshibe.markets :as mm]
            [shibeshibe.datomic.core :as db]
            [shibeshibe.setup :as setup]
            [shibeshibe.reads :as reads]
            [shibeshibe.writes :as writes]
            [shibeshibe.utils :refer :all]))

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
