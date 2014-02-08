(ns shibeshibe.core
  (:gen-class)
  (:require [shibeshibe.tasks.crawl-markets :as mm]
            [shibeshibe.web.server :as server]
            [shibeshibe.components :as com]
            [shibeshibe.db.reads :as reads]
            [shibeshibe.controller :as c]
            [shibeshibe.utils :refer :all]))


(defmulti run (fn [option _] option))

(defmethod run "update" [_ args]
  (com/gather-troops!)
  (c/update-db)
  (System/exit 0))

(defmethod run "bootstrap" [_ args]
  (com/bootstrap!)
  (System/exit 0))

(defmethod run "server" [_ args]
  (com/gather-troops!)
  (println "Launching server")
  (apply server/create-server args))

(defn -main [command & args]
  (run command args))

;; --------------------------------
