(ns shibeshibe.writes
  (:require [shibeshibe.markets :as mm]
            [shibeshibe.datomic.core :as db]
            [shibeshibe.utils :refer :all]))

(defn read-sources! []
  (doseq [item (mm/get-market-data)]
    (->> item (map-keys #(keyword "market" (name %)))
         db/transact-one)))
