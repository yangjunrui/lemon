(defproject lemon "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :mirrors {"tbmirror"
                      {:name "taobao mirror"
                       :url "http://mvnrepo.taobao.ali.com/mvn/repository"}}
            :repositories [
                           ["taobao mirror"
                            {:url "http://mvnrepo.taobao.ali.com/mvn/repository"
                             :checksum :ignore
                             :username "admin"
                             :password :env}]]
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [com.taobao.tair/tair-mc-client "1.0.4.8"]]
            :main lemon.server)

