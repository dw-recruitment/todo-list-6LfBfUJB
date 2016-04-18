(ns tv-todo.core
  (:gen-class)
  (:use [hiccup.page :only (html5 include-css)])
  (:require [ring.adapter.jetty :as jet]
            [hiccup.core :as hic]))

(defn layout [view]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (hic/html
          [:head
           [:title "TODOS"]
           [:link {:rel "stylesheet"
                   :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
                   :integrity "sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
                   :crossorigin "anonymous"}]
           [:script {:src "https://code.jquery.com/jquery-2.2.3.min.js"
                     :integrity "sha256-a23g1Nt4dtEYOj7bR+vTu7+T8VP13humZFBJNIYoEJo="
                     :crossorigin "anonymous"}]
           [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
                     :integrity "sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
                     :crossorigin "anonymous"}]]
          [:body
           [:div {:class "container"}
            [:div {:class "text-center"}
             [:h1 "My Todos"]]
            [:hr]
            [:div {:class "view-content"} view]
            ]])})



(def under-construction
  [:img {:class "img-responsive center-block"
         :src "http://www.animatedimages.org/data/media/1432/animated-pikachu-image-0018.gif"
         :style "height: 400px;"}])

(defn handler [request]
  (case (:uri request)
    "/" (layout under-construction)))

(defn -main [] (jet/run-jetty handler {:port 8000}))
