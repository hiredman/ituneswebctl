(ns ituneswebctl.core
  (:use [ring.adapter.jetty :only [run-jetty]]
        [ring.middleware.params :only [wrap-params]]
        [hiccup.core]
        [clojure.contrib.shell])
  (:gen-class))

(defn itunes [action]
  (sh  "/usr/bin/osascript" "-e"
       (format "tell app \"iTunes\" to %s" action)))

(defn play []
  (itunes "play"))
(defn pause []
  (itunes "pause"))
(defn playpause []
  (itunes "playpause"))
(defn next′ []
  (itunes "next track"))
(defn previous []
  (itunes "previous track"))

(defn itunesctl [req]
  (case (get (:query-params req) "action" "")
        "play" (play)
        "pause" (pause)
        "playpause" (playpause)
        "next" (next′)
        "previous" (previous)
        "" nil)
  {:status 200
   :body
   (html
    [:html
     [:body
      [:ul
       (for [i [:play :pause :playpause :next :previous]]
         [:li {:style "font-size:4em;"} [:a {:href (format "?action=%s" (name i))} (name i)]])]]])})

(defn -main [& args]
  (run-jetty (wrap-params #'itunesctl) {:port (read-string (first args))}))
