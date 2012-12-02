(ns lemon.views.welcome
  (:require [lemon.views.common :as common]
            [lemon.tair.monitor :as monitor]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial post-item [[ds st]]
            [:li (str ds ": " st)])
(defpartial post-row [[ds st]]
            (if (= "alive" st)
                [:tr.error [:td ds] [:td st]]
                [:tr [:td ds] [:td st]]))
(defpage "/welcome" []
         (common/layout
            (let [tair (monitor/new-tair ["10.232.12.141:5198"] "group_1")]
             [:table.table.table-hover.table-striped
              [:thread [:tr [:th "DataServers"] [:th "Status"]]]
              (map post-row (monitor/get-ds-status tair))])))
