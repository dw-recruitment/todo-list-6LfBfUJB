(ns tv-todo.core
  (:gen-class)
  (:use [hiccup.page :only (html5 include-css)])
  (:require [ring.adapter.jetty :as jet]
            [hiccup.core :as hic]
            [tv-todo.views :as v]))

(defn handler [request]
  (case (:uri request)
    "/" (v/layout v/under-construction)
    "/about" (v/layout v/about)))

(defn -main [] (jet/run-jetty handler {:port 8000}))
