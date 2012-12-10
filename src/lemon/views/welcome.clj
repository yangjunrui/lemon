(ns lemon.views.welcome
  (:require [lemon.views.common :as common]
            [lemon.tair.monitor :as monitor]
            [lemon.tair.clusters :as clusters]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.form]
        [hiccup.page :only [include-css include-js html5]]))

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


(defpage "/welcome" []
         (common/layout
             "Welcome to Lemon"
             [:ul#mytab.nav.nav-tabs
              [:li.active [:a {:href "#mdb" :data-toggle "tab"} "mdb"]]
              [:li [:a {:href "#ldb" :data-toggle "tab"} "ldb"]]
              [:li [:a {:href "#rdb" :data-toggle "tab"} "rdb"]]]
             [:div#engines.tab-content
              (map post-cluster-table (keys clusters/clusters))]))
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
    (->>
        n
        (str)
        (reverse)
        (partition-all 3)
        (map reverse)
        (reverse)
        (interpose ",")
        (apply concat)
        (apply str)))

(defpartial post-ds-row [[ds ds-map]]
            (let [
                  {:strs [getCount putCount hitCount removeCount evictCount itemCount dataSize useSize]}
                  ds-map]
                [:tr
                 [:td ds]
                 [:td getCount]
                 [:td hitCount]
                 [:td (if (zero? getCount) 0.0 (format "%.2f" (float (/ hitCount getCount))))]
                 [:td putCount]
                 [:td removeCount]
                 [:td (if (zero? evictCount) evictCount [:span.label.label-warning evictCount])]
                 [:td (commify-num itemCount)]
                 [:td (commify-num dataSize)]
                 [:td (commify-num (if (zero? itemCount) 0 (quot dataSize itemCount)))]
                 [:td (commify-num useSize)]]))
(defpage [:get "/monitor/:eng/:cluster-name/dataserver"] {:keys [eng cluster-name]}
         (let [tair (monitor/get-tair eng cluster-name)
               ds-st-map (monitor/get-status-of-all-ds tair)]
             (common/layout
                 "DataServer Statistics"
                 [:table.table.table-hover.table-striped.tableWithFloatingHeader.tablesorter {:id "ds-statistics"}
                  [:thead
                   [:tr
                    [:th "DataServer"]
                    [:th "get/s"]
                    [:th "hit/s"]
                    [:th "hit-rate"]
                    [:th "put/s"]
                    [:th "remove/s"]
                    [:th "evict/s"]
                    [:th "item-count"]
                    [:th "data-size"]
                    [:th "per-size"]
                    [:th "use-size"]]]
                  (map post-ds-row ds-st-map)])))
(defpartial post-ns-row [[area area-map]]
            (let [
                  {:strs [getCount putCount hitCount removeCount evictCount itemCount dataSize useSize quota]}
                  area-map]
                [:tr
                 [:td area]
                 [:td getCount]
                 [:td hitCount]
                 [:td (if (zero? getCount) 0.0 (format "%.2f" (float (/ hitCount getCount))))]
                 [:td putCount]
                 [:td removeCount]
                 [:td (if (zero? evictCount) evictCount [:span.label.label-warning evictCount])]
                 [:td (commify-num itemCount)]
                 [:td (commify-num dataSize)]
                 [:td (commify-num (if (zero? itemCount) 0 (quot dataSize itemCount)))]
                 [:td (commify-num useSize)]
                 [:td (commify-num (if (nil? quota) -1 quota))]]))
(defpage [:get "/monitor/:eng/:cluster-name/area"] {:keys [eng cluster-name]}
         (let [tair (monitor/get-tair eng cluster-name)
               ns-st-map (monitor/get-status-of-all-ns tair)]
             (common/layout
                 "Area Statistics"
                 [:table.table.table-hover.table-striped.tableWithFloatingHeader.tablesorter {:id "area-statistics"}
                  [:thead
                   [:tr
                    [:th "Area"]
                    [:th "get/s"]
                    [:th "hit/s"]
                    [:th "hit-rate"]
                    [:th "put/s"]
                    [:th "remove/s"]
                    [:th "evict/s"]
                    [:th "item-count"]
                    [:th "data-size"]
                    [:th "per-size"]
                    [:th "use-size"]
                    [:th "quota"]]]
                  (map post-ns-row ns-st-map)])))
(defpartial query-fields [{:keys [qstr result]}]
            [:div.control-group
             [:div.control-label (label "qstr" "Query: ")]
             [:div.controls (text-field {:class "input-xxlarge"} "qstr" qstr)]]
            [:div.control-group
             [:div.control-label (label "result" "Result: ")]
             [:div.controls (text-area {:rows 8 :class "field span6"} "result" result)]]
            [:div.control-group
             [:div.controls (submit-button "Query")]])
(defpage [:get "/monitor/:eng/:cluster-name/query"] {:keys [eng cluster-name] :as query}
         (def tair (monitor/get-tair eng cluster-name))
         (common/layout
             (str "Query in " cluster-name)
             (form-to {:class "form-horizontal"}
                      [:get (format "/monitor/%s/%s/query" eng cluster-name)]
                      (if (nil? (:qstr query))
                          (query-fields query)
                          (do
                              (use 'lemon.views.welcome)
                              (query-fields (assoc query :result (eval (read-string (:qstr query)))))))
                      )))
