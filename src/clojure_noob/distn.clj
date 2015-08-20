(ns clojure-noob.distn
  )

(defn to-pdf
  [counts]
  (let [invalid #(or (not (number? %)) (< % 0))
        included (fn [[_ x]] (> x 0))
        sum (reduce + 0 (vals counts))]
    (when (not (empty? (filter invalid (vals counts))))
          (throw (IllegalArgumentException. "relative frequencies must be numbers >= 0")))
    (into {} (map (fn [[k v]] [k (/ v sum)]) (filter included counts)))))

(defn pdf-to-cdf
  [pdf]
  ((reduce (fn [[cum cdf] [y py]]
              (let [cpy (+ cum py)]
                   [cpy (cons [y cpy] cdf)]))
           [0 ()]
           pdf) 1))

(defn pdf-to-icdf
  [pdf]
  (into (sorted-map) (map (fn [[a b]] [b a]) (pdf-to-cdf pdf))))

(defn sample-icdf
  ([icdf]
   (sample-icdf icdf (rand)))
  ([icdf q]
   (let [[y py] (first (subseq icdf >= q))]
        py)))

(defn count-instances [coll]
  (reduce #(assoc %1 %2 (inc (%1 %2 0))) (sorted-map) coll))

