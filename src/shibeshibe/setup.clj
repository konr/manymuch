(ns shibeshibe.setup
  (:require [shibeshibe.datomic.core :as db]
            [shibeshibe.models :as mo]))


(def mem-db  (db/random-uri))
(def disk-db "datomic:free://localhost:4334/shibeshibe-2")


(defn connect! [uri]
  (db/connect! uri))

(defn init-db! [uri]
  (db/init-db!
   {:uri uri
    :extensions []
    :seed []
    :schemata [(db/gen-attribute-seq mo/all-models)]}))
