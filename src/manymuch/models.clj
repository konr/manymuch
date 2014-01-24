(ns manymuch.models)


(def market-model
  {:market/buy    {}
   :market/with   {}
   :market/for    {:db/valueType :db.type/double}
   :market/broker {}})



(def all-models (conj market-model {}))
