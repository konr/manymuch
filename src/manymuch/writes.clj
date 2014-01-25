(ns manymuch.writes
  (:require [manymuch.markets :as mm]
            [manymuch.datomic.core :as db]
            [manymuch.utils :refer :all]))

(defn read-sources! []
  (doseq [item (mm/get-market-data)]
    (->> item (map-keys #(keyword "market" (name %)))
         db/transact-one)))
