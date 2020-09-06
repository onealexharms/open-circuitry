(ns open-circuitry.svg)

(defn rendering
  [board]
  (let [attributes (second board)]
    (if (:width attributes)
      [nil {:width (:width attributes)}]
      (throw (Exception. "A board needs a :width attribute")))))
