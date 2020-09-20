(ns open-circuitry.test-helpers
  (:require
    [clojure.java.io :as io]
    [dali.io]
    [net.cgrand.enlive-html :as enlive]
    [open-circuitry.svg :refer [dali-rendering]]))
    

(defn svg-element [board selector]
  (-> board
      dali-rendering
      dali.io/render-svg-string
      (.getBytes)
      io/input-stream
      enlive/xml-parser
      (enlive/select selector)
      first))
