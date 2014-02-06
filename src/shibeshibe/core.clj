(ns shibeshibe.core
  (:gen-class)
  (:require [shibeshibe.tasks.crawl-markets :as mm]
            [shibeshibe.datomic.core :as db]
            [shibeshibe.models :as mo]
            [shibeshibe.web.server :as server]
            [shibeshibe.db.reads :as reads]
            [shibeshibe.controller :as c]
            [shibeshibe.utils :refer :all]))


(defmulti run (fn [option _] option))

(defmethod run "convert" [_ args]
  (connect)
  (->> args c/tokens->wallet reads/wallet->R$)
  (System/exit 0))

(defmethod run "update" [_ args]
  (setup/connect! setup/disk-db)
  (c/update-db)
  (System/exit 0))

(defmethod run "bootstrap" [_ args]
  (setup/init-db! setup/disk-db)
  (System/exit 0))

(defmethod run "server" [_ args]
  (connect)
  (println "Launching server")
  (apply server/create-server args))

(defn -main [command & args]
  (run command args))

;; --------------------------------

