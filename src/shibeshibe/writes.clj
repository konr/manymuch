(ns shibeshibe.writes
  (:require [shibeshibe.markets :as mm]
            [shibeshibe.datomic.core :as db]
            [shibeshibe.reads :as reads]
            [shibeshibe.utils :refer :all]
            [midje.sweet :refer :all]))

(defn read-sources! []
  (doseq [item (mm/get-market-data)]
    (let [object (.entity item)
          eid (-> object (select-keys [:market/broker :market/with :market/buy]) db/entity->eid)]
      (-> object (assoc-maybe :db/id eid) db/transact-one))))
