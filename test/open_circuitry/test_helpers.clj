(ns open-circuitry.test-helpers
  (:require
    [clojure.java.io :as io]
    [dali.io]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.svg :refer [dali-rendering]]))
    

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

(defn svg-attributes [board selector]
  (:attrs (element-by-selector selector board)))
