(ns shibeshibe.controller
  (:require [clojure.string :as str]
            [shibeshibe.db.writes :as writes]
            [shibeshibe.db.reads :as reads]))

(defn tokens->wallet [args]
  (->> args (map str/upper-case) (partition 2)
       (map (fn [[v k]] {(keyword k) (Double. v)}))
       (into {})))

(defn parse-wallet [{:keys [wallet fiat]}]
  (->> (str/split wallet #" ")
      tokens->wallet
      (reads/appraise-wallet fiat)
      with-out-str))

(defn update-db []
  (println "Updating database")
  (writes/read-sources!))
