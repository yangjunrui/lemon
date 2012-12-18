(ns lemon.views.welcome
  (:require [lemon.views.common :as common]
            [lemon.tair.monitor :as monitor]
            [lemon.tair.clusters :as clusters]
            [cheshire.core :as json]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage defpartial render]]
        [hiccup.form]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial post-cluster-row [{:keys [name master slave group engine]}]
            [:tr
             [:td [:a {:href (format "/monitor/%s/%s/dataserver" engine name) :target "_blank"} name]]
             [:td master]
             [:td slave]
             [:td group]])

(defpartial post-cluster-table [eng]
            [:div.tab-pane.fade.in {:id (name eng)}
             [:table.table.table-hover.table-striped.table-condensed
              [:thead [:tr [:th "Cluster"] [:th "Master"] [:th "Slave"] [:th "Group Name"]]]
              (map post-cluster-row (clusters/clusters eng))]])

(defpage "/" []
         (common/layout
             "Welcome to Lemon"
             [:div.navbar.navbar-inverse.navbar-fixed-top
              [:div.navbar-inner
               [:div.container
                [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
                 [:span.icon-bar]
                 [:span.icon-bar]
                 [:span.icon-bar]]
                [:a.brand {:href "/"} "Lemon"]
                [:div.nav-collapse.collapse
                 [:ul.nav
                  [:li.active [:a {:href "/"} "Home"]]]]]]]
             [:div.container
              [:div
               [:ul#mytab.nav.nav-tabs
                [:li.active [:a {:href "#mdb" :data-toggle "tab"} "mdb"]]
                [:li [:a {:href "#ldb" :data-toggle "tab"} "ldb"]]
                [:li [:a {:href "#rdb" :data-toggle "tab"} "rdb"]]]]
              [:div#engines.tab-content
               (map post-cluster-table (keys clusters/clusters))]]))
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
(defpage [:get "/monitor/:eng/:cluster-name"] {:keys [eng cluster-name] :as query}
         (render [:get (format "/monitor/%s/%s/dataserver" eng cluster-name)] query))
(defpage [:get "/monitor/:eng/:cluster-name/dataserver"] {:keys [eng cluster-name]}
         (let [tair (monitor/get-tair eng cluster-name)
               ds-st-map (monitor/get-status-of-all-ds tair)]
             (common/layout
                 "DataServer Statistics"
                 [:div.navbar.navbar-inverse.navbar-fixed-top
                  [:div.navbar-inner
                   [:div.container
                    [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
                     [:span.icon-bar]
                     [:span.icon-bar]
                     [:span.icon-bar]]
                    [:a.brand {:href "/"} "Lemon"]
                    [:div.nav-collapse.collapse
                     [:ul.nav
                      [:li [:a {:href "/"} "Home"]]
                      [:li.active [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                      [:li [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                      [:li [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                      ]]]]]
                 [:div.container
                  [:table.table.table-hover.table-striped.table-condensed.tableWithFloatingHeader.tablesorter {:id "ds-statistics"}
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
                   (map post-ds-row ds-st-map)]])))
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
                 [:td (commify-num (if (or (nil? itemCount) (zero? itemCount)) 0 (quot dataSize itemCount)))]
                 [:td (commify-num useSize)]
                 [:td (commify-num (if (nil? quota) -1 quota))]]))
(defpage [:get "/monitor/:eng/:cluster-name/area"] {:keys [eng cluster-name]}
         (let [tair (monitor/get-tair eng cluster-name)
               ns-st-map (monitor/get-status-of-all-ns tair)]
             (common/layout
                 "Area Statistics"
                 [:div.navbar.navbar-inverse.navbar-fixed-top
                  [:div.navbar-inner
                   [:div.container
                    [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
                     [:span.icon-bar]
                     [:span.icon-bar]
                     [:span.icon-bar]]
                    [:a.brand {:href "/"} "Lemon"]
                    [:div.nav-collapse.collapse
                     [:ul.nav
                      [:li [:a {:href "/"} "Home"]]
                      [:li [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                      [:li.active [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                      [:li [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                      ]]]]]
                 [:div.container
                  [:table.table.table-hover.table-striped.table-condensed.tableWithFloatingHeader.tablesorter {:id "area-statistics"}
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
                   [:div {:type "text" :style "position: fixed; left: 5px; top: 45px; width: 100px;"}
                    (text-field {:class "input-mini" :placeholder "area"} "area-filter")]
                   "<tr></tr>"]])))
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
             [:div.navbar.navbar-inverse.navbar-fixed-top
              [:div.navbar-inner
               [:div.container
                [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
                 [:span.icon-bar]
                 [:span.icon-bar]
                 [:span.icon-bar]]
                [:a.brand {:href "/"} "Lemon"]
                [:div.nav-collapse.collapse
                 [:ul.nav
                  [:li [:a {:href "/"} "Home"]]
                  [:li [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                  [:li [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                  [:li.active [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                  ]]]]]
             [:div.container
              (form-to {:class "form-horizontal"}
                       [:get (format "/monitor/%s/%s/query" eng cluster-name)]
                       (if (nil? (:qstr query))
                           (query-fields query)
                           (do
                               (use 'lemon.views.welcome)
                               (query-fields (assoc query :result (eval (read-string (:qstr query)))))))
                       )]))
(defpage [:get "/monitor/area-json"] {:keys [eng cluster-name start-ns end-ns]}
         (json/generate-string (monitor/get-status-of-ns-by-range
                  (monitor/get-tair eng cluster-name)
                  (Integer. start-ns)
                  (Integer. end-ns))))
