(ns tv-todo.db
  (require [clojure.java.jdbc :as sql]))

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
                       [:completed :boolean]
                       [:list_id :int])))

(defn create-lists-table []
  (sql/db-do-commands db
                      (sql/create-table-ddl
                       :lists
                       [:id :serial "PRIMARY KEY"]
                       [:name :text])))

(defn drop-table [table-name]
  (sql/db-do-commands db
                      (sql/drop-table-ddl (keyword table-name))))

(defn query-all [table-name]
  (sql/query db [(str "select * from " table-name)]))

(defn create-todo
  ([body] (sql/insert! db :todos {:body body 
                                  :completed false
                                  :list_id nil}))
  ([body list-id] (sql/insert! db :todos {:body body 
                                          :completed false 
                                          :list_id list-id}))
  ([body list-id completed] (sql/insert!
                             db :todos {:body body 
                                        :completed completed 
                                        :list_id list-id})))

(defn create-list [name]
  (sql/insert! db :lists {:name name}))

(defn count-table [table-name]
  (:count
   (first (sql/query db [(str "select count(*) from " table-name)]))))

(defn find-by-id [table-name id]
  (first (sql/query db [(str "select * from "
                             table-name
                             " where id = ?") id])))

(defn delete-by-id [table-name id]
  (sql/delete! db (keyword table-name) ["id = ?" id]))

(defn flip-completed [id]
  (let [old-value (:completed (find-by-id "todos" id))]
    (sql/update! db :todos {:completed (not old-value)} ["id = ?" id])))

(defn todos-for-list [list-id]
  (sql/query db ["select * from todos where list_id = ?" list-id]))

(defn add-dummy-data []
  (do (create-list "first-list") 
      (create-todo "first undone" 1)
      (create-todo "second undone" 1)
      (create-todo "first done" 1 true)
      (create-todo "second done" 1 true)))

(defn migrate []
  (do
    (if (not (table-exists? "todos")) (create-todos-table))
    (if (not (table-exists? "lists")) (create-lists-table))
    (if (and (= 0 (count-table "todos"))
             (= 0 (count-table "lists")))
      (add-dummy-data))))

(defn reset-db []
  (do
    (if (table-exists? "todos") (drop-table "todos"))
    (if (table-exists? "lists") (drop-table "lists"))
    (migrate)))
