(ns shibeshibe.models)


;;;;;;;;
;; DB ;;
;;;;;;;;

(def market-model
  {:market/buy    {}
   :market/with   {}
   :market/for    {:db/valueType :db.type/double}
   :market/broker {}})

(def all-models (conj market-model {}))

;;;;;;;;;;;;
;; Schema ;;
;;;;;;;;;;;;

(defprotocol StorableOnDatomic
  (entity [this]))

(defprotocol Reversible
  (reverse [this]))


(defrecord Trade [buy with for broker]
  StorableOnDatomic
  (entity [this]
    {:market/buy    buy
     :market/with   with
     :market/for    for
     :market/broker broker})

  Reversible
  (reverse [this]
    (conj this
      {:buy with
       :with buy
       :for (/ 1 for)
       :broker broker})))

(defn new-trade [buy with for broker]
  (->Trade buy with for broker))
