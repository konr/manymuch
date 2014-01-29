(ns shibeshibe.utils
  (:use [midje.sweet :refer :all]))

(defn map-keys [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map (function k) v)))))

(facts "on map-keys"
       (map-keys name {:a 1 :b 3})
       => {"a" 1 "b" 3})

(defn map-vals [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map k (function v))))))

(facts "on map-vals"
       (map-vals (partial + 10) {:a 1 :b 3})
       => {:a 11 :b 13})

(defn map-keys* [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map (function k v) v)))))

(facts "on map-keys*"
       (map-keys* (fn [k v] (keyword (str (name k) v))) {:a 1 :b 2})
       => {:a1 1 :b2 2})

(defn map-vals* [function map]
  (loop [[[k v] & tail :as all] (vec map) new-map {}]
    (if-not (seq all) new-map (recur tail (assoc new-map k (function k v))))))

(facts "on map-vals*"
       (map-vals* (fn [k v] (format "Key %s Value %d" k v)) {:a 1 :b 2})
       => {:a "Key :a Value 1" :b "Key :b Value 2"})

(defn find-first [fn seq]
  (first (filter fn seq)))

(facts "on find-first"
       (find-first #(> % 3) [1 2 3 4 5 6])
       => 4)

(defn or-die [item]
  (or item (throw (Exception. "Nil element!"))))

(facts on "or-die"
       (-> 30  or-die) => 30
       (-> nil or-die) => throws)

(defn assoc-maybe [item & keypairs]
  (loop [[k v & cetera] keypairs
         item item]
    (if (nil? k) item
        (if (nil? v) (recur cetera item)
            (recur cetera (assoc item k v))))))

(facts "on assoc-maybe"
       (-> {} (assoc-maybe :a 3))
       => {:a 3}
       (-> {} (assoc-maybe :a nil :b 4))
       => {:b 4})
