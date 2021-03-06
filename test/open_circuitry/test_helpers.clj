(ns open-circuitry.test-helpers
  (:require
    [clojure.java.io :as io]
    [clojure.test]
    [dali.io]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.svg :refer [dali-rendering]]))
    
(defmacro exists [thing]
  `(clojure.test/is ~thing))

(defn elements-by-selector [selector board]
  (-> board
      dali-rendering
      dali.io/render-svg-string
      (.getBytes)
      io/input-stream
      enlive/xml-parser
      (enlive/select selector)))

(defn element-by-selector [selector board]
  (first (elements-by-selector selector board)))

(defn attributes-by-selector [selector board]
  (:attrs (element-by-selector selector board)))

(defn toolpaths-with-id [toolpath-id board]
  (elements-by-selector [[:g (enlive/attr=  :id toolpath-id)]] board))

(defn location-selector [x y]
  [[(enlive/attr= :cx (str x) (enlive/attr= :cy (str y)))]])

