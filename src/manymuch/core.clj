(ns manymuch.core
  (:gen-class)
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


(defn arguments->wallet [args]
  (->> args (partition 2)
       (map (fn [[v k]] {(keyword k) (Double. v)}))
       (into {})))
                                        ;
(defn distance [depth]
  (let [symbols (repeatedly depth (fn [] {:symbol (gensym '?symbol_) :name (gensym '?name_)}))]
    {:find (map :symbol symbols)
     :in '[$ ?from ?to]
     :where (->> symbols (concat [{:name '?to}]) (partition 2 1)
                 (mapcat (fn [[{name1 :name} {sym2 :symbol name2 :name}]]
                           [[sym2 :market/buy name1] [sym2 :market/with name2]]))
                 (concat [(list (-> symbols last :symbol) :market/with '?from)]))}))


(def all-queries (map distance (range 1 4)))

;;; TODO move to another place

(defn conversion-strategies [from to]
  ;; FIX figure WTF is going
  ;; on with this wrong order
  (mapcat #(db/qes % to from) all-queries))

(defn convert-with-best-strategy [from to amount]
  (clojure.pprint/pprint [from to])
  (apply max 0
         (for [strategy (conversion-strategies from to)]
           (->> strategy (map :market/for) (reduce * amount)))))


(defn appraise-wallet [currency wallet]
  (let [value-kw (keyword (str "value-in-" currency))

        lines (map (fn [[coin amount]] {:name (name coin)
                                       :amount amount
                                       value-kw (-> coin name
                                                    (convert-with-best-strategy currency amount)
                                                    Math/round) }) wallet)]
    (->>
     [{:name "-----" :amount "-----" value-kw "-----"}
      {:name "TOTAL" :amount "" value-kw (reduce + (map value-kw lines))}]
     (concat lines)
     clojure.pprint/print-table)))

(def wallet->R$ (partial appraise-wallet "BRL"))


(defn -main [& args]
  (println "Preparing databases")
  (gather-troops!)
  (println "Reading from the Interweb")
  (read-sources!)
  (println "Converting")
  (->> args arguments->wallet wallet->R$))
