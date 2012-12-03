(ns lemon.tair.monitor
    (:gen-class)
    (:import [com.taobao.tair.impl DefaultTairManager]
             [com.taobao.tair.extend.impl DefaultExtendTairManager])
    )

(defn new-tair
    ([cs-list group]
     (let [tair (DefaultTairManager.)
           _ (doto tair
                 (.setConfigServerList cs-list)
                 (.setGroupName group)
                 (.init))]
         tair)))

(def Q-ns-cap (int 1))
(def Q-mig-st (int 2))
(def Q-ds-st (int 3))
(def Q-group-st (int 4))
(def Q-statistics (int 5))

(defn ip-to-id [ip] (com.taobao.tair.etc.TairUtil/hostToLong ip))
(defn- atoi [s] (Integer/parseInt s))
(defn- atol [s] (Long/parseLong s))
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
(defn get-status-of-all-ns
    ([tair]
     (get-status-by-ds tair "0.0.0.0")))
