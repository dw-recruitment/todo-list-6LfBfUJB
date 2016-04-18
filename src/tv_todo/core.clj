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
    :post (let [req (codec/form-decode (slurp (:body request)))
                body (get req "todo")
                list-id (read-string (get req "list-id"))]
            (db/create-todo body list-id)
            (r/redirect (str "/lists/" list-id)))
    (v/bad-news "Something Done Goofed")))

(defn handle-lists [request]
  (case (:request-method request)
    :get (v/layout (v/lists-index))
    :post (if-let [name (parse-request request "list")]
            (let [list-id (:id (first (db/create-list name)))]
              (r/redirect (str "/lists/" list-id))))))

(defn uri-check [request]
  (let [uri (:uri request)]
    (if (and (= "/lists/" (subs uri 0 7))
             (> (count uri) 7))
      (do (if-let [id (read-string (subs uri 7))]
            (v/layout (v/lists-show id))))
      (v/bad-news "Are You Lost?"))))

(defn handler [request]
  (case (:uri request)
    "/" (handle-lists request)
    "/update-todo" (let [id (read-string (parse-request request "id"))
                         todo (db/find-by-id "todos" id)]
                     (db/flip-completed id)
                     (r/redirect (str "/lists/" (:list_id todo))))
    "/destroy-todo" (do (let [id (read-string (parse-request request "id"))
                              todo (db/find-by-id "todos" id)
                              list-id (:list_id todo)]
                          (db/delete-by-id "todos" id)
                          (println list-id)
                          (r/redirect (str "/lists/" list-id))))
    "/todos" (handle-todos request)
    "/about" (v/layout v/about)
    "/favicon.ico" (v/bad-news "")
    (uri-check request)))

(defn -main [] (do
                 (db/reset-db) 
                 (jet/run-jetty handler {:port 8000})))
