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

(defn personalize [input]
  (let [resp1 (http/get "http://http-kit.org/")
      resp2 (http/get "http://clojure.org/")]
    (println "Response 1's status: " (:status @resp1)) ; wait as necessary
    (println "Response 2's status: " (:status @resp2)))
    (identity input)
  )

(defroutes app
  (GET "/search" {{input :input} :params}
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (personalize input)}
       )
  (GET "/" []
       (splash))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
