(ns tv-todo.views
  (:require [hiccup.core :as hic]
            [hiccup.form :as form]
            [tv-todo.db :as db]))

(defn bad-news [body-text]
  {:status 404
   :headers {}
   :body body-text})

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

(def about
  [:div
   [:h2 "About This Project"]
   [:p "Post-Its are for the weak. Step into the future with this dedicated To Do List PLATFORM!"]])

(def todo-form
  [:div (form/form-to [:post "/"]
                      (form/label "todo" "Create a Todo")
                      [:br]
                      (form/text-field "todo")
                      (form/submit-button "Add Todo"))])

(defn striker [completed]
  (if completed 
    "text-decoration: line-through;" ""))

(defn todos-list [todos]
  [:div 
   todo-form
   [:ul
    (for [todo (db/query-all "todos")]
      [:li
       [:h4 {:style (striker (:completed todo))} (:body todo)
        (form/form-to [:post "/update-todo"]
                      (form/hidden-field "id" (:id todo))
                      (form/submit-button (if (:completed todo)
                                            "Undo" "Mark as Done")))
        (form/form-to [:post "/destroy-todo"]
                      (form/hidden-field "id" (:id todo))
                      (form/submit-button "Delete"))]])]])

(defn todos-index []
  (todos-list (db/query-all "todos")))

(defn lists-index []
  [:div
   [:ul
    (for [list (db/query-all "lists")]
      [:li
       [:a {:href (str "/lists/" (:id list))}
        [:h4 (:name list)]]])]])

(defn lists-show [id]
  (let [list (db/find-by-id "lists" id)
        todos (db/todos-for-list id)]
    [:div
     [:h2 (:name list)]
     (todos-list todos)]))
