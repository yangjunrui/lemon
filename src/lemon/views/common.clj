(ns lemon.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css html5]]))

(defpartial layout [title & content]
            (html5
              [:head
               [:title title]
               (include-css "/css/bootstrap.min.css")
               (include-css "/css/bootstrap-responsive.min.css")
               (include-js "/js/jquery-1.8.3.min.js")
               (include-js "/js/bootstrap.min.js")
               (include-js "/js/jquery.tablesorter.js")
               (include-js "/js/lemon.js")]
              [:body
               [:div#wrapper
                content]]))

(defpartial index-layout [& content]
            (html5
                [:head
                 [:title "Welcome to Lemon"]
                 (include-css "/css/bootstrap.min.css")
                 (include-css "/css/bootstrap-responsive.min.css")
                 (include-js "/js/jquery.tablesorter.js")
                 (include-js "/js/bootstrap.min.js")]
                [:body]))
