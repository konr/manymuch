(ns shibeshibe.controller
  (:require [clojure.string :as str]
            [shibeshibe.reads :as reads]))


(defn tokens->wallet [args]
  (->> args (partition 2)
       (map (fn [[v k]] {(keyword k) (Double. v)}))
       (into {})))

(defn parse-wallet [wallet]
  (-> wallet :wallet (str/split #" ")
      tokens->wallet
      reads/wallet->R$
      with-out-str))
