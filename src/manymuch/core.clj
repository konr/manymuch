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
                                        ;
;;;;;;;;;;;;;;;;;;;;;;;
;;; TODO GENERALIZE ;;;
;;;;;;;;;;;;;;;;;;;;;;;

(def distance-1
  {:find '[?x]
   :in '[$ ?from ?to]
   :where '[[?x :market/buy ?to] [?x :market/with ?from]]})

(def distance-2
  {:find '[?x ?y]
   :in '[$ ?from ?to]
   :where '[[?x :market/buy ?to]
            [?x :market/with ?x_name] [?y :market/buy ?x_name]
            [?y :market/with ?from]]})


(def all-queries [distance-1 distance-2])

;;; TODO move to another place

(defn conversion-strategies [from to]
  (mapcat #(db/qes % from to) all-queries))

(defn convert-with-best-strategy [from to amount]
  (apply max
   (for [strategy (conversion-strategies from to)]
     (->> strategy (map :market/for) (reduce * amount)))))

