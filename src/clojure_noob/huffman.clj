(ns clojure-noob.huffman
  (:use clojure.data.priority-map))

(defn huffman-combine
  [nodes0 key-combine]
  (let [[key0 val0] (peek nodes0)
        nodes1 (pop nodes0)
        [key1 val1] (peek nodes1)
        nodes2 (pop nodes1)
        new-key (key-combine key0 key1)]
    (assoc nodes2 new-key (+ val0 val1))))

(defn huffman-key-combine [left right] {:0 left :1 right})

(defn huffman-build
  [symbol-freq-map]
  (loop [trees (into (priority-map) symbol-freq-map)]
    (if (< (count trees) 2)
      ((first trees) 0)
      (recur (huffman-combine trees huffman-key-combine)))))


(comment
  "if we could define different function clauses based on destructuring and not just arity, we'd do the following (and it would be very elegant):"
(defn huffman-bitseq-grow
  ([bit [bits sym]] [(conj bits bit) sym])
  ([bit sym] [(list bit) sym]))
  )

(defn huffman-bitseq-grow
  [bit vec-or-sym]
  (if (vector? vec-or-sym)
    (let [[bits sym] vec-or-sym]
      [(conj bits bit) sym])
    [(list bit) vec-or-sym]))
 
(comment "old and busted"
(defn predicate-split
  [predicate stuff]
  (let [reduction (fn [result-map [satisfies? item]]
                    (assoc result-map
                           satisfies?
                           (conj (result-map satisfies? []) item)))
        reduced (reduce reduction {} (map #(vector (predicate %) %) stuff))]
    [(reduced false) (reduced true)]))
         )

(defn predicate-split
  [predicate stuff]
  (let [{nonmatching false matching true} (group-by predicate stuff)]
    (list nonmatching matching)))

(defn leaves
  [m]
  (let [[immediate-leaves branches] (predicate-split map? (vals m))]
    (apply concat (cons immediate-leaves (map leaves branches)))
  ))

(comment "oy"
(defn leaves
  [map]
  (let [[immed-leaves branches] (predicate-split map? (vals map))]
    (apply concat (conj (map leaves branches) immed-leaves))))

(defn branches
  [m]
  (predicate-split map? (vals m)))
         )

(defn combine-path-and-leaf
  [step [rest-of-path leaf]]
  [(conj step rest-of-path) leaf])

(defn paths
  [tree]
  (let [leaf-to-path (fn [[key leaf]] [(list key) leaf])
        branch-to-path (fn [key [path leaf]] [(conj path key) leaf])
        expand-branch (fn [[key subtree]] (map #(branch-to-path key %) (paths subtree)))
        [leaves branches] (predicate-split #(map? (% 1)) tree)]

       (concat (mapcat expand-branch branches) (map leaf-to-path leaves))
  ))


