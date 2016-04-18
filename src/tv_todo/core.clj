(ns tv-todo.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jet]
            [ring.util.codec :as codec]
            [ring.util.response :as r]
            [tv-todo.views :as v]
            [tv-todo.db :as db]))

(defn parse-request [request key]
  (get (codec/form-decode (slurp (:body request))) key))

(defn handle-todos [request]
  (case (:request-method request)
    :get (v/layout (v/todos-index))
    :post (do (if-let [body (parse-request request "todo")]
                (db/create-todo body))
              (r/redirect "/"))))

(defn handler [request]
  (case (:uri request)
    "/" (handle-todos request)
    "/update-todo" (do (if-let [id (read-string (parse-request request "id"))]
                          (db/flip-completed id))
                       (r/redirect "/"))
    "/destroy-todo" (do (if-let [id (read-string (parse-request request "id"))]
                          (db/delete-by-id "todos" id))
                        (r/redirect "/"))
    "/about" (v/layout v/about)
    "/favicon.ico" (v/bad-news "")))

(defn -main [] (do
                 (db/migrate) 
                 (jet/run-jetty handler {:port 8000})))
