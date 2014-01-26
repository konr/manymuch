(ns shibeshibe.markets
  (:require [org.httpkit.client :as http]
            [swiss-arrows.core  :refer :all]
            [cheshire.core      :as json]))

(def markets
  [{:name :cryptsy
    :url "http://pubapi.cryptsy.com/api.php?method=marketdatav2"}
   {:name :mercado-bitcoin
    :url-btc "https://www.mercadobitcoin.com.br/api/ticker/"
    :url-ltc "https://www.mercadobitcoin.com.br/api/ticker_litecoin/"}])


(defn get-json [url]
  (let [res @(http/get url)]
    (assert (= 200 (:status res)))
    (-> res :body (json/parse-string keyword))))


(defmulti last-trades :name)

(defmethod last-trades :cryptsy [{url :url}]
  (for [[_ market] (get-in (get-json url) [:return :markets])]
    {:buy (:primarycode market)
     :with (:secondarycode market)
     :for (Double. (:lasttradeprice market))
     :broker "Cryptsy"}))

(defmethod last-trades :mercado-bitcoin [{:keys [url-btc url-ltc]}]
  (let [btc (get-json url-btc)
        ltc (get-json url-ltc)]
    [{:buy "BTC"
      :with "BRL"
      :for (get-in btc [:ticker :last])
      :broker "Mercado Bitcoin"}
     {:buy "LTC"
      :with "BRL"
      :for (get-in ltc [:ticker :last])
      :broker "Mercado Bitcoin"}]))


(defn mirror-market-data [{:keys [buy with for broker] :as data}]
  [data {:buy with :with buy :for (/ 1 for) :broker broker}])


(defn get-market-data []
  (->> markets
       (mapcat last-trades)
       (mapcat mirror-market-data)))
