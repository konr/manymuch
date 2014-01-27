(ns shibeshibe.core
  (:gen-class)
  (:require [shibeshibe.markets :as mm]
            [shibeshibe.datomic.core :as db]
            [shibeshibe.setup :as setup]
            [shibeshibe.server :as server]
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


(defmulti run (fn [option _] option))

(defmethod run "convert" [_ args]
  (try (setup/connect! setup/disk-db)
       (catch Exception e (do (println "Failed! Creating a memory database")
                              (setup/init-db! setup/mem-db)
                              (update-db))))
  (->> args arguments->wallet reads/wallet->R$)
  (System/exit 0))

(defmethod run "update" [_ args]
  (setup/connect! setup/disk-db)
  (update-db)
  (System/exit 0))

(defmethod run "bootstrap" [_ args]
  (setup/init-db! setup/disk-db)
  (System/exit 0))

(defmethod run "server" [_ args]
  (apply server/create-server args))

(defn -main [command & args]
  (println "Connecting to the local database")
  (run command args))
