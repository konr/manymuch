(ns shibeshibe.markets
  (:require [org.httpkit.client :as http]
            [swiss-arrows.core  :refer :all]
            [cheshire.core      :as json]))

(def markets
  [{:name :cryptsy
    :url "http://pubapi.cryptsy.com/api.php?method=marketdatav2"}
   {:name :mercado-bitcoin
    :url-btc "https://www.mercadobitcoin.com.br/api/ticker/"
    :url-ltc "https://www.mercadobitcoin.com.br/api/ticker_litecoin/"}
   {:name :bitstamp
    :url "https://www.bitstamp.net/api/ticker/"}
   {:name :kraken
    :url "https://api.kraken.com/0/public/Ticker"}])


(defn parse-json-body [res]
  (assert (= 200 (:status res)))
  (-> res :body (json/parse-string keyword)))

(defn get-json [url]
  (parse-json-body @(http/get url)))

(defn post-json [url options]
  (parse-json-body @(http/post url options)))


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

(defmethod last-trades :bitstamp [{:keys [url]}]
  (let [btc (get-json url)]
    [{:buy "BTC"
      :with "USD"
      :for (Double. (:last btc))
      :broker "Bitstamp"}]))

(defmethod last-trades :kraken [{:keys [url]}]
  (let [res (post-json url {:query-params {:pair "XXBTZEUR"}})
        last (get-in res [:result :XXBTZEUR :c 0])]
    [{:buy "BTC"
      :with "EUR"
      :for (Double. last)
      :broker "Kraken"}]))

(defn mirror-market-data [{:keys [buy with for broker] :as data}]
  [data {:buy with :with buy :for (/ 1 for) :broker broker}])


(defn get-market-data []
  (->> markets
       (mapcat last-trades)
       (mapcat mirror-market-data)))
