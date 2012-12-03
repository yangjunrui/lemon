(ns lemon.views.welcome
  (:require [lemon.views.common :as common]
            [lemon.tair.monitor :as monitor]
            [lemon.tair.clusters :as clusters]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial post-row [[ds st]]
            (if (= "alive" st)
                [:tr.error [:td ds] [:td st]]
                [:tr [:td ds] [:td st]]))
(defpage "/welcome" []
         (common/layout
             "Welcome to lemon"
             (let [tair (monitor/new-tair ["10.232.12.141:5198"] "group_1")]
                 [:table.table.table-hover.table-striped
                  [:thead [:tr [:th "DataServers"] [:th "Status"]]]
                  (map post-row (monitor/get-ds-status tair))])
             ))

(defpartial post-cluster-row [{:keys [name master slave group engine]}]
            [:tr
             [:td [:a {:href (format "/monitor/%s/%s" engine name) :target "_blank"} name]]
             [:td master]
             [:td slave]
             [:td group]])

(defpartial post-cluster-table [eng]
            [:div.tab-pane.fade.in {:id (name eng)}
             [:table.table.table-hover.table-striped
              [:thead [:tr [:th "Cluster"] [:th "Master"] [:th "Slave"] [:th "Group Name"]]]
              (map post-cluster-row (clusters/clusters eng))]])


(defpage "/" []
         (common/layout
             "Welcome to Lemon"
             [:ul#mytab.nav.nav-tabs
              [:li.active [:a {:href "#mdb" :data-toggle "tab"} "mdb"]]
              [:li [:a {:href "#ldb" :data-toggle "tab"} "ldb"]]
              [:li [:a {:href "#rdb" :data-toggle "tab"} "rdb"]]]
             [:div#engines.tab-content
              (map post-cluster-table (keys clusters/clusters))]))
(defn- commify-num [n]
    (let [s (reverse (str n))]
          (loop [ss s [group remainder] (split-at 3 ss) result (list)]
                       (if (empty? ss)
                           (apply str (interpose "," (map #(apply str (reverse %)) result)))
                           (recur remainder (split-at 3 remainder)  (conj result group))))))

(defpartial post-ns-row [[area area-map]]
            (let [m (area-map area)
                  get- (area-map "getCount")
                  put- (area-map "putCount")
                  hit- (area-map "hitCount")
                  rem- (area-map "removeCount")
                  item- (area-map "itemCount")
                  data-size- (area-map "dataSize")
                  use-size- (area-map "useSize")]
                [:tr
                 [:td area]
                 [:td get-]
                 [:td hit-]
                 [:td (if (zero? get-) 0.0 (format "%.2f" (float (/ hit- get-))))]
                 [:td put-]
                 [:td rem-]
                 [:td (commify-num item-)]
                 [:td (commify-num data-size-)]
                 [:td (commify-num use-size-)]]))
(defpage [:get "/monitor/:eng/:cluster-name"] {:keys [eng cluster-name]}
         (defn- select-filter [{name :name}] (= name cluster-name))
         (let [config (first (filter select-filter (clusters/clusters (keyword eng))))
               {master :master slave :slave group :group} config
               tair (monitor/new-tair [master slave] group)
               ns-st-map (monitor/get-status-of-all-ns tair)]
             (common/layout
                 "Area Statistics"
                 [:table.table.table-hover.table-striped
                  [:thead
                   [:tr
                    [:th "Area"]
                    [:th "get/s"]
                    [:th "hit/s"]
                    [:th "hit-rate"]
                    [:th "put/s"]
                    [:th "remove/s"]
                    [:th "item-count"]
                    [:th "data-size"]
                    [:th "use-size"]]]
                  (map post-ns-row ns-st-map)])))

