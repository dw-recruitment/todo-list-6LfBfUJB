(ns tv-todo.core
  (:gen-class)
  (:use [hiccup.page :only (html5 include-css)])
  (:require [ring.adapter.jetty :as jet]
            [tv-todo.views :as v]
            [tv-todo.db :as db]))

(defn handler [request]
  (case (:uri request)
    "/" (v/layout (v/todos-index))
    "/about" (v/layout v/about)))

(defn -main [] (do
                 (db/migrate) 
                 (jet/run-jetty handler {:port 8000})))
