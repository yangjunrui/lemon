(ns lemon.tair.clusters)
(def clusters {
               :mdb [
                     {:name "comm-daily"
                      :master "10.232.12.141:5198"
                      :slave "10.232.12.142:5198"
                      :group "group_1"
                      :engine "mdb"}
                     {:name "mdbcomm-daily"
                      :master "10.235.144.191:5198"
                      :slave "10.235.144.192:5198"
                      :group "group_mdbcomm"
                      :engine "mdb"}
                     {:name "mm-daily"
                      :master "10.232.12.141:5198"
                      :slave "10.232.12.142:5198"
                      :group "group_1"
                      :engine "mdb"}
                     {:name "mcomm-daily"
                      :master "10.232.12.141:5198"
                      :slave "10.232.12.142:5198"
                      :group "group_1"
                      :engine "mdb"}
                     {:name "ju-mtair-daily"
                      :master "10.235.144.116:5198"
                      :slave "10.235.144.117:5198"
                      :group "group_ju"
                      :engine "mdb"}]
               :ldb [
                     {:name "ldbcommon-daily"
                      :master "10.235.145.80:5198"
                      :slave "10.235.145.82:5198"
                      :group "group_ldbcommon"
                      :engine "ldb"}
                     {:name "icbucomm-ldb-daily"
                      :master "10.232.16.24:5198"
                      :slave "10.232.16.24:5198"
                      :group "group_1"
                      :engine "ldb"}]
               :rdb [
                     {:name "etaordb-daily"
                      :master "10.232.129.90:5198"
                      :slave "10.232.129.90:5198"
                      :group "group_1"
                      :engine "rdb"}
                     {:name "icrdb-daily"
                      :master "10.232.102.36:5198"
                      :slave "10.232.102.36:5198"
                      :group "group_1"
                      :engine "rdb"}]
               :tdbm [
                      {:name "tdbm-daily"
                       :master "10.232.12.140:5198"
                       :slave "10.232.15.122:5198"
                       :group "group_1"
                       :engine "tdbm"}]})
