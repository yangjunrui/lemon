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
(defn- get-stat
    [tair qtype ds]
    (into {} (.getStat tair qtype (.getGroupName tair) ds)))
(defn get-ds-status
    [tair]
    (get-stat tair Q-ds-st 0))
