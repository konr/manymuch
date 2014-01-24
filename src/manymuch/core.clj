(ns manymuch.core
  (:require [manymuch.markets :as mm]
            [manymuch.datomic.core :as db]
            [manymuch.models :as mo]
            [manymuch.utils :refer :all]))





(defn gather-troops! []
  (db/init-db!
   {:uri (db/random-uri)
    :extensions []
    :seed []
    :schemata [(db/gen-attribute-seq mo/all-models)]}))


(defn read-sources! []
  (doseq [item (mm/get-market-data)]
    (->> item (map-keys #(keyword "market" (name %)))
         db/transact-one)))

(defn -main []
  (gather-troops!)
  (read-sources!))
