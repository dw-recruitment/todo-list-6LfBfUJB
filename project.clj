(defproject tv-todo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]]
  :main ^:skip-aot tv-todo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
