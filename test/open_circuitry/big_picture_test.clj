(ns open-circuitry.big-picture-test
  (:require
   [clojure.test :refer [deftest is]]
   [open-circuitry.svg :refer [rendered-board]]
   [dali.io :refer [render-svg-string]]))

(deftest dali-can-render-board
  (let [exception-thrown?
        (try
          (render-svg-string (rendered-board [:open-circuitry/board
                                              {:width 50,
                                               :height 100}]))
          false
          (catch Exception _
            true))]
    (is (not exception-thrown?))))
