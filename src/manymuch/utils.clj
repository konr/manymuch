(ns manymuch.utils)

(defn map-keys [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map (function k) v)))))

(defn map-vals [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map k (function v))))))

(defn map-keys* [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map (function k v) v)))))

(defn map-vals* [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map k (function k v))))))


(defn find-first [fn seq]
  (first (filter fn seq)))

(defn or-die [item]
  (or item (throw (Exception. "Nil element!"))))
