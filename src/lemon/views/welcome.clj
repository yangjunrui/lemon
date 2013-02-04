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

(defpartial header-layout [& content]
            [:div.navbar.navbar-inverse.navbar-fixed-top
             [:div.navbar-inner
              [:div.container
               [:a.btn.btn-navbar {:data-toggle "collapse" :data-target ".nav-collapse"}
                [:span.icon-bar]
                [:span.icon-bar]
                [:span.icon-bar]]
               [:a.brand {:href "/"} "Lemon"]
                content]]])
(defpage "/" []
         (common/layout
             "Welcome to Lemon"
             (header-layout
               [:div.nav-collapse.collapse
                 [:ul.nav
                  [:li.active [:a {:href "/"} "Home"]]]])
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
(defpage [:get "/monitor/:eng/:cluster-name/dataserver"] {:keys [eng cluster-name]}
         (let [tair (monitor/get-tair eng cluster-name)
               ds-st-map (monitor/get-status-of-all-ds tair)]
             (common/ds-layout
                 "DataServer Statistics"
                 (header-layout
                     [:div.nav-collapse.collapse
                     [:ul.nav
                      [:li [:a {:href "/"} "Home"]]
                      [:li.active [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                      [:li [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                      [:li [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                      ]])
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
(defpage [:get "/monitor/:eng/:cluster-name/area"] {:keys [eng cluster-name]}
             (common/ns-layout
                 "Area Statistics"
                 (header-layout
                    [:div.nav-collapse.collapse
                     [:ul.nav
                      [:li [:a {:href "/"} "Home"]]
                      [:li [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                      [:li.active [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                      [:li [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                      ]]
                    [:form.navbar-search.pull-right {:onsubmit "return false"}
                     [:input.span1.search-query {:autofocus "autofocus" :placeholder "area" :id "area-filter" :oninput "update_ns()"}]])
                 [:div.container
                  [:div.hide [:input {:id "eng-type" :value eng}] [:input {:id "cluster-name" :value cluster-name}]]
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
                   "<tr></tr>"]]))
(defpartial guru-fields []
            [:div.control-group
             [:div.control-label (label "qstr" "Query: ")]
             [:div.controls (text-field
                                {:class "input-xxlarge" :required "required"
                                 :list "qstrs" :autofocus "autofocus" :pattern "\\(.*\\)"
                                 :placeholder "Enter Clojure query sentence here, e.g. (.get tair 0 \"key\")"} "qstr" )
              [:datalist {:id "qstrs"}
               [:option {:value "(.get tair 0 \"key\")"}]
               [:option {:value "(.put tair 0 \"key\" \"value\")"}]
               [:option {:value "(.delete tair \"key\")"}]
               [:option {:value "(.incr tair 0 \"key\" 1 0 0)"}]
               [:option {:value "(.decr tair 0 \"key\" 1 0 0)"}]
               [:option {:value "(.prefixGet tair 0 \"pkey\" \"skey\")"}]
               [:option {:value "(.prefixPut tair 0 \"pkey\" \"skey\" \"value\")"}]
               [:option {:value "(.prefixDelete tair 0 \"pkey\" \"skey\")"}]
               [:option {:value "(.prefixIncr tair 0 \"pkey\" \"skey\" 1 0 0)"}]
               [:option {:value "(.lazyRemoveArea tair 0)"}]
               ]]]
            [:div.control-group
             [:div.control-label (label "guru-result" "Result: ")]
             [:div.controls (text-area {:rows 8 :class "field span6"
                                        :placeholder "Result would be displayed here, if no exception occurs."} "guru-result")]])
(defpartial dummy-fields []
            [:div.control-group
             [:div.control-label (label "area" "Area: ")]
             [:div.controls [:input
                                {:class "input-large" :type "text"
                                 :pattern "([0-9])|([1-9][0-9])|([1-9][0-9][0-9])|(10[0-1][0-9])|(102[0-3])"
                                 :title "0-1023" :required "required"
                                 :placeholder "area" :autofocus "autofocus" :id "area"}]]]
            [:div.control-group
             [:div.control-label (label "key" "Key: ")]
             [:div.controls (text-field
                                {:class "input-xxlarge"
                                 :pattern (apply str (interpose "|"
                                                                ["(\".*\")"
                                                                 "([0-9]{1,})"
                                                                 "(\\([.a-zA-Z]{1,} [0-9]{1,}\\))"
                                                                 "(\\([.a-zA-Z]{1,} \".*\"\\))"]))
                                 :required "required"
                                 :placeholder "e.g. \"key\", 123, (short 123), (long 123)"} "key" )]]
            [:div.control-group
             [:div.control-label (label "dummy-result" "Result: ")]
             [:div.controls (text-area {:rows 8 :class "field span6"
                                        :placeholder "Result would be displayed here, if no exception occurs."} "dummy-result")]]
            [:div.control-group
             [:div.controls (submit-button {:id "guru-submit"} "Query")]])
(defpage [:get "/monitor/:eng/:cluster-name/query"] {:keys [eng cluster-name] :as query}
         (common/query-layout
             (str "Query in " cluster-name)
             (header-layout
                [:div.nav-collapse.collapse
                 [:ul.nav
                  [:li [:a {:href "/"} "Home"]]
                  [:li [:a {:href (format "/monitor/%s/%s/dataserver" eng cluster-name)} "DataServer"]]
                  [:li [:a {:href (format "/monitor/%s/%s/area" eng cluster-name)} "Area"]]
                  [:li.active [:a {:href (format "/monitor/%s/%s/query" eng cluster-name)} "Query"]]
                  ]])
             [:div.container
              [:div.hide [:input {:id "eng-type" :value eng}] [:input {:id "cluster-name" :value cluster-name}]]
              [:div.tabbable.tabs-left
               [:ul.nav.nav-tabs
                [:li
                 [:a {:href "#guru" :data-toggle "tab"} "Guru"]]
                [:li.active
                 [:a {:href "#dummy" :data-toggle "tab"} "Dummy"]]
                [:li
                 [:a {:href "#help" :data-toggle "tab"} "Help"]]]
               [:div.tab-content
                [:div.tab-pane.fade.in {:id "guru"}
                 (form-to {:class "form-horizontal" :id "guru-form"}
                          [:get "#"]
                          (guru-fields)
                          )]
                [:div.tab-pane.fade.in.active {:id "dummy"}
                 (form-to {:class "form-horizontal" :id "dummy-form"}
                          [:get "#"]
                          (dummy-fields))]
                [:div.tab-pane.fade.in {:id "help"}
                 [:p "You could only perform `get' request in the dummy mode, while the type of key being constrained, such as String, Integer, Long, Double, etc."]
                 [:p "When in guru mode, you could launch any request that `tair-client' supports."]
                 ]]]]))

;;; restful-related
(defpage [:get "/monitor/query"] {:keys [eng cluster-name qstr] :as query}
         (def tair (monitor/get-tair eng cluster-name))
         (do
             (use 'lemon.views.welcome)
             (str (eval (read-string qstr)))))
(defpage [:get "/monitor/ns-json"] {:keys [eng cluster-name start-ns end-ns]}
         (json/generate-string (monitor/get-status-of-ns-by-range
                  (monitor/get-tair eng cluster-name)
                  (Integer. start-ns)
                  (Integer. end-ns))))
