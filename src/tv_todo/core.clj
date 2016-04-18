(ns tv-todo.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jet]))

(defn under-construction []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "<h1>Todos List</h1>"
              "<hr>"
              "<img src='http://www.animatedimages.org/data/media/1432/animated-pikachu-image-0018.gif' style='height: 400px;'>")})

(defn handler [request]
  (case (:uri request)
    "/" (under-construction)))

(defn -main [] (jet/run-jetty handler {:port 8000}))
