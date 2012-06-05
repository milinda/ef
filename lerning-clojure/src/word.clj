(in-ns 'word)
(clojure.core/refer 'clojure.core)

(def token-regex #"\w+")

(def stop-words
  #{"a" "in" "that" "for" "was" "is" "it" "the" "of" "and" "to" "he"})

(defn to-lower-case [token-string]
  (.toLowerCase token-string))

(defn tokenize-string
  ([input-string]
   (map to-lower-case (re-seq token-regex input-string)))
  ([input-string stop-words?]
   (filter (complement stop-words?) (tokenize-string input-string))))

(defn tokenize
  ([filename]
   (tokenize-string (slurp filename)))
  ([filename stop-words?]
   (tokenize-string (slurp filename) stop-words?)))



