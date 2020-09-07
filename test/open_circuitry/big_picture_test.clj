(ns open-circuitry.big-picture-test
  (:require
   [clojure.test :refer [deftest is]]
   [open-circuitry.svg :refer [dali-rendering]]
   [dali.io :refer [render-svg-string]]))

(deftest dali-can-render-board
  (let [exception-thrown?
        (try
          (render-svg-string (dali-rendering [:open-circuitry/board
                                              {:width 50,
                                               :height 100}]))
          false
          (catch Exception _
            true))]
    (is (not exception-thrown?))))
