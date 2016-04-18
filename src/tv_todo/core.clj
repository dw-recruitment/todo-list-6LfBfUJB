(ns tv-todo.core
  (:gen-class)
  (:use [hiccup.page :only (html5 include-css)])
  (:require [ring.adapter.jetty :as jet]
            [hiccup.core :as hic]
            [tv-todo.views :as v]
            [clojure.java.jdbc :as sql]))

(def db "postgresql://localhost:5432/todo")

(defn table-exists? [table-name]
  (-> (sql/query 
       db 
       [(str "select count(*) from information_schema.tables where table_name='"
             table-name
             "'")])
      first :count pos?))

(defn create-todos-table []
  (sql/db-do-commands db
                      (sql/create-table-ddl
                       :todos
                       [:id :serial "PRIMARY KEY"]
                       [:body :text]
                       [:completed :boolean])))

(defn drop-table [table-name]
  (sql/db-do-commands db
                      (sql/drop-table-ddl (keyword table-name))))

(defn query-all [table-name]
  (sql/query db [(str "select * from " table-name)]))

(defn create-todo
  ([body] (sql/insert! db :todos {:body body :completed false}))
  ([body completed] (sql/insert! db :todos {:body body :completed completed})))

(defn count-table [table-name]
  (:count
   (first (sql/query db [(str "select count(*) from " table-name)]))))

(defn handler [request]
  (case (:uri request)
    "/" (v/layout v/under-construction)
    "/about" (v/layout v/about)))

(defn add-dummy-data []
  (do (create-todo "first undone")
      (create-todo "second undone")
      (create-todo "first done" true)
      (create-todo "second done" true)))

(defn migrate []
  (do
    (if (not (table-exists? "todos")) (create-todos-table))
    (if (= 0 (count-table "todos")) (add-dummy-data))))

(defn -main [] (jet/run-jetty handler {:port 8000}))
