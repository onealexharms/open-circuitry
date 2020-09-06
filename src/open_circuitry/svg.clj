(ns open-circuitry.svg)

(defn attributes [node]
  (second node))

(defn rendering
  [board]
  (let [attributes (attributes board)]
    (if (:width attributes)
      [nil {:width (:width attributes)}]
      (throw (Exception. "A board needs a :width attribute")))))
