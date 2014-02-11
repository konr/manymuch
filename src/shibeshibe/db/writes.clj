(ns shibeshibe.db.writes
  (:require [shibeshibe.datomic.core :as db]
            [shibeshibe.tasks.crawl-markets :as mm]
            [shibeshibe.utils :refer :all]
            [midje.sweet :refer :all]))

(defn write-market-data! [context]
  (let [db    (get context ::db/database)
        conn  (get context ::db/connection)
        items (get context ::mm/market)]
    (doseq [item items]
      (->> (assoc-maybe (.entity item) :db/id (.eid item db))
           (db/transact-one conn)))))
