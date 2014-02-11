(ns shibeshibe.datomic.core
  (:require [com.stuartsierra.component :as component]
            [shibeshibe.datomic.schemata :as ls]
            [schema.core :as s]
            [schema.macros :as sm]
            [datomic.api :as d]))


;;;;;;;;;;
;; CORE ;;
;;;;;;;;;;

(definterface Creatable
  (create [])
  (destroy []))

(defrecord Database [uri conn seed schemata extensions]

  Creatable
  (create [component]
    (d/create-database uri)
    (let [conn (d/connect uri)
          db   (d/db conn)]
      (assert conn) (assert db)
      (doseq [kind [extensions schemata seed]]
        (doseq [group kind]
          @(d/transact conn group)))
      (-> component
          (assoc :uri   uri)
          (assoc :conn  conn))))

  component/Lifecycle
  (start [component]
    (let [conn (d/connect uri)]
      (-> component
          (assoc :conn  conn))))

  (stop [component]))


(defn connection->db [connection] (d/db connection))

(sm/defn init-db!
  [data :- {:uri s/String
            :extensions [[ls/Entity]]
            :seed       [[ls/Entity]]
            :schemata   [[ls/Entity]]}]
  (.create (map->Database data)))

(sm/defn from-uri
  [uri :- s/String]
  (map->Database {:uri uri}))


(sm/defn random-uri :- s/String []
  (str "datomic:mem://" (d/squuid)))


(sm/defn tempid :- ls/Eid
  ([] (tempid :db.part/user))
  ([partition :- s/Keyword] (d/tempid partition)))


(sm/defn resolve-tx :- ls/Eid
  [db     :- ls/Database
   tx     :- ls/TxResults
   tempid :- ls/Eid]
  (d/resolve-tempid db (:tempids tx) tempid))

;;;;;;;;;;;;;;;;;;;;;;;
;; Schema generation ;;
;;;;;;;;;;;;;;;;;;;;;;;

(sm/defn gen-attribute :- ls/Entity
  [ident :- s/Keyword
   map   :- ls/Entity]
  (assert (keyword? ident))
  (conj
   {:db/id                 (tempid :db.part/db)
    :db/ident              ident
    :db/valueType          :db.type/string
    :db/cardinality        :db.cardinality/one
    :db/doc                (str ident)
    :db.install/_attribute :db.part/db}
   map))

(sm/defn gen-attribute-seq :- [ls/Entity]
  [skeleton :- {s/Keyword ls/Entity}]
  (map (partial apply gen-attribute) skeleton))


;;;;;;;;;;;;;;;;
;; Operations ;;
;;;;;;;;;;;;;;;;

(sm/defn eid->entity :- ls/Entity
  [db  :- ls/Database
   eid :- ls/Eid]
  (->> eid (d/entity db)
       seq (into {})))

(sm/defn q :- ls/ResultSet
  [db    :- ls/Database
   query :- ls/Query, & args]
  (apply d/q query db args))

(sm/defn qe :- #{ls/Entity}
  [& args]
  (map (comp eid->entity first) (apply q args)))

(sm/defn qes :- #{[ls/Entity]}
  [& args]
  (map (partial map eid->entity) (apply q args)))

;; ---

(sm/defn transact :- ls/TxResults
  [connection :- ls/Connection
   data :- [ls/Entity]]
  (->> data (d/transact connection) deref))

(sm/defn transact-one :- ls/Eid
  [connection :- ls/Connection
   entity :- ls/Entity]
  (let [id (:db/id entity)
        tid (or id (tempid))
        res (-> entity (assoc :db/id tid) vector (->> (transact connection)))]
    (or id (resolve-tx (connection->db connection) res tid))))

(sm/defn entity->eids :- [ls/Eid]
  [db :- ls/Database
   entity :- ls/Entity]
  (map first (q db {:find '[?e] :where (doall (for [[k v] entity] ['?e k v]))})))

(sm/defn entity->eid :- ls/Eid
  [db :- ls/Database
   entity   :- ls/Entity]
  (first (entity->eids db entity)))
