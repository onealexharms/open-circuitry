(ns open-circuitry.svg)

(defn attributes [node]
  (second node))

(defn rendering
  [board]
  (when-not (:width (attributes board))
    (throw (Exception. "A board needs a :width attribute")))
  [nil {:width (:width (attributes board))}])
