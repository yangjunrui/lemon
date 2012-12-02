(ns lemon.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-js include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "lemon"]
               (include-css "/css/bootstrap.min.css")
               (include-css "/css/bootstrap-responsive.min.css")
               (include-js "/js/bootstrap.min.js")]
              [:body
               [:div#wrapper
                content]]))
