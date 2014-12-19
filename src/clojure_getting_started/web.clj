(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [org.httpkit.client :as http]
            ))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (pr-str ["Hello" :from 'Heroku])})

(def user-to-category
    {
      "bob"  "mens-fashion-accessories"
      "jane" "womens-fashion-accessories"
    }
  )

(def base_url
  "http://search-service-production.herokuapp.com/search.json?types[]=products"
  )

(defn search_url
  ([] base_url)
  ([code] (concat (search_url) "&category_hierarchy.code=" code))
  )

(defn personalize [user]
  (let [resp1 (http/get (search_url (user-to-category user)))
      resp2 (http/get (search_url))]
    (println "Response 1's status: " (:status @resp1)) ; wait as necessary
    (println "Response 2's status: " (:status @resp2)))
    (concat "blah")
  )

(defroutes app
  (GET "/search" {{user :user} :params}
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (personalize user)}
       )
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

(use 'ring.server.standalone)
  (serve (site #'app))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
