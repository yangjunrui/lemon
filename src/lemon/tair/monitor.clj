(ns lemon.tair.monitor
    (:gen-class)
    (:require [lemon.tair.clusters :as clusters])
    (:import [com.taobao.tair.impl DefaultTairManager]
             [com.taobao.tair.extend.impl DefaultExtendTairManager])
    )

(defn- connect-to-tair
    ([cs-list group]
     (let [tair (DefaultTairManager.)
           _ (doto tair
                 (.setConfigServerList cs-list)
                 (.setGroupName group)
                 (.init))]
         tair)))
(defn get-tair
    [eng cluster-name]
    (defn- select-filter [{name :name}] (= name cluster-name))
    (let [config (first (filter select-filter (clusters/clusters (keyword eng))))
          {:keys [master slave group]} config]
        (connect-to-tair [master slave] group)))

(def Q-ns-cap (int 1))
(def Q-mig-st (int 2))
(def Q-ds-st (int 3))
(def Q-group-st (int 4))
(def Q-statistics (int 5))

(defn ip-to-id [ip] (com.taobao.tair.etc.TairUtil/hostToLong ip))
(defn- atoi [s] (Integer. s))
(defn- atol [s] (Long. s))
(defn- group-of-tair [tair] (.getGroupName tair))
(defn- get-stat
    [tair qtype ds]
    (into {} (.getStat tair qtype (group-of-tair tair) ds)))
(defn get-ds-status
    [tair]
    (get-stat tair Q-ds-st 0))
(defn get-ds-list
    [tair]
    (keys (get-ds-status tair)))
(defn get-ns-capacity
    [tair]
    (let [st (get-stat tair Q-ns-cap 0)]
        (into (sorted-map)
              (map (fn [[k v]]
                       [(Integer. (re-find  #"\d+" k)) (Long. v)])
                   st))))
(defn get-status-by-ds
    ([tair ds]
     (let [st (get-stat tair Q-statistics (ip-to-id ds))
           ns-st-map (atom (sorted-map))]
         (doseq [[k v] st]
             (let [seps (clojure.string/split k #"\s+")
                   ns (atoi (seps 0))
                   field (seps 1)]
                 (swap! ns-st-map assoc-in [ns field] (atol v))))
         @ns-st-map)))
(defn get-status-of-ds
    ([tair ds]
     (apply merge-with + (vals (get-status-by-ds tair ds)))))
(defn get-status-of-all-ds
    ([tair]
     (let [ds-list (get-ds-list tair)]
         (zipmap ds-list (map #(get-status-of-ds tair %) ds-list)))))
(defn get-status-of-all-ns
    ([tair]
     (let [st (get-status-by-ds tair "0.0.0.0")
           cap (get-ns-capacity tair)
           merged (merge-with #(assoc %1 "quota" %2) st cap)]
         (into (sorted-map) (remove #(number? (second %)) merged)))))

